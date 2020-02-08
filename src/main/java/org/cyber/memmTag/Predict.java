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

	// *************************************����Ϊ���ԣ�����******************************************//

	// **********************************����Ϊcyberʵ��Ԥ�⺯��******************************************//
	/**
	 * ����cyberEntity���͵����ַ���strԤ��
	 * 
	 * @param path
	 * @throws IOException
	 * @return tag��ע��ɵ�sentences
	 */
	public void TransferPredictPath(String cyberEntity, String str, String path) throws IOException {
	}

	public List<Word> TransferPredictData( String str) throws IOException {
		// �ȶ�ȡһ���ļ������浽word,word����sentences
		PreEntityExtract ee = new PreEntityExtract();
		List<Term> termList = ee.getShortSegment2(str);// ����Ҫ���в��Եľ��ӡ���ֱ�ӷִʣ������Ǵ�ĳ���ļ��ж�ȡ�ִʽ������ȡҲ�кô����������޸ķִʽ����
		List<Word> sentences = new ArrayList<Word>();
		for (Term term : termList)
			sentences.add(new Word(term.word, term.nature.toString()));// ��sentences�е�ÿ��wordֻ��tagû�б��
		String prev = "n_START_";
		String prev2 = "n_START_";

		String modelFileName = "G:\\extraEclipseWork\\CyberEntity\\memmConfig\\Model\\featureAfterTransferModel.model";
		MaxentModel m = new GenericModelReader(new File(modelFileName)).getModel();
		this._model = m;
		for (int i = 0; i < sentences.size(); i++) {// ��ÿ�����ʽ���������ȡ������predict��ȡ��ǽ��
			String feat = this.getPredictFeatures(i, sentences, prev, prev2);// �õ���ǰ�ʵ�����
			String[] contexts = feat.split(" ");// ******��һ������������******��������һ���ո�
			prev2 = prev;
			prev = _model.getBestOutcome(_model.eval(contexts));
//			float[] values = RealValueFileEventStream.parseContexts(contexts);
//			System.out.println(_model.eval(contexts, values)); 
			sentences.get(i).tag = prev;
			// prev=label[this.max(_model.eval(contexts))];
		}
		this.writeTo(sentences);// �����д���ļ�
		return sentences;
	}

	
	public List<Word> TransferPredictData(List<Term> termList) throws IOException {
		// �ȶ�ȡһ���ļ������浽word,word����sentences
		List<Word> sentences = new ArrayList<Word>();
		for (Term term : termList)
			sentences.add(new Word(term.word, term.nature.toString()));// ��sentences�е�ÿ��wordֻ��tagû�б��
		String prev = "n_START_";
		String prev2 = "n_START_";

		String modelFileName = "G:\\extraEclipseWork\\CyberEntity\\memmConfig\\Model\\featureAfterTransferModel.model";
		MaxentModel m = new GenericModelReader(new File(modelFileName)).getModel();
		this._model = m;
		for (int i = 0; i < sentences.size(); i++) {// ��ÿ�����ʽ���������ȡ������predict��ȡ��ǽ��
			String feat = this.getPredictFeatures(i, sentences, prev, prev2);// �õ���ǰ�ʵ�����
			String[] contexts = feat.split(" ");// ******��һ������������******��������һ���ո�
			prev2 = prev;
			prev = _model.getBestOutcome(_model.eval(contexts));
//			float[] values = RealValueFileEventStream.parseContexts(contexts);
//			System.out.println(_model.eval(contexts, values)); 
			sentences.get(i).tag = prev;
			// prev=label[this.max(_model.eval(contexts))];
		}
		this.writeTo(sentences);// �����д���ļ�
		return sentences;
	}
	// /**
	// * ȡ�������ֵ
	// * @param prob
	// * @return
	// */
	// //��temp�м亯������������ȡ
	// public int max(double[] prob){
	// int max=0;
	// if (prob[0]<prob[1])
	// max=1;
	// if(prob[max]<prob[2])
	// max=2;
	// return max;
	// }

	// ��temp�м亯������������ȡ
	public String getPredictFeatures(int i, List<Word> sentences, String prev, String prev2) throws IOException { // ���ִʽ��
		Map<Integer, String> context = new HashMap<Integer, String>();// ���棬
																		// keyΪ��i������
		Map<Integer, String> pos_context = new HashMap<Integer, String>();// ���ԣ�keyΪ��i������
		int l = sentences.size();
		// ǰ�ڲ�������
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
	 * �����д���ı�txt�Լ�ֱ�ӿ���̨���sentences(�Ѿ������ȫ��ǵ�sentences)��
	 * 
	 * @param newPath
	 * @param sentences
	 * @throws IOException
	 */
	// ��temp�м亯������������ȡ
	public void writeTo(List<Word> sentences) throws IOException {
		String newPath = System.getProperty("user.dir") + "\\memmConfig\\Model\\finishPredictData.dat";// ת��������
		File writename = new File(newPath); // ���·�������û����Ҫ����һ���µ�output.txt�ļ�
		if (!writename.exists())
			writename.createNewFile(); // �������ļ�
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		String str = "";
		for (int i = 0; i < sentences.size(); i++) {
			{
				str = sentences.get(i).word + " " + sentences.get(i).tag;
				System.out.println(str);
			}
			out.write(str + "\r\n");
		}
		out.flush(); // �ѻ���������ѹ���ļ�
		out.close(); // ���ǵùر��ļ�
	}

	/**
	 * BMES������Ԥ����
	 * 
	 * @throws IOException
	 */
	public Map<String,List<String>> getResult(String str) throws IOException {
		System.out.println("�����Ԥ����......");
		Map<String,List<String>> iocs =new HashMap<String,List<String>>();
		List<Word> sentences = this.TransferPredictData(str);//��������ؼ����Լ������̨�Ľ�����
		List<String> norgList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nthreatList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nrList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nhackList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> neventList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nreportList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nconferenceList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
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
				System.out.println("Ԥ���ʵ��Ϊnvendor:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊnvendor:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ��в������Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ��в������Ϊ������:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ�ڿ���֯:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ�ڿ���֯:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ�¼���:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ�¼���:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				nreportList.add(result);
			}
		}
//		for(String string:resultList)
//			System.out.println(string);
		return iocs;
	}

	/**
	 * ���� old ����
	 * BMES������Ԥ�������������Ϊlist��(�� nconference)
	 * �������޸ġ�������������BMESʶ���Ĵ���list�кϲ������޸Ĵ��ԡ�����
	 * @throws IOException
	 */
	public Map<String,List<String>> getResult(List<Term> termList) throws IOException {
		System.out.println("�����Ԥ����......");
		Map<String,List<String>> iocs =new HashMap<String,List<String>>();
		List<Word> sentences = this.TransferPredictData(termList);//��������ؼ����Լ������̨�Ľ�����
		List<String> norgList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nthreatList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nrList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nproductList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nhackList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> neventList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nreportList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nconferenceList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
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
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ��в����Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ��в����Ϊ������:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ�ڿ���֯:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ�ڿ���֯:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ�¼���:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ�¼���:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ�����:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nproduct")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ�����:" + result);
				nreportList.add(result);
			}
		}
