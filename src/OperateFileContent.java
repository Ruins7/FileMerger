import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.File; 
import java.io.BufferedWriter; 
import java.io.FileNotFoundException; 
import java.io.FileWriter; 
import java.io.IOException; 

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *  Main calss to operate file content
 */
public class OperateFileContent{

    // Non sence value to take a place before output to final file
    public static final String NONSENCEVALUE  = "WTH";

    // store values and titles
    private static Set<String> columnNames = new HashSet<>();
    private static List<Map<String, String>> allPersonsList = new ArrayList<>();
	
    /**
    * @ClassName: exactCVSFile
    * @Descriptio: Exact .csv file content.
    * @author: Freddy
    * @date: Mar 08, 2018 6:43:03 PM
    * @param String filename: file name
    */
	public void exactCVSFile(String filename){
		try { 
            BufferedReader reader = new BufferedReader(new FileReader("./data/" + filename));
            //reader.readLine();//title row
            String line = null;
            int flag = 0;
            String last = "";
            List<String> titleList = new ArrayList<>();
            while((line = reader.readLine()) != null){ 
                String[] item = line.split("，");
                last = item[item.length-1];
                String[] value = new String[]{};
                value = last.split(",");
                if(flag == 0){
                    // get titles in to set container
                    for(int i = 0 ; i < value.length; i++){
                        String titleName = toTrimString(value[i]);
                        titleList.add(titleName);
                        columnNames.add(titleName);
                    }
                    flag++;  
                }else{
                    // get values in to list
                    Map<String, String> onePersonMap = new HashMap<>();
                    for(int i = 0 ; i < value.length; i++){
                        // put in the map <title, value>
                        onePersonMap.put(titleList.get(i), toTrimString(value[i]));
                    }
                    allPersonsList.add(onePersonMap);
                }                          
            } 
        } catch (Exception e) { 
        	System.err.println("No such "+ filename + " file in data folder or something is wrong with this file, Please try again"); 
        	//e.printStackTrace(); 
        }
    }

    /**
    * @ClassName: exactHTMLFile
    * @Descriptio: Exact .html file content
    * @author: Freddy
    * @date: Mar 08, 2018 7:05:03 PM
    * @param String filename: file name
    */
    public void exactHTMLFile(String filename){
    	try{
    		File input = new File("./data/" + filename); 
    		Document doc = Jsoup.parse(input, "UTF-8", ""); 
    		Elements trs = doc.select("table").select("tr");
            List<String> titleList = new ArrayList<>();
            Elements ths = trs.get(0).select("th");
            for(int j = 0;j < ths.size(); j++){
                String title = ths.get(j).text();
                columnNames.add(title);
                titleList.add(title);
            }
			for(int i = 1; i < trs.size(); i++){
                Map<String, String> onePersonMap = new HashMap<>();
    			Elements tds = trs.get(i).select("td");
    			for(int j = 0; j < tds.size(); j++){
    				String value = tds.get(j).text();
                    onePersonMap.put(titleList.get(j), value);
    			}
                allPersonsList.add(onePersonMap);
    		}
    	} catch (Exception e) {
    		System.err.println("No such "+ filename + " file in data folder or something is wrong with this file, Please try again"); 
        	//e.printStackTrace(); 
    	}
    }

    /**
    * @ClassName: exactXMLFile
    * @Descriptio: Exact .xml file content (TODO)
    * @author: Freddy
    * @date: Mar 08, 2018 7:08:13 PM
    * @param String filename: file name
    */
    public void exactXMLFile(String filename){

    }

    /**
    * @ClassName: generateFinalResultFile
    * @Descriptio: Generate the final result file
    * @author: Freddy
    * @date: Mar 09, 2018 3:26:11 AM
    * @param String filename: file name
    * @param String fileType: the type of file
    * @param String outPutPath: the path of file
    */
    public void generateFinalResultFile(String filename, String fileType, String outPutPath){
        // merger data have same IDs
        // find same IDs
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < allPersonsList.size(); i++) {
            for (int j = 1; j < allPersonsList.size(); j++) {
                if(j > i){
                    for(Map.Entry<String, String> entryi : allPersonsList.get(i).entrySet()){
                        for(Map.Entry<String, String> entryj : allPersonsList.get(j).entrySet()){
                            if("ID".equals(entryi.getKey()) && "ID".equals(entryj.getKey()) && entryi.getValue().equals(entryj.getValue())){
                                ids.add(entryi.getValue());
                                break;
                            }
                        }
                    }
                }     
            }
        }

