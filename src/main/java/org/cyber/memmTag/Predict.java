package org.cyber.memmTag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.cyber.dictTag.PreEntityExtract;
import org.cyber.memmTag.Word;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;

import opennlp.maxent.BasicContextGenerator;
import opennlp.maxent.ContextGenerator;
import opennlp.maxent.DataStream;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;
import opennlp.model.RealValueFileEventStream;

/**
 * Test the model on some input.
 */
public class Predict {
	MaxentModel _model;
	ContextGenerator _cg = new BasicContextGenerator();

	public Predict() {
	}

	public Predict(MaxentModel m) {
		_model = m;
	}

	private void eval(String predicates) {
		eval(predicates, false);
	}

	private void eval(String predicates, boolean real) {
		String[] contexts = predicates.split(" ");
		double[] ocs;
		if (!real) {
			ocs = _model.eval(contexts);
		} else {
			float[] values = RealValueFileEventStream.parseContexts(contexts);
			ocs = _model.eval(contexts, values);
		}
		// System.out.println(_model.getBestOutcome(ocs));
		System.out.println("For context: " + predicates + "\n" + _model.getAllOutcomes(ocs) + "\n");

	}

	private static void usage() {

	}

	/**
	 * Main method. Call as follows:
	 * <p>
	 * java Predict dataFile (modelFile)
	 */

//	public static void main(String[] args) {
//		String dataFileName, modelFileName;
//		boolean real = false;
//		String type = "maxent";
//		int ai = 0;
//		if (args.length > 0) {
//			while (args[ai].startsWith("-")) {
//				if (args[ai].equals("-real")) {
//					real = true;
//				} else if (args[ai].equals("-perceptron")) {
//					type = "perceptron";
//				} else {
//					usage();
//				}
//				ai++;
//			}
//			dataFileName = args[ai++];
//			if (args.length > ai) {
//				modelFileName = args[ai++];
//			} else {
//				modelFileName = dataFileName.substring(0, dataFileName.lastIndexOf('.')) + "Model.txt";
//			}
//		} else {
//			dataFileName = "G:\\extraEclipseWork\\CyberEntity\\src\\main\\java\\org\\cyber\\memmTag\\football.test";
//			modelFileName = "G:\\extraEclipseWork\\CyberEntity\\src\\main\\java\\org\\cyber\\memmTag\\footballModel.txt";
//		}
//		Predict predictor = null;
//		try {
//			MaxentModel m = new GenericModelReader(new File(modelFileName)).getModel();
//			predictor = new Predict(m);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//
//		if (dataFileName.equals("")) {
//			predictor.eval("Sunday");
//		} else {
//			try {
//				DataStream ds = new PlainTextByLineDataStream(new FileReader(new File(dataFileName)));
//				while (ds.hasNext()) {
//					String s = (String) ds.nextToken();
//					predictor.eval(s.substring(0, s.lastIndexOf(' ')), real);
//				}
//				return;
//			} catch (Exception e) {
//				System.out.println("Unable to read from specified file: " + modelFileName);
//				System.out.println();
//				e.printStackTrace();
//			}
//		}
//	}

	// *************************************以上为测试，无用******************************************//

	// **********************************以上为cyber实体预测函数******************************************//
	/**
	 * 关于cyberEntity类型的新字符串str预测
	 * 
	 * @param path
	 * @throws IOException
	 * @return tag标注完成的sentences
	 */
	public void TransferPredictPath(String cyberEntity, String str, String path) throws IOException {
	}

