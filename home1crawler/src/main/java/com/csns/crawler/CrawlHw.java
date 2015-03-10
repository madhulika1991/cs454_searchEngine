package com.csns.crawler;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlHw {
	private static String temp;

	private static String path = "c:\\downloaded\\";
	private static List<String> exts = new ArrayList<String>(Arrays.asList("jpg",
			"jpeg", "png", "gif", "doc", "docx", "xls", "xlsx", "pdf"));
	private static List<String> visitedList=new ArrayList<String>();
	static JSONObject baseObj = new JSONObject();
	static JSONArray arr = new JSONArray();

	public static void main(String[] args) throws SQLException, IOException {
		
		if (args.length == 4) {
			temp = args[3];
			String jsonFileName="CntrlFile.json";
			FileWriter file = new FileWriter(path+jsonFileName, true);
			baseObj.put("BaseUrl", temp);
			baseObj.put("EmbeddedLinksInfo",arr);
			processPage(Integer.parseInt(args[1]),args[3]);
			file.write(baseObj.toString());
			file.flush();
			file.close();
		} else {
			System.out.println("Usage: java -jar crawler.jar -d <depth> -u <url> ");
			System.exit(1);
		}
		//extractMetadata();
	}

	public static void processPage(int depth,String URL) throws SQLException,
			IOException {
		// check if the given URL is already in database
		Document doc;
		if (depth >= 0) {
			if(!visitedList.contains(URL)){
				try{
					String urlExt = FilenameUtils.getExtension(URL);

					System.out.println(URL);
					if (urlExt != null && urlExt.length() > 0
							&& exts.contains(urlExt)) {

						JSONObject obj = new JSONObject();
						try {
							URL url = new URL(URL);
							InputStream in = url.openStream();
							String name=UUID.randomUUID()
									.toString() + "." + urlExt;
							OutputStream out = new BufferedOutputStream(
									new FileOutputStream(path+name));

							for (int b; (b = in.read()) != -1;) {
								out.write(b);
							}

							out.close();
							in.close();
							obj.put("url", URL);
							obj.put("localFilePath",path+name);		
							obj.put("lastUpdatedDate", new Date());
							JSONArray array = new JSONArray();
							obj.put("images", array);
							obj.put("links", array);
							arr.put(obj);
						} catch (Exception ex) {
							System.out.println(ex);
						}
					} else {
						// get useful information
						doc = Jsoup.connect(URL).get();

						
						downloadPage(URL, doc);
						
						depth = depth - 1;

						// System.out.println(temp);
						// get all links and recursively call the processPage
						// method
						visitedList.add(URL);
						Elements questions = doc.select("a[href]");
						for (Element link : questions) {
							
							if (link.attr("href").contains(
									temp.split("\\.")[1] + "."
											+ temp.split("\\.")[2])
									&& !link.attr("href").contains("mailto")
									&& !link.attr("href").startsWith("https")) {
								// System.out.println(link.attr("abs:href"));
								processPage(depth,link.attr("abs:href"));
							}
						}
					
					}
			} catch (Exception e) {
				 System.out.println(e);
			}
		}
		}
		
		}
	

	private static void downloadPage(String uRL, Document doc) throws IOException {
		// TODO Auto-generated method stub

		
		FileWriter fileWriter;
		String htmlName;
		URL url;
		
		JSONObject obj = new JSONObject();
		htmlName = UUID.randomUUID().toString() + ".html";

		System.out.println(uRL + " " + htmlName);
		
		Elements imgs=doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		JSONArray imgArray = new JSONArray();
		for(Element img:imgs){
			JSONObject imgObj = new JSONObject();
			try {

				String src=img.attr("abs:src");
				
					url=new URL(src);
				
				String urlExt = FilenameUtils.getExtension(src);
				InputStream in = url.openStream();

				if(urlExt.contains("?")){
					String extCh=urlExt.split("\\?")[0];
					urlExt=extCh;

				}
				
				String name=UUID.randomUUID()
						.toString() + "." + urlExt;
				OutputStream out = new BufferedOutputStream(
						new FileOutputStream(path+name));

			
				for (int b; (b = in.read()) != -1;) {
					out.write(b);
				}

				out.close();
				in.close();
				imgObj.put("src", src);
				imgObj.put("localPath", path+name);
			    imgArray.put(imgObj);
			} catch (Exception ex) {
				System.out.println(ex);
			}
			
		}
		Elements questions = doc.select("a[href]");
		JSONArray linksArray = new JSONArray();
		for (Element link : questions) {
			JSONObject linkObj = new JSONObject();
			
			linkObj.put("url", link.attr("abs:href"));
			linksArray.put(linkObj);
				
			
		}

		try {
			fileWriter = new FileWriter(path + htmlName);
			fileWriter.write(doc.body().html());
			fileWriter.close();
			obj.put("url", uRL);
			obj.put("localFilePath",path+htmlName);
			obj.put("title", doc.title());		
			obj.put("lastUpdatedDate", new Date());
			obj.put("images", imgArray);
			obj.put("links", linksArray);
			arr.put(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			FileUtils.forceDelete(new File(path+htmlName));
			System.out.println(e);
		}

	}

	
}