        // there are same IDs !
        if(ids.size() != 0){
            List<Map<String, String>> preMergerData = new ArrayList<>();
            // merger data
            for (int j = 0; j < ids.size(); j++) {
                for (int i = 0; i < allPersonsList.size(); i++) {
                    for(Map.Entry<String, String> entry : allPersonsList.get(i).entrySet()){
                        if("ID".equals(entry.getKey()) && ids.get(j).equals(entry.getValue())){
                            preMergerData.add(allPersonsList.get(i));
                        }  
                    }
                }
                // add same ID data into postMergerData as one map
                Map<String, String> postMergerData = new HashMap<>();
                for (int k = 0; k < preMergerData.size(); k++) {
                    for(Map.Entry<String, String> entryk : preMergerData.get(k).entrySet()){
                        postMergerData.put(entryk.getKey(), entryk.getValue());
                    }
                }
                // remove old data in allPersonsList
                Iterator<Map<String,String>> it = allPersonsList.iterator();  
                while(it.hasNext()){  
                    Map<String,String> ele = it.next();  
                    for(Map.Entry<String, String> entryp : ele.entrySet()){
                        if("ID".equals(entryp.getKey()) && ids.get(j).equals(entryp.getValue())){
                            it.remove();
                        }
                    }  
                }
                // add new map data to allPersonsList
                allPersonsList.add(postMergerData);
                // clean preMergerData
                preMergerData.clear();
            }   
        }
        // Ordering by ID ASCE
        allPersonsList = insertionSortByID();
        try { 
            // target .csv file path (in ./out folder)
            File outPath = new File(outPutPath);
            if (! outPath.exists()) {  
                outPath.mkdir();  
            }
            File csvFile = new File(outPutPath, filename + fileType);
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, false));//do not append data

            // set to list
            List<String> printTitles = new ArrayList<>(columnNames);
            List<List<String>> contentList = new ArrayList<>();
            for(Map<String, String> aperson : allPersonsList){
                List<String> list = new ArrayList<>();
                int flag = 0;
                for (int i = 0; i < printTitles.size(); i++) {
                    for(Map.Entry<String, String> entry : aperson.entrySet()){
                        if(printTitles.get(i).equals(entry.getKey())){         
                            list.add(entry.getValue());
                            // reset the flag value
                            flag  = 0;     
                            break;
                        }
                        flag++;
                    }
                    if(flag == aperson.size()){
                        list.add(NONSENCEVALUE);
                        // reset the flag value     
                        flag = 0;
                    }    
                }
                contentList.add(list);      
            }

            // output to combined.csv file (printTitles and contentList are final results)
            // title row
            for (String title : printTitles) {
                bw.write(title + ","); 
            }
            bw.newLine();
            // content rows
            for (List<String> contentlist : contentList) {
                for (String value : contentlist) {
                    if(value.equals(NONSENCEVALUE)){
                        bw.write(",");
                        continue; 
                    }
                    bw.write(value + ","); 
                }  
                bw.newLine();
            }
            bw.close();    
        } catch (FileNotFoundException e) { 
            System.err.println("Something wrong with creating combined.csv file, please try again");
            //e.printStackTrace(); 
        } catch (IOException e) { 
            System.err.println("Something wrong while writing into combined.csv file, please try again");
            //e.printStackTrace(); 
        } 
    }

    /**
    * @ClassName: toTrimString
    * @Descriptio: Common method for triming String(Tool method for current class use only)
    * @author: Freddy
    * @date: Mar 08, 2018 10:42:21 PM
    * @param String str: str is the String to trim
    */
    private String toTrimString(String str){
        String reStr = "";
        try{
            reStr = str.substring(1, str.length()-1); 
        } catch (Exception e) {
            System.err.println("Something wrong when trying to trim " + str);
            //e.printStackTrace(); 
        }
        return reStr;   
    }

    /**
    * @ClassName: insertionSortByID
    * @Descriptio: Insertion sort algorithm(Tool method for current class use only)
    * @author: Freddy
    * @date: Mar 09, 2018 1:13:21 AM
    */
    private List<Map<String, String>> insertionSortByID(){
        List<Integer> idList = new ArrayList<>();
        // exact all ids
        for (int i = 0; i < allPersonsList.size(); i++) {
            for(Map.Entry<String, String> entryi : allPersonsList.get(i).entrySet()){
                if("ID".equals(entryi.getKey())){
                    idList.add(Integer.valueOf(entryi.getValue()+""));
                }
            }
        }
        // insertion sort by id ASCE
        for (int j = 1; j < idList.size(); j++) {
            int key = idList.get(j);
            int i = j - 1;
            while(i >= 0 && idList.get(i) > key){
                idList.set(i + 1, idList.get(i));
                i--;
            }
            idList.set(i + 1, key);
        }
        // generate new orderd list
        List<Map<String, String>> allPersonsListNew = new ArrayList<>();
        for (int j = 0; j < idList.size(); j++) {
            for (int i = 0; i < allPersonsList.size(); i++) {
                for(Map.Entry<String, String> entryi : allPersonsList.get(i).entrySet()){
                    if("ID".equals(entryi.getKey())){
                        int id = Integer.valueOf(entryi.getValue()+"");
                        if(id == idList.get(j)){
                            allPersonsListNew.add(allPersonsList.get(i));
                        }              
                    }
                }
            }
        }
        allPersonsList.clear();
        return allPersonsListNew; 
    }
}