package org.cyber.zhifac.crf4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class EncoderFeatureIndex extends FeatureIndex {
    private HashMap<String, Pair<Integer, Integer>> dic_;

    public EncoderFeatureIndex(int n) {
        threadNum_ = n;
        dic_ = new HashMap<String, Pair<Integer, Integer>>();
    }

    public int getID(String key) {
        if (!dic_.containsKey(key)) {
            dic_.put(key, new Pair<Integer, Integer>(maxid_, 1));
            int n = maxid_;
            maxid_ += (key.charAt(0) == 'U' ? y_.size() : y_.size() * y_.size());
            return n;
        } else {
            Pair<Integer, Integer> pair = dic_.get(key);
            int k = pair.getKey();
            int oldVal = pair.getValue();
            dic_.put(key, new Pair<Integer, Integer>(k, oldVal + 1));
            return k;
        }
    }

    private boolean openTemplate(String filename) {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == ' ' || line.charAt(0) == '#') {
                    continue;
                } else if (line.charAt(0) == 'U') {
                    unigramTempls_.add(line.trim());
                } else if (line.charAt(0) == 'B') {
                    bigramTempls_.add(line.trim());
                } else {
                    System.err.println("unknown type: " + line);
                }
            }
            br.close();
            templs_ = makeTempls(unigramTempls_, bigramTempls_);
        } catch(Exception e) {
            if (isr != null) {
                try {
                    isr.close();
                } catch(Exception e2) {
                }
            }
            e.printStackTrace();
            System.err.println("Error reading " + filename);
            return false;
        }
        return true;
    }

    private boolean openTagSet(String filename) {
        int max_size = 0;
        InputStreamReader isr = null;
        y_.clear();
        try {
            isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                char firstChar = line.charAt(0);
                if (firstChar == '\0' || firstChar == ' ' || firstChar == '\t') {
                    continue;
                }
                String[] cols = line.split("[\t]", -1);
                if (max_size == 0) {
                    max_size = cols.length;
                }
                if (max_size != cols.length) {
                    String msg = "inconsistent column size: " + max_size +
                        " " + cols.length + " " + filename;
                    throw new RuntimeException(msg);
                }
                xsize_ = cols.length - 1;
                if (y_.indexOf(cols[max_size - 1]) == -1) {
                    y_.add(cols[max_size - 1]);
                }
            }
            Collections.sort(y_);
            br.close();
        } catch(Exception e) {
            if (isr != null) {
                try {
                    isr.close();
                } catch(Exception e2) {
                }
            }
            e.printStackTrace();
            System.err.println("Error reading " + filename);
            return false;
        }
        return true;
    }

    public boolean open(String filename1, String filename2) {
        checkMaxXsize_ = true;
        return openTemplate(filename1) && openTagSet(filename2);
    }

    public boolean save(String filename, boolean textModelFile) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(Encoder.MODEL_VERSION);
            oos.writeObject(costFactor_);
            oos.writeObject(maxid_);
            if (max_xsize_ > 0) {
                xsize_ = Math.min(xsize_, max_xsize_);
            }
            oos.writeObject(xsize_);
            oos.writeObject(y_);
            oos.writeObject(unigramTempls_);
            oos.writeObject(bigramTempls_);
            List<Pair<String, Integer>> pairList = new ArrayList<Pair<String, Integer>>();
            for (String key: dic_.keySet()) {
                pairList.add(new Pair<String, Integer>(key, dic_.get(key).getKey()));
            }

            Collections.sort(pairList, new Comparator<Pair<String,Integer>>() {
                public int compare(Pair<String,Integer> one,
                                   Pair<String,Integer> another) {
                    return one.getKey().compareTo(another.getKey());
                }
            });
            List<String> keys = new ArrayList<String>();
            int[] values = new int[pairList.size()];
            int i = 0;
            for (Pair<String, Integer> pair: pairList) {
                keys.add(pair.getKey());
                values[i++] = pair.getValue();
            }
            DoubleArrayTrie dat = new DoubleArrayTrie();
            System.out.println("Building Trie");
            dat.build(keys, null, values, keys.size());
            System.out.println("Trie built.");

            oos.writeObject(dat.getBase());
            oos.writeObject(dat.getCheck());
            oos.writeObject(alpha_);
            oos.close();

            if (textModelFile) {
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filename + ".txt"), "UTF-8");
                osw.write("version: " + Encoder.MODEL_VERSION + "\n");
                osw.write("cost-factor: " + costFactor_ + "\n");
                osw.write("maxid: " + maxid_ + "\n");
                osw.write("xsize: " + xsize_ + "\n");
                osw.write("\n");
                for (String y: y_) {
                    osw.write(y + "\n");
                }
                osw.write("\n");
                for (String utempl: unigramTempls_) {
                    osw.write(utempl + "\n");
                }
                for (String bitempl: bigramTempls_) {
                    osw.write(bitempl + "\n");
                }
                osw.write("\n");
                for (Pair<String, Integer> pair: pairList) {
                    osw.write(pair.getValue() + " " + pair.getKey() + "\n");
                }
                osw.write("\n");

                for (int k = 0; k < maxid_; k++) {
                    String val = new DecimalFormat("0.0000000000000000").format(alpha_[k]);
                    osw.write(val + "\n");
                }
                osw.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error saving model to " + filename);
            return false;
        }
        return true;
    }

    public void clear() {

    }

    public void shrink(int freq, List<TaggerImpl> taggers) {
        if (freq <= 1) {
            return;
        }
        int newMaxId = 0;
        HashMap<Integer, Integer> old2new = new HashMap<Integer, Integer>();
        HashMap<String, Pair<Integer, Integer>> newDic_ = new HashMap<String, Pair<Integer, Integer>>();
        List<String> ordKeys = new ArrayList<String>(dic_.keySet());
        // update dictionary in key order, to make result compatible with crfpp
        Collections.sort(ordKeys);
        for (String key: ordKeys) {
            Pair<Integer, Integer> featFreq = dic_.get(key);
            if (featFreq.getValue() >= freq) {
                old2new.put(featFreq.getKey(), newMaxId);
                newDic_.put(key, new Pair<Integer,Integer>(newMaxId, featFreq.getValue()));
                newMaxId += (key.charAt(0) == 'U' ? y_.size() : y_.size() * y_.size());
            }
        }

        for (TaggerImpl tagger: taggers) {
            List<List<Integer>> featureCache = tagger.getFeatureCache_();
            for (int k = 0; k < featureCache.size(); k++) {
                List<Integer> featureCacheItem = featureCache.get(k);
                List<Integer> newCache = new ArrayList<Integer>();
                for (Integer it : featureCacheItem) {
                    if (old2new.containsKey(it)) {
                        newCache.add(old2new.get(it));
                    }
                }
                newCache.add(-1);
                featureCache.set(k, newCache);
            }
        }
        maxid_ = newMaxId;
        dic_ = newDic_;
    }

    public boolean convert(String textmodel, String binarymodel) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(textmodel), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;

            int version = Integer.valueOf(br.readLine().substring("version: ".length()));
            costFactor_ = Double.valueOf(br.readLine().substring("cost-factor: ".length()));
            maxid_ = Integer.valueOf(br.readLine().substring("maxid: ".length()));
            xsize_ = Integer.valueOf(br.readLine().substring("xsize: ".length()));
            System.out.println("Done reading meta-info");
            br.readLine();

            while ((line = br.readLine()) != null && line.length() > 0) {
                y_.add(line);
            }
            System.out.println("Done reading labels");
            while ((line = br.readLine()) != null && line.length() > 0) {
                if (line.startsWith("U")) {
                    unigramTempls_.add(line);
                } else if (line.startsWith("B")) {
                    bigramTempls_.add(line);
                }
            }
            System.out.println("Done reading templates");
            dic_ = new HashMap<String, Pair<Integer, Integer>>();
            while ((line = br.readLine()) != null && line.length() > 0) {
                String[] content = line.trim().split(" ");
                if (content.length != 2) {
                    System.err.println("feature indices format error");
                    return false;
                }
                dic_.put(content[1], new Pair<Integer, Integer>(Integer.valueOf(content[0]), 1));
            }
            System.out.println("Done reading feature indices");
            List<Double> alpha = new ArrayList<Double>();
            while ((line = br.readLine()) != null && line.length() > 0) {
                alpha.add(Double.valueOf(line));
            }
            System.out.println("Done reading weights");
            alpha_ = new double[alpha.size()];
            for (int i = 0; i < alpha.size(); i++) {
                alpha_[i] = alpha.get(i);
            }
            br.close();
            System.out.println("Writing binary model to " + binarymodel);
            return save(binarymodel, false);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        } else {
            EncoderFeatureIndex featureIndex = new EncoderFeatureIndex(1);
            if (!featureIndex.convert(args[0], args[1])) {
                System.err.println("Fail to convert text model");
            }
        }
    }
}
