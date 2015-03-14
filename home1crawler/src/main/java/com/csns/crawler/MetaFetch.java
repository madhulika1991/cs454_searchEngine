package com.csns.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.ContentHandler;


public class MetaFetch {

	
	
	public static void main(String args[]){
		
		String path="c:\\downloaded\\";
		//string void name = length file;
		String name="hw2.json";
		try {
		if(args!=null && args.length == 2 && args[0].equals("-c")){
			FileWriter file = new FileWriter(path+name, true);
			String fileName=args[1];
			
				JSONObject obj = readJsonFile(path + fileName);
				JSONArray arr=obj.getJSONArray("EmbeddedLinksInfo");
				for (int i = 0, size = arr.length(); i < size; i++)
			    {
			      JSONObject objectInArray = arr.getJSONObject(i);
			      
			     if(null!=objectInArray.get("images"))
					 fetchMetadata((JSONArray) objectInArray.get("images"));
					 

					 
					// fetchMetadata((JSONArray) objectInArray.get("links"));
					 
			    }
					
				file.write(obj.toString());
			file.flush();
			file.close();
			System.out.println("\n\n\n== MetaData Extraction Done Pls Check "+name);
			
		}else{
			System.out.println("Usage:java -jar extractor.jar -c <control file>");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private static void fetchMetadata(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		
		
		for (int i = 0, size = jsonArray.length(); i < size; i++)
	    {
			JSONObject imageJson = (JSONObject) jsonArray.getJSONObject(i);
			FileInputStream fileInputStream = null;
			JSONObject metaJson = new JSONObject();
			try {
				File file = new File(imageJson.getString("localPath"));
				fileInputStream = new FileInputStream(file);

				ContentHandler contenthandler = new BodyContentHandler();
				Metadata metadata = new Metadata();
				metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
				AutoDetectParser parser = new AutoDetectParser();

				parser.parse(fileInputStream, contenthandler, metadata);
				for(int j = 0; j <metadata.names().length; j++) 
				{ 
					String name = metadata.names()[j]; 						
					metaJson.put(name, metadata.get(name));
				} 

				imageJson.put("MetaData", metaJson);
				System.out.println(imageJson.toString());

			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			}
			
		}
		
	

	private static JSONObject readJsonFile(String string) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		String json=IOUtils.toString(new FileReader(new File(string)));
		 JSONObject obj = new JSONObject(json);
		 return obj;
	}
}
