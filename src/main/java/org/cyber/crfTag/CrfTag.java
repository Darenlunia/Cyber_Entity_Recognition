package org.cyber.crfTag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.cyber.dictTag.PreEntityExtract;
import org.cyber.evaluation.TestCrfLearn;
import org.cyber.evaluation.TestCrfTest;
import org.cyber.zhifac.crf4j.CrfTest;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;


public class CrfTag {
	
	/**
	 * 得到训练文档newPath(查看正确后，去掉Before字样)
	 * oldPath是memmTag的数据格式
	 * 将memm的训练数据进行格式转换，从word/pos[/t]tag转换为word[/t]pos[/t]tag (“[]”代表/t是制表符)
	 * @throws IOException
	 */
	public void before() throws IOException{
		String newPath= System.getProperty("user.dir") + "\\target\\classes\\example\\basenp\\trainBefore.data";
		BufferedWriter out= new BufferedWriter(new FileWriter(new File(newPath)));

		String oldPath=System.getProperty("user.dir") + "\\memmConfig\\备份训练数据\\featureBeforeTransfer.dat";
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(oldPath),"UTF-8"));  
		
		String line=null;  
		while((line=br.readLine())!=null)  
		{  
			String segs[] = line.split("	");
			String[] term = segs[0].split("/");
//			System.out.println(term[0]+term[1]+segs[1]);
			out.write(term[0] +"\t"+term[1]+"\t"+segs[1]+ "\r\n");
		}
		out.close();
		br.close();
	}
	

	/**
	 * 得到测试文档newPath
	 * _将termList放入文档，变成crf对应格式的测试文档
	 * @throws IOException 
	 */
	public void before2(List<Term> termList) throws IOException{
		String newPath= System.getProperty("user.dir") + "\\target\\classes\\example\\basenp\\test.data";
		BufferedWriter out= new BufferedWriter(new FileWriter(new File(newPath)));
		String line=null;  
		for(int i=0;i<termList.size();i++){
			out.write(termList.get(i).word +"\t"+termList.get(i).nature.toString()+"\r\n");
		}
		out.close();
	}
	 /**
     * —— newest ——
     * 测试文档进行tag预测后会生成中间文件temp，直接使用temp对termList进行更新
     * @param termList
     * @param category
     * @param checkThreshold
     * @param predictFile
     * @param retrain
     * @param trainArgs
     * @param testArgs
     * @throws IOException
     */
	public List<Term> getResult4(List<Term> termList,String category, double checkThreshold, String predictFile, boolean retrain,
			String[] trainArgs, String[] testArgs) throws IOException {
//		System.out.println(termList);
		new CrfTag().before2(termList);
		File f = new File(category + ".m");
		if (retrain || !f.exists()) {
			new TestCrfLearn().testLearnModel(category, trainArgs);
		}
		assert f.exists();
		File temp = null;
		temp = File.createTempFile("crftmp-" + category + new Date().getTime(), ".out");
		temp.deleteOnExit();

		URL predictData = this.getClass().getClassLoader().getResource("example/" + category + "/" + predictFile);
		System.out.println(predictData);
		assert predictData != null;
		String[] args = { "-m", category + ".m", predictData.getPath(), "-o", temp.getAbsolutePath() };
		if (testArgs != null) {
			String[] newargs = new String[testArgs.length + args.length];
			for (int i = 0; i < testArgs.length; i++) {
				newargs[i] = testArgs[i];
			}
			for (int i = 0; i < args.length; i++) {
				newargs[testArgs.length + i] = args[i];
			}
			args = newargs;
		}
//		System.out.println(temp.length());
		CrfTest.run(args);
//		System.out.println(temp.length());

		InputStreamReader isr = new InputStreamReader(new FileInputStream(temp), "UTF-8");

		BufferedReader br = new BufferedReader(isr);
		String line = null;

		for(int i=0;i<termList.size()-1;i++){
			if((line = br.readLine()) == null) break;
			
			
			String[] toks = line.split("[\t]", -1);
			while(toks.length<3){
				line = br.readLine();
				toks = line.split("[\t]", -1);
				System.out.println(i);
				System.out.println(line);
			}
			{
				if (toks[2].equals("B-norg")) {	
					String result = toks[0];
					line = br.readLine();
					toks = line.split("[\t]", -1);
					termList.remove(i);
					while (toks[2].equals("M-norg")) {
						result += toks[0];
						line = br.readLine();
						toks = line.split("[\t]", -1);
						termList.remove(i);
					}
					if(toks[2].equals("E-norg")){
						result += toks[0];
//						termList.remove(i);
					}
					//插入一个i
					termList.set(i, new Term(result,Nature.nt));
					System.out.println("新添机构:" + result);
				}else if(toks[2].equals("S-norg")){
					String result=toks[0];
					termList.set(i, new Term(result,Nature.nt));
					System.out.println("新添机构:" + result);
				}else if (toks[2].equals("B-nthreat")) {
					String result = toks[0];
					termList.remove(i);
					line = br.readLine();
					toks = line.split("[\t]", -1);
					while (toks[2].equals("M-nthreat")) {
						result += toks[0];
						termList.remove(i);
						line = br.readLine();
						toks = line.split("[\t]", -1);
					}
					if(toks[2].equals("E-nthreat")){
						result += toks[0];
						termList.remove(i);
					}
					termList.add(i, new Term(result,Nature.create("nthreat")));
					System.out.println("新添威胁（多为病毒）:" + result);
				}else if(toks[2].equals("S-nthreat")){
					String result=toks[0];
					termList.set(i,new Term(result,Nature.create("nthreat")));
					System.out.println("新添威胁（多为病毒）:" + result);
				}else if (toks[2].equals("B-nr")) {
					String result = toks[0];
					termList.remove(i);
					line = br.readLine();
					toks = line.split("[\t]", -1);
					while (toks[2].equals("M-nr")) {
						result += toks[0];
						termList.remove(i);
						line = br.readLine();
						toks = line.split("[\t]", -1);
					}
					if(toks[2].equals("E-nr")){
						result += toks[0];
						termList.remove(i);
					}
					System.out.println("新添人名:" + result);
					termList.add(i, new Term(result,Nature.nr));
				}else if(toks[2].equals("S-nr")){
					String result=toks[0];
					termList.set(i, new Term(result,Nature.nr));
					System.out.println("新添人名:" + result);
				}else if (toks[2].equals("B-nhack")) {
					String result = toks[0];
					termList.remove(i);
					line = br.readLine();
					toks = line.split("[\t]", -1);
					while (toks[2].equals("M-nhack")) {
						result += toks[0];
						termList.remove(i);
						line = br.readLine();
						toks = line.split("[\t]", -1);
					}
					if(toks[2].equals("E-nhack")){
						result += toks[0];
						termList.remove(i);
					}
					System.out.println("新添黑客组织:" + result);
					termList.add(i, new Term(result,Nature.create("nhack")));
				}else if(toks[2].equals("S-nhack")){
					String result=toks[0];
					termList.set(i, new Term(result,Nature.create("nhack")));
					System.out.println("新添黑客组织:" + result);
				}
				else if (toks[2].equals("B-nevent")) {
					String result = toks[0];
					termList.remove(i);
					line = br.readLine();
					toks = line.split("[\t]", -1);
					while (toks[2].equals("M-nevent")) {
						result += toks[0];
						termList.remove(i);
						line = br.readLine();
						toks = line.split("[\t]", -1);
					}
					if(toks[2].equals("E-nevent")){
						result += toks[0];
						termList.remove(i);
					}
					System.out.println("新添事件名:" + result);
					termList.add(i, new Term(result,Nature.create("nevent")));
				}else if(toks[2].equals("S-nevent")){
					String result=toks[0];
					System.out.println("新添事件名:" + result);
					termList.set(i, new Term(result,Nature.create("nevent")));
				}else if (toks[2].equals("B-nreport")) {
					String result = toks[0];
					termList.remove(i);
					line = br.readLine();
					toks = line.split("[\t]", -1);
					while (toks[2].equals("M-nreport")) {
						result += toks[0];
						termList.remove(i);
						line = br.readLine();
						toks = line.split("[\t]", -1);
					}
					if(toks[2].equals("E-nreport")){
						result += toks[0];
						termList.remove(i);
					}
					System.out.println("新添报告名:" + result);
					termList.add(i, new Term(result,Nature.create("nreport")));
				}else if(toks[2].equals("S-nreport")){
					String result=toks[0];
					System.out.println("新添报告名:" + result);
					termList.set(i, new Term(result,Nature.create("nreport")));
				}else if (toks[2].equals("B-nconference")) {
					String result = toks[0];
					termList.remove(i);
					line = br.readLine();
					toks = line.split("[\t]", -1);
					while (toks[2].equals("M-nconference")) {
						result += toks[0];
						line = br.readLine();
						toks = line.split("[\t]", -1);
						termList.remove(i);
					}
					if(toks[2].equals("E-nconference")){
						result += toks[0];
						termList.remove(i);
					}
					System.out.println("新添会议名:" + result);
					termList.add(i, new Term(result,Nature.create("nconference")));
				}else if(toks[2].equals("S-nconference")){
					String result=toks[0];
					System.out.println("新添会议名:" + result);
					termList.set(i,new Term(result,Nature.create("nconference")));
				}else if (toks[2].equals("B-nproduct")) {
					String result = toks[0];
					termList.remove(i);
					line = br.readLine();
					toks = line.split("[\t]", -1);
					while (toks[2].equals("M-nproduct")) {
						result += toks[0];
						termList.remove(i);
						line = br.readLine();
						toks = line.split("[\t]", -1);
					}
					if(toks[2].equals("E-nproduct")){
						result += toks[0];
						termList.remove(i);
					}
					System.out.println("新添软件名:" + result);
					termList.add(i, new Term(result,Nature.create("nproduct")));
				}else if(toks[2].equals("S-nproduct")){
					String result=toks[0];
					System.out.println("新添软件名:" + result);
					termList.set(i, new Term(result,Nature.create("nproduct")));
				}
			}
		}
		br.close();
		return termList;
//		//下面的这些怕是要删掉了
//		Map<String, List<String>> iocs = new HashMap<String, List<String>>();
//		List<String> norgList = new ArrayList<String>();// 存储BMES组合后的结果
//		List<String> nthreatList = new ArrayList<String>();// 存储BMES组合后的结果
//		List<String> nrList = new ArrayList<String>();// 存储BMES组合后的结果
//		List<String> nhackList = new ArrayList<String>();// 存储BMES组合后的结果
//		List<String> neventList = new ArrayList<String>();// 存储BMES组合后的结果
//		List<String> nreportList = new ArrayList<String>();// 存储BMES组合后的结果
//		List<String> nconferenceList = new ArrayList<String>();// 存储BMES组合后的结果
//		iocs.put("norg", norgList);
//		iocs.put("nthreat", nthreatList);
//		iocs.put("nr", nrList);
//		iocs.put("nhack", nhackList);
//		iocs.put("nevent", neventList);
//		iocs.put("nreport", nreportList);
//		iocs.put("nconference", nconferenceList);
//  		
//		InputStreamReader isr = new InputStreamReader(new FileInputStream(temp), "UTF-8");
//		BufferedReader br = new BufferedReader(isr);
//		String line = null;
//		while ((line = br.readLine()) != null) {
//			System.out.println(line);
//			String[] toks = line.split("[\t]", -1);
//			if (toks[2].equals("B-norg")) {
//				String result =toks[0];
//				line = br.readLine();
//				while (toks[2].equals("M-norg")) {
//					result +=toks[0];
//					line = br.readLine();
//				}
//				if(toks[2].equals("E-norg")){
//					result +=toks[0];
//				}
//				norgList.add(result);
//				System.out.println("预测的实体为nvendor:" + result);
//			}else if(toks[2].equals("S-norg")){
//				String result=toks[0];
//				System.out.println("预测的实体为nvendor:" + result);
//				norgList.add(result);
//			}
//			else if (toks[2].equals("B-nthreat")) {
//				String result =toks[0];
//				line = br.readLine();
//				while (toks[2].equals("M-nthreat")) {
//					result += toks[0];
//					line = br.readLine();
//				}
//				if(toks[2].equals("E-nthreat")){
//					result += toks[0];
//				}
//				nthreatList.add(result);
//				System.out.println("预测的实体为威胁（多数为病毒）:" + result);
//			}else if(toks[2].equals("S-nthreat")){
//				String result=toks[0];
//				System.out.println("预测的实体为威胁（多数为病毒）:" + result);
//				nthreatList.add(result);
//			}
//			else if (toks[2].equals("B-nr")) {
//				String result = toks[0];
//				line = br.readLine();
//				while (toks[2].equals("M-nr")) {
//					result += toks[0];
//					line = br.readLine();
//				}
//				if(toks[2].equals("E-nr")){
//					result += toks[0];
//				}
//				nrList.add(result);
//				System.out.println("预测的实体为人名:" + result);
//			}else if(toks[2].equals("S-nr")){
//				String result=toks[0];
//				System.out.println("预测的实体为人名:" + result);
//				nrList.add(result);
//			}
//			else if (toks[2].equals("B-nhack")) {
//				String result = toks[0];
//				line = br.readLine();
//				while (toks[2].equals("M-nhack")) {
//					result +=toks[0];
//					line = br.readLine();
//				}
//				if(toks[2].equals("E-nhack")){
//					result +=toks[0];
//				}
//				nhackList.add(result);
//				System.out.println("预测的实体为黑客组织:" + result);
//			}else if(toks[2].equals("S-nhack")){
//				String result=toks[0];
//				System.out.println("预测的实体为黑客组织:" + result);
//				nhackList.add(result);
//			}
//			else if (toks[2].equals("B-nevent")) {
//				String result = toks[0];
//				line = br.readLine();
//				while (toks[2].equals("M-nevent")) {
//					result += toks[0];
//					line = br.readLine();
//				}
//				if(toks[2].equals("E-nevent")){
//					result += toks[0];
//				}
//				neventList.add(result);
//				System.out.println("预测的实体为事件名:" + result);
//			}else if(toks[2].equals("S-nevent")){
//				String result=toks[0];
//				System.out.println("预测的实体为事件名:" + result);
//				neventList.add(result);
//			}
//			else if (toks[2].equals("B-nreport")) {
//				String result =toks[0];
//				line = br.readLine();
//				while (toks[2].equals("M-nreport")) {
//					result +=toks[0];
//					line = br.readLine();
//				}
//				if(toks[2].equals("E-nreport")){
//					result += toks[0];
//					line = br.readLine();
//				}
//				nreportList.add(result);
//				System.out.println("预测的实体为报告名:" + result);
//			}else if(toks[2].equals("S-nreport")){
//				String result=toks[0];
//				System.out.println("预测的实体为报告名:" + result);
//				nreportList.add(result);
//			}else if (toks[2].equals("B-nconference")) {
//				String result = toks[0];
//				line = br.readLine();
//				while (toks[2].equals("M-nconference")) {
//					result +=toks[0];
//					line = br.readLine();
//				}
//				if(toks[2].equals("E-nconference")){
//					result += toks[0];
//				}
//				nreportList.add(result);
//				System.out.println("预测的实体为报告名:" + result);
//			}else if(toks[2].equals("S-nconference")){
//				String result=toks[0];
//				System.out.println("预测的实体为报告名:" + result);
//				nreportList.add(result);
//			}
//		}
          
    }
	
	public static void main(String arg[]) throws IOException{
//		new CrfTag().before();//得到train，然后放入另一个项目
		
		PreEntityExtract ee = new PreEntityExtract();
		String str="网络安全公司RecordedFuture的威胁研究小组Insikt Group在上周四发表的一篇博文中给我们带来了关于Mirai僵尸网络的最新动态。在今年1月下旬，Mirai僵尸网络的一个变种针对金融业企业发起了一系列DDoS攻击。Insikt Group表示，攻击事件中的僵尸网络可能与Mirai僵尸网络的变种IoTroop存在密切联系。到目前为止，至少已经有三家欧洲金融机构成为了攻击事件的受害者。";
		List<Term> list = ee.getShortSegment2(str);
		ee.allPretreatment(list);
		List<Term> newlist=new CrfTag().getResult4(list,"basenp", 0.7, "test.data", false, null, null);
		System.out.println(newlist);
		System.out.println("***end***");
	}
}
