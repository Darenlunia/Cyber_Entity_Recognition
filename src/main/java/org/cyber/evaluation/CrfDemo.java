package org.cyber.evaluation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cyber.zhifac.crf4j.ModelImpl;
import org.cyber.zhifac.crf4j.Tagger;

/**
 * Crf的Demo
 */
public class CrfDemo {
    ModelImpl model = null;

    public CrfDemo() {
        model = new ModelImpl();
    }

    public boolean init(String modelFile) {
        System.out.println("Reading model " + modelFile);
        return model.open(modelFile, 0, 0, 1.0);
    }

    public List<String> segLine(String input, Tagger tagger) {
        if (input == null || input.length() == 0) {
            return new ArrayList<String>();
        } else {
            char[] chars = input.toCharArray();
            for (char c: chars) {
                tagger.add(c + "\tk");
            }
            tagger.parse();
            List<String> res = new ArrayList<String>();
            for (int i = 0; i < tagger.size(); i++) {
                res.add(tagger.yname(tagger.y(i)));
            }
            return res;
        }
    }

    public String seg(String line) {
        Tagger tagger = model.createTagger();
        StringBuilder sb = new StringBuilder();
        List<String> tags = segLine(line, tagger);
        for (String s: tags) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            CrfDemo ne = new CrfDemo();
            ne.init("seg.m");
            System.out.println(new Date().getTime());
            String res = ne.seg("毎日新聞社特別顧問");
            System.out.println(new Date().getTime());
            System.out.println(res);
        } catch(Exception e) {
        }
    }
}