//		for(String string:resultList)
//			System.out.println(string);
		return iocs;
	}
	
	/**
	 * ���� new ����
	 * @param termList �ִ�+�ʵ�+������ɺ��list�����ڼ�������أ�
	 * @param iocs ����һ����listת��Ϊmap�洢�������������ʶ����´��Ե�map
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<String>> getResult2(List<Term> termList,Map<String,List<String>> iocs) throws IOException {
		List<Word> sentences = this.TransferPredictData(termList);//��������ؼ����Լ������̨�Ľ�����
		List<String> nthreatList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nhackList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> neventList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nreportList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
		List<String> nconferenceList = new ArrayList<String>();//�洢BMES��Ϻ�Ľ��
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
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ��в����Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ��в����Ϊ������:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ����:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ�ڿ���֯:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ�ڿ���֯:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ�¼���:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ�¼���:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
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
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				i--;
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("Ԥ���ʵ��Ϊ������:" + result);
				nreportList.add(result);
			}
		}

		return iocs;
	}
	
	/**
	 * ���� newest ����
	 * @param termList �ִ�+�ʵ�+������ɺ��list�����ڼ�������أ�
	 * �����޸�list���ٴ�����һ����getEntity3()����map�洢
	 * @return
	 * @throws IOException
	 */
	public void getResult3(List<Term> termList) throws IOException {
		List<Word> sentences = this.TransferPredictData(termList);//��������ؼ����Լ������̨�Ľ�����
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
				//����һ��i
				sentences.add(i, new Word(result, "nt","O"));
				System.out.println("�������:" + result);
			}else if(sentences.get(i).tag.equals("S-norg")){
				String result=sentences.get(i).word;
				sentences.set(i, new Word(result, "nt","O"));
				System.out.println("�������:" + result);
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
				System.out.println("������в����Ϊ������:" + result);
			}else if(sentences.get(i).tag.equals("S-nthreat")){
				String result=sentences.get(i).word;
				sentences.set(i, new Word(result, "nthreat","O"));
				System.out.println("������в����Ϊ������:" + result);
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
				System.out.println("��������:" + result);
				sentences.add(i, new Word(result, "nr","O"));
			}else if(sentences.get(i).tag.equals("S-nr")){
				String result=sentences.get(i).word;
				System.out.println("��������:" + result);
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
				System.out.println("����ڿ���֯:" + result);
				sentences.add(i, new Word(result, "nhack","O"));
			}else if(sentences.get(i).tag.equals("S-nhack")){
				String result=sentences.get(i).word;
				sentences.set(i, new Word(result, "nhack","O"));
				System.out.println("����ڿ���֯:" + result);
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
				System.out.println("�����¼���:" + result);
				sentences.add(i, new Word(result, "nevent","O"));
			}else if(sentences.get(i).tag.equals("S-nevent")){
				String result=sentences.get(i).word;
				System.out.println("�����¼���:" + result);
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
				System.out.println("��������:" + result);
				sentences.add(i, new Word(result, "nreport","O"));
			}else if(sentences.get(i).tag.equals("S-nreport")){
				String result=sentences.get(i).word;
				System.out.println("��������:" + result);
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
				System.out.println("���������:" + result);
				sentences.add(i, new Word(result, "nconference","O"));
			}else if(sentences.get(i).tag.equals("S-nconference")){
				String result=sentences.get(i).word;
				System.out.println("���������:" + result);
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
				System.out.println("���������:" + result);
				sentences.add(i, new Word(result, "nproduct","O"));
			}else if(sentences.get(i).tag.equals("S-nproduct")){
				String result=sentences.get(i).word;
				System.out.println("���������:" + result);
				sentences.set(i, new Word(result, "nproduct","O"));
			}
		}
		for(int j=0;j<sentences.size();j++){
			termList.set(j, new Term(sentences.get(j).word, Nature.create(sentences.get(j).pos)));
		}
	}
}