	public List<Word> TransferPredictData( String str) throws IOException {
		// 先读取一波文件，储存到word,word放入sentences
		PreEntityExtract ee = new PreEntityExtract();
		List<Term> termList = ee.getShortSegment2(str);// 输入要进行测试的句子——直接分词，而不是从某个文件中读取分词结果（读取也有好处，即可以修改分词结果）
		List<Word> sentences = new ArrayList<Word>();
		for (Term term : termList)
			sentences.add(new Word(term.word, term.nature.toString()));// 在sentences中的每个word只有tag没有标记
		String prev = "n_START_";
		String prev2 = "n_START_";

		String modelFileName = "G:\\extraEclipseWork\\CyberEntity\\memmConfig\\Model\\featureAfterTransferModel.model";
		MaxentModel m = new GenericModelReader(new File(modelFileName)).getModel();
		this._model = m;
		for (int i = 0; i < sentences.size(); i++) {// 对每个单词进行特征获取，输入predict获取标记结果
			String feat = this.getPredictFeatures(i, sentences, prev, prev2);// 得到当前词的特征
			String[] contexts = feat.split(" ");// ******这一步可能有问题******，最后多了一个空格
			prev2 = prev;
			prev = _model.getBestOutcome(_model.eval(contexts));
//			float[] values = RealValueFileEventStream.parseContexts(contexts);
//			System.out.println(_model.eval(contexts, values)); 
			sentences.get(i).tag = prev;
			// prev=label[this.max(_model.eval(contexts))];
		}
		this.writeTo(sentences);// 将结果写入文件
		return sentences;
	}

	
	public List<Word> TransferPredictData(List<Term> termList) throws IOException {
		// 先读取一波文件，储存到word,word放入sentences
		List<Word> sentences = new ArrayList<Word>();
		for (Term term : termList)
			sentences.add(new Word(term.word, term.nature.toString()));// 在sentences中的每个word只有tag没有标记
		String prev = "n_START_";
		String prev2 = "n_START_";

		String modelFileName = "G:\\extraEclipseWork\\CyberEntity\\memmConfig\\Model\\featureAfterTransferModel.model";
		MaxentModel m = new GenericModelReader(new File(modelFileName)).getModel();
		this._model = m;
		for (int i = 0; i < sentences.size(); i++) {// 对每个单词进行特征获取，输入predict获取标记结果
			String feat = this.getPredictFeatures(i, sentences, prev, prev2);// 得到当前词的特征
			String[] contexts = feat.split(" ");// ******这一步可能有问题******，最后多了一个空格
			prev2 = prev;
			prev = _model.getBestOutcome(_model.eval(contexts));
//			float[] values = RealValueFileEventStream.parseContexts(contexts);
//			System.out.println(_model.eval(contexts, values)); 
			sentences.get(i).tag = prev;
			// prev=label[this.max(_model.eval(contexts))];
		}
		this.writeTo(sentences);// 将结果写入文件
		return sentences;
	}
	// /**
	// * 取三者最大值
	// * @param prob
	// * @return
	// */
	// //↓temp中间函数——特征提取
	// public int max(double[] prob){
	// int max=0;
	// if (prob[0]<prob[1])
	// max=1;
	// if(prob[max]<prob[2])
	// max=2;
	// return max;
	// }

	// ↓temp中间函数——特征提取
	public String getPredictFeatures(int i, List<Word> sentences, String prev, String prev2) throws IOException { // ：分词结果
		Map<Integer, String> context = new HashMap<Integer, String>();// 词面，
																		// key为第i个单词
		Map<Integer, String> pos_context = new HashMap<Integer, String>();// 词性，key为第i个单词
		int l = sentences.size();
		// 前期参数处理
		context.put(-2, "_START_");
		context.put(-1, "_START_");
		context.put(l, "_END_");
		context.put(l + 1, "_END_");
		pos_context.put(-2, "_PSTART_");
		pos_context.put(-1, "_PSTART_");
		pos_context.put(l, "_PEND_");
		pos_context.put(l + 1, "_PEND_");
		for (int j = 0; j < sentences.size(); j++) {
			context.put(j, sentences.get(j).word);
			pos_context.put(j, sentences.get(j).pos);
		}
		GetFeatures gf = new GetFeatures();
		HashSet<String> hs = gf.getFeatures(i, context, pos_context, prev, prev2);
		String fieldList = "";
		for (String str : hs) {
			fieldList = fieldList + str + " ";
		}
		return fieldList;
	}

