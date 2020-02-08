package org.cyber.evaluation;

import java.net.URL;

import org.cyber.zhifac.crf4j.CrfLearn;

public class TestCrfLearn {
    public void testLearnModel(String category, String[] option) {
        URL templ = this.getClass().getClassLoader().getResource("example/" + category + "/template");
        URL train = this.getClass().getClassLoader().getResource("example/" + category + "/train.data");
        assert templ != null;
        assert train != null;
        String[] args = new String[]{templ.getPath(), train.getPath(), category + ".m"};
        if (option != null) {
            String[] newargs = new String[option.length + args.length];
            for (int i = 0; i < option.length; i++) {
                newargs[i] = option[i];
            }
            for (int i = 0; i < args.length; i++) {
                newargs[option.length + i] = args[i];
            }
            args = newargs;
        }
        assert CrfLearn.run(args);
    }


//    @Test
//    public void testBasicLearnNPModel() {
//        testLearnModel("basenp", null);
//    }
//    
    public static void main(String args[]){
    	new TestCrfLearn().testLearnModel("basenp", null);
    }
}
