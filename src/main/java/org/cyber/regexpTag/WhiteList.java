package org.cyber.regexpTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Description WhiteList
 * ���У�(map)whiteList��keyΪpattern��URL/HOST...����valueΪwhitelist��ÿ��ini�ļ���Ӧ��������ʽ���������룩ֵ
 *
 * 
 */
public class WhiteList {

	private HashMap<String, List<Pattern>> whiteList=new HashMap<String, List<Pattern>>();

	public WhiteList() {//��֪���Ƿ�����д�޲ι��캯������
		try {
			this.compileWhiteList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 * @throws IOException
	 * @basedir��������ini����·�� ��ȡ�������е�����patterns ����map��keyΪini�ļ�����valueΪ������������ʽ
	 * 
	 */
	public void compileWhiteList() throws IOException {
		String basedir = "G:\\extraEclipseWork\\ioc-parse\\wlini\\whitelists";
		File file = new File(basedir);
		String[] iniFileArray = file.list();// fileName��whitelists������ini�ļ������б�
		List<Pattern> patternList=new ArrayList<Pattern>();
		for (int i = 0; i < iniFileArray.length; i++) { // iΪ����һ��������
			// �ָ��������������,��_�ָ�������֣�ȡǰ��ģ�����key
			String key = iniFileArray[i].substring(0,iniFileArray[i].lastIndexOf("."));//��ȡ����׺1λ������
			key = key.substring(key.lastIndexOf("_")+1);//��ȡ��׺��
			//System.out.println(key);
			String searchdir = basedir + "\\" + iniFileArray[i];
			// System.out.print(fileName[i]);
			FileReader fr = new FileReader(searchdir);
			BufferedReader in = new BufferedReader(fr);// ��ȡĳ��������
			String temp = "";
			if (in.readLine() == null)//���б���
				continue;
			while ((temp = in.readLine()) != null) {
				// System.out.println(temp);
				temp.replaceAll("\n", "");//����ÿ�����ݲ����룬������list
				Pattern pattern = Pattern.compile(temp);
				patternList.add(pattern);
			}
//			System.out.println("�����pattern"+i+"..."+key+"...");
			whiteList.put(key, patternList);
			in.close();
		}
		
	}
	
	/**
	 * ���������
	 * @return
	 */
	public HashMap<String, List<Pattern>> retWhiteList(){
		System.out.println("whiteList"+whiteList);
		return whiteList;
	}
	
	/**
	 * û���ã��ĳ�������ĺ���
	 * ���� ��valueΪ(map)whitelistÿ��ini�ļ���Ӧÿ�еĽ���ֵ��˫��list��
	 * @return
	 */
	public List<List<Pattern>> retValue() {
		List<List<Pattern>> list = new ArrayList<List<Pattern>>(); 
		for (int i = 0; i < whiteList.size(); i++) {//��whietList��valueֵ֮List<Pattern>��������list������
			String key = "" + i;
			list.add(whiteList.get(key));//ͻȻ����˫���List����ȡ�����LIST�Ǹ���ini�ļ����ڲ���ÿһ�У���ʵ���Ժϲ��������У�����һ��ѭ��
		}
		return list;
		
	}
	
	/**
	 * ͨ������whitelist�ļ�����ini�ļ������ͣ�url/host...���Ʋ��Ҷ�Ӧini�ļ��Ľ���ֵ
	 * ͨ������keyֵ���ض�Ӧini�ļ��Ľ���ֵ������list
	 */	
	public List<Pattern> getValue(String key) {
		List<Pattern> list = new ArrayList<Pattern>();
		list=whiteList.get(key);
		return list;
	}
}