	/**
	 * 将结果写入文本txt以及直接控制台输出sentences(已经完成完全标记的sentences)）
	 * 
	 * @param newPath
	 * @param sentences
	 * @throws IOException
	 */
	// ↓temp中间函数——特征提取
	public void writeTo(List<Word> sentences) throws IOException {
		String newPath = System.getProperty("user.dir") + "\\memmConfig\\Model\\finishPredictData.dat";// 转换后语料
		File writename = new File(newPath); // 相对路径，如果没有则要建立一个新的output.txt文件
		if (!writename.exists())
			writename.createNewFile(); // 创建新文件
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		String str = "";
		for (int i = 0; i < sentences.size(); i++) {
			{
				str = sentences.get(i).word + " " + sentences.get(i).tag;
				System.out.println(str);
			}
			out.write(str + "\r\n");
		}
		out.flush(); // 把缓存区内容压入文件
		out.close(); // 最后记得关闭文件
	}

	/**
	 * BMES组合输出预测结果
	 * 
	 * @throws IOException
	 */
	public Map<String,List<String>> getResult(String str) throws IOException {
		System.out.println("最大熵预测结果......");
		Map<String,List<String>> iocs =new HashMap<String,List<String>>();
		List<Word> sentences = this.TransferPredictData(str);//进行最大熵计算以及向控制台的结果输出
		List<String> norgList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nthreatList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nrList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nhackList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> neventList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nreportList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nconferenceList = new ArrayList<String>();//存储BMES组合后的结果
		iocs.put("norg", norgList);
		iocs.put("nthreat", nthreatList);
		iocs.put("nr", nrList);
		iocs.put("nhack", nhackList);
		iocs.put("nevent", neventList );
		iocs.put("nreport", nreportList);
		iocs.put("nconference", nconferenceList );
		
		for (int i = 0; i < sentences.size(); i++) {
			if (sentences.get(i).tag.equals("B-norg")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-norg")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-norg")){
					result += sentences.get(i).word;
					i++;
				}
				norgList.add(result);
				System.out.println("预测的实体为nvendor:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为nvendor:" + result);
				norgList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nthreat")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nthreat")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nthreat")){
					result += sentences.get(i).word;
					i++;
				}
				nthreatList.add(result);
				System.out.println("预测的实体为威胁（多数为病毒）:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为威胁（多数为病毒）:" + result);
				nthreatList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nr")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nr")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nr")){
					result += sentences.get(i).word;
					i++;
				}
				nrList.add(result);
				System.out.println("预测的实体为人名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为人名:" + result);
				nrList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nhack")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nhack")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nhack")){
					result += sentences.get(i).word;
					i++;
				}
				nhackList.add(result);
				System.out.println("预测的实体为黑客组织:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为黑客组织:" + result);
				nhackList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nevent")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nevent")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nevent")){
					result += sentences.get(i).word;
					i++;
				}
				neventList.add(result);
				System.out.println("预测的实体为事件名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为事件名:" + result);
				neventList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nreport")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nreport")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nreport")){
					result += sentences.get(i).word;
					i++;
				}
				nreportList.add(result);
				System.out.println("预测的实体为报告名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为报告名:" + result);
				nreportList.add(result);
			}else if (sentences.get(i).tag.equals("B-nconference")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nconference")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nconference")){
					result += sentences.get(i).word;
					i++;
				}
				nreportList.add(result);
				System.out.println("预测的实体为报告名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为报告名:" + result);
				nreportList.add(result);
			}
		}
