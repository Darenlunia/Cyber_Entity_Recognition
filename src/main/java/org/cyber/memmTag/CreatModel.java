package org.cyber.memmTag;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import opennlp.maxent.BasicEventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.RealBasicEventStream;
import opennlp.maxent.io.GISModelWriter;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.AbstractModel;
import opennlp.model.EventStream;
import opennlp.model.OnePassDataIndexer;
import opennlp.model.OnePassRealValueDataIndexer;
import opennlp.perceptron.PerceptronTrainer;

public class CreatModel {
	public static boolean USE_SMOOTHING = false;
    public static double SMOOTHING_OBSERVATION = 0.1;
    
    private static void usage() {
      System.err.println("java CreateModel [-real] dataFile");
      System.exit(1);
    }
    
    /**
	 * Main method. Call as follows:
	 * <
	 * java CreateModel dataFile
     * @throws IOException 
	 */
	
	public CreatModel() throws IOException{
		//训练数据
		new TransferCorpus();
		String[] args ={System.getProperty("user.dir") + "\\memmConfig\\Model\\featureAfterTransfer.dat"};
		int ai = 0;
		boolean real = false;
		String type = "maxent";
		if (args.length == 0) {
			usage();
		}
		while (args[ai].startsWith("-")) {
			if (args[ai].equals("-real")) {
				real = true;
			} else if (args[ai].equals("-perceptron")) {
				type = "perceptron";
			} else {
				System.err.println("Unknown option: " + args[ai]);
				usage();
			}
			ai++;
		}
		
		String dataFileName = new String(args[ai]);
		//同文件夹生成模型文件的数据
		String modelFileName = dataFileName.substring(0, dataFileName.lastIndexOf('.')) + "Model.model";
		try {
			FileReader datafr = new FileReader(new File(dataFileName));
			System.out.println(datafr.getEncoding());
			EventStream es;
			if (!real) {
				es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
			} else {
				es = new RealBasicEventStream(new PlainTextByLineDataStream(datafr));
			}
			GIS.SMOOTHING_OBSERVATION = SMOOTHING_OBSERVATION;
			AbstractModel model;
			if (type.equals("maxent")) {
				if (!real) {
					model = GIS.trainModel(es, USE_SMOOTHING);
					
				} else {
					model = GIS.trainModel(100, new OnePassRealValueDataIndexer(es, 0), USE_SMOOTHING);
				}
			} else if (type.equals("perceptron")) {
				System.err.println("Perceptron training");
				model = new PerceptronTrainer().trainModel(10, new OnePassDataIndexer(es, 0), 0);
			} else {
				System.err.println("Unknown model type: " + type);
				model = null;
			}

			File outputFile = new File(modelFileName);
			GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
			writer.persist();
		} catch (Exception e) {
			System.out.print("Unable to create model due to exception: ");
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] arg) {
		try {
			new CreatModel();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
