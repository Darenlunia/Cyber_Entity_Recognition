package org.cyber.evaluation;

import org.cyber.dictTag.PreEntityExtract;
import org.cyber.zhifac.crf4j.CrfTest;
import org.junit.Test;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;


public class TestCrfTest {
    public void testModel(String category, double checkThreshold, String testFile,
                          boolean retrain, String[] trainArgs, String[] testArgs) throws IOException {
        File f = new File(category + ".m");
        if (retrain || !f.exists()) {
            new TestCrfLearn().testLearnModel(category, trainArgs);
        }
        assert f.exists();
        File temp = null;
        try {
            temp = File.createTempFile("crftmp-" + category + new Date().getTime(), ".out");
            temp.deleteOnExit();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        URL test = this.getClass().getClassLoader().getResource("example/" + category + "/" + testFile);
//        System.out.println(test);
        assert test != null;
        String[] args = {"-m", category + ".m", test.getPath(), "-o", temp.getAbsolutePath()};
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
        
        assert CrfTest.run(args);
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(temp), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int total = 0;
            int correct = 0;
            while((line = br.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '#' || line.charAt(0) == ' ') {
                    continue;
                } else {
                	System.out.println(line);
                    String[] toks = line.split("[\t]", -1);
//                    System.out.println(toks[toks.length - 1]+"  "+toks[toks.length - 2]);
                    assert toks.length > 2;
                    if (toks[toks.length - 1].equals(toks[toks.length - 2])) {
                        correct++;
                    }
                    total++;
                }
            }
            br.close();
            double score = (double)correct / total;
            System.out.println("score="+score);
            assert score >= checkThreshold;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
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

		for(int i=0;i<termList.size();i++){

			if((line = br.readLine()) == null) break;
			System.out.println(i);
			System.out.println(line);
			String[] toks = line.split("[\t]", -1);
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
	

    @Test
    public void testNPTestdata() throws IOException {
    	PreEntityExtract ee = new PreEntityExtract();
		String str="自从上个月21号美国DNS服务提供商Dyn遭遇大规模DDoS攻击以来，关于幕后黑手的身份引发各界猜测。Flashpoint发布的最新报告认为，这次超大型的DDoS攻击很有可能就是一名脚本小子将索尼游戏网站(PSN)作为目标，利用Mirai恶意软件感染大量IoT僵尸网络而发动的，Dyn由于向PSN提供域名解析服务而惨遭中枪。与之前所怀疑的、黑客活动组织、恐怖分子或国家黑客攻击无关。但是来自ThousandEyes的网络故障分析师Nick Kephart对此事持有不同的意见，他表示发起如此规模的DDoS攻击只是为了攻击Dyn的一个客户的说法是站不住脚的。与此同时，Akamai公司的Martin McKeay更是直言，Flashpoint的分析就是为了掩盖真正的攻击目标。";
    	List<Term> list = ee.getShortSegment2(str);
		ee.allPretreatment(list);
		System.out.println(list.size());
		List<Term> newlist=getResult4(list,"basenp", 0.7, "test.data", false, null, null);
		System.out.println(newlist);
    	System.out.println("***end***");
    }
    
    
    public static void main(String args[]) throws IOException{
    	PreEntityExtract ee = new PreEntityExtract();
		String str="自从上个月21号美国DNS服务提供商Dyn遭遇大规模DDoS攻击以来，关于幕后黑手的身份引发各界猜测。Flashpoint发布的最新报告认为，这次超大型的DDoS攻击很有可能就是一名脚本小子将索尼游戏网站(PSN)作为目标，利用Mirai恶意软件感染大量IoT僵尸网络而发动的，Dyn由于向PSN提供域名解析服务而惨遭中枪。与之前所怀疑的、黑客活动组织、恐怖分子或国家黑客攻击无关。但是来自ThousandEyes的网络故障分析师Nick Kephart对此事持有不同的意见，他表示发起如此规模的DDoS攻击只是为了攻击Dyn的一个客户的说法是站不住脚的。与此同时，Akamai公司的Martin McKeay更是直言，Flashpoint的分析就是为了掩盖真正的攻击目标。";
    	List<Term> list = ee.getShortSegment2(str);
		ee.allPretreatment(list);
		List<Term> newlist=new TestCrfTest().getResult4(list,"basenp", 0.7, "test.data", false, null, null);
		System.out.println(list);
    	System.out.println("***end***");
    }
}