//		for(String string:resultList)
//			System.out.println(string);
		return iocs;
	}

	/**
	 * —— old ——
	 * BMES组合输出预测结果（输入参数为list）(无 nconference)
	 * ——待修改——————将BMES识别后的词在list中合并，并修改词性。——
	 * @throws IOException
	 */
	public Map<String,List<String>> getResult(List<Term> termList) throws IOException {
		System.out.println("最大熵预测结果......");
		Map<String,List<String>> iocs =new HashMap<String,List<String>>();
		List<Word> sentences = this.TransferPredictData(termList);//进行最大熵计算以及向控制台的结果输出
		List<String> norgList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nthreatList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nrList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nproductList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nhackList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> neventList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nreportList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nconferenceList = new ArrayList<String>();//存储BMES组合后的结果
		iocs.put("norg", norgList);
		iocs.put("nthreat", nthreatList);
		iocs.put("nr", nrList);
		iocs.put("nproduct", nproductList);
		iocs.put("nhack", nhackList);
		iocs.put("nevent", neventList );
		iocs.put("nreport", nreportList);
		iocs.put("nconference", nconferenceList);
		for (int i = 0; i < sentences.size(); i++) {
			if (sentences.get(i).tag.equals("B-norg")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-norg")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-norg")){
					result += sentences.get(i).word;
					i++;
				}
				norgList.add(result);
				System.out.println("预测的实体为机构:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为机构:" + result);
				norgList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nthreat")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nthreat")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nthreat")){
					result += sentences.get(i).word;
					i++;
				}
				nthreatList.add(result);
				System.out.println("预测的实体为威胁（多为病毒）:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为威胁（多为病毒）:" + result);
				nthreatList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nr")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nr")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nr")){
					result += sentences.get(i).word;
					i++;
				}
				nrList.add(result);
				System.out.println("预测的实体为人名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为人名:" + result);
				nrList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nhack")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nhack")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nhack")){
					result += sentences.get(i).word;
					i++;
				}
				nhackList.add(result);
				System.out.println("预测的实体为黑客组织:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为黑客组织:" + result);
				nhackList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nevent")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nevent")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nevent")){
					result += sentences.get(i).word;
					i++;
				}
				neventList.add(result);
				System.out.println("预测的实体为事件名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为事件名:" + result);
				neventList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nreport")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nreport")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nreport")){
					result += sentences.get(i).word;
					i++;
				}
				nreportList.add(result);
				System.out.println("预测的实体为报告名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为报告名:" + result);
				nreportList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nconference")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nconference")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nconference")){
					result += sentences.get(i).word;
					i++;
				}
				nreportList.add(result);
				System.out.println("预测的实体为会议名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为会议名:" + result);
				nreportList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nproduct")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nproduct")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nproduct")){
					result += sentences.get(i).word;
					i++;
				}
				nreportList.add(result);
				System.out.println("预测的实体为软件名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nproduct")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为软件名:" + result);
				nreportList.add(result);
			}
		}
//		for(String string:resultList)
//			System.out.println(string);
		return iocs;
	}
	
	/**
	 * —— new ——
	 * @param termList 分词+词典+规则完成后的list（用于计算最大熵）
	 * @param iocs 将上一步的list转换为map存储，并加入最大熵识别的新词性的map
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<String>> getResult2(List<Term> termList,Map<String,List<String>> iocs) throws IOException {
		List<Word> sentences = this.TransferPredictData(termList);//进行最大熵计算以及向控制台的结果输出
		List<String> nthreatList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nhackList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> neventList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nreportList = new ArrayList<String>();//存储BMES组合后的结果
		List<String> nconferenceList = new ArrayList<String>();//存储BMES组合后的结果
		iocs.put("nthreat", nthreatList);
		iocs.put("nhack", nhackList);
		iocs.put("nevent", neventList );
		iocs.put("nreport", nreportList);
		iocs.put("nconference", nconferenceList);
		for (int i = 0; i < sentences.size(); i++) {
			if (sentences.get(i).tag.equals("B-norg")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-norg")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-norg")){
					result += sentences.get(i).word;
					i++;
				}
				iocs.get("nt").add(result);
				System.out.println("预测的实体为机构:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为机构:" + result);
				iocs.get("nt").add(result);
			}
			else if (sentences.get(i).tag.equals("B-nthreat")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nthreat")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nthreat")){
					result += sentences.get(i).word;
					i++;
				}
				nthreatList.add(result);
				System.out.println("预测的实体为威胁（多为病毒）:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为威胁（多为病毒）:" + result);
				nthreatList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nr")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nr")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nr")){
					result += sentences.get(i).word;
					i++;
				}
				iocs.get("nr").add(result);
				System.out.println("预测的实体为人名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为人名:" + result);
				iocs.get("nr").add(result);
			}
			else if (sentences.get(i).tag.equals("B-nhack")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nhack")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nhack")){
					result += sentences.get(i).word;
					i++;
				}
				nhackList.add(result);
				System.out.println("预测的实体为黑客组织:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为黑客组织:" + result);
				nhackList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nevent")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nevent")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nevent")){
					result += sentences.get(i).word;
					i++;
				}
				neventList.add(result);
				System.out.println("预测的实体为事件名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为事件名:" + result);
				neventList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nreport")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nreport")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nreport")){
					result += sentences.get(i).word;
					i++;
				}
				nreportList.add(result);
				System.out.println("预测的实体为报告名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为报告名:" + result);
				nreportList.add(result);
			}
			else if (sentences.get(i).tag.equals("B-nconference")) {
				String result = sentences.get(i).word;
				i++;
				while (sentences.get(i).tag.equals("M-nconference")) {
					result += sentences.get(i).word;
					i++;
				}
				if(sentences.get(i).tag.equals("E-nconference")){
					result += sentences.get(i).word;
					i++;
				}
				nreportList.add(result);
				System.out.println("预测的实体为报告名:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("预测的实体为报告名:" + result);
				nreportList.add(result);
			}
		}

		return iocs;
	}
	
	/**
	 * —— newest ——
	 * @param termList 分词+词典+规则完成后的list（用于计算最大熵）
	 * 仅仅修改list，再传回上一步的getEntity3()做成map存储
	 * @return
	 * @throws IOException
	 */
	public void getResult3(List<Term> termList) throws IOException {
		List<Word> sentences = this.TransferPredictData(termList);//进行最大熵计算以及向控制台的结果输出
		for (int i = 0; i < sentences.size(); i++) {
			if (sentences.get(i).tag.equals("B-norg")) {		
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-norg")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-norg")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				//插入一个i
				sentences.add(i, new Word(result, "nt","O"));
				System.out.println("新添机构:" + result);
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				sentences.set(i, new Word(result, "nt","O"));
				System.out.println("新添机构:" + result);
			}
			else if (sentences.get(i).tag.equals("B-nthreat")) {
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-nthreat")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-nthreat")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				sentences.add(i, new Word(result, "nthreat","O"));
				System.out.println("新添威胁（多为病毒）:" + result);
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				sentences.set(i, new Word(result, "nthreat","O"));
				System.out.println("新添威胁（多为病毒）:" + result);
			}
			else if (sentences.get(i).tag.equals("B-nr")) {
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-nr")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-nr")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				System.out.println("新添人名:" + result);
				sentences.add(i, new Word(result, "nr","O"));
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("新添人名:" + result);
				sentences.set(i, new Word(result, "nr","O"));
			}
			else if (sentences.get(i).tag.equals("B-nhack")) {
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-nhack")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-nhack")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				System.out.println("新添黑客组织:" + result);
				sentences.add(i, new Word(result, "nhack","O"));
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				sentences.set(i, new Word(result, "nhack","O"));
				System.out.println("新添黑客组织:" + result);
			}
			else if (sentences.get(i).tag.equals("B-nevent")) {
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-nevent")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-nevent")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				System.out.println("新添事件名:" + result);
				sentences.add(i, new Word(result, "nevent","O"));
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("新添事件名:" + result);
				sentences.set(i, new Word(result, "nevent","O"));
			}
			else if (sentences.get(i).tag.equals("B-nreport")) {
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-nreport")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-nreport")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				System.out.println("新添报告名:" + result);
				sentences.add(i, new Word(result, "nreport","O"));
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("新添报告名:" + result);
				sentences.set(i, new Word(result, "nreport","O"));
			}
			else if (sentences.get(i).tag.equals("B-nconference")) {
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-nconference")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-nconference")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				System.out.println("新添会议名:" + result);
				sentences.add(i, new Word(result, "nconference","O"));
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("新添会议名:" + result);
				sentences.set(i, new Word(result, "nconference","O"));
			}
			else if (sentences.get(i).tag.equals("B-nproduct")) {
				String result = sentences.get(i).word;
				sentences.remove(i);
				while (sentences.get(i).tag.equals("M-nproduct")) {
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				if(sentences.get(i).tag.equals("E-nproduct")){
					result += sentences.get(i).word;
					sentences.remove(i);
				}
				System.out.println("新添软件名:" + result);
				sentences.add(i, new Word(result, "nproduct","O"));
			}else if(sentences.get(i).tag.equals("S-nproduct")){
				String result=sentences.get(i).word;
				System.out.println("新添软件名:" + result);
				sentences.set(i, new Word(result, "nproduct","O"));
			}
		}
		for(int j=0;j<sentences.size();j++){
			termList.set(j, new Term(sentences.get(j).word, Nature.create(sentences.get(j).pos)));
		}
	}
}
