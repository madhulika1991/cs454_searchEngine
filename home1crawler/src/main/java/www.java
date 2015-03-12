import java.io.FileWriter;

import org.json.JSONArray;
import org.json.JSONObject;


public class www {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mpulic static void main
		public static void main
		console data()
		main.string
		public static void main(String args[]){
			
			String path="c:\\downloaded\\";
			String name="hw2.json";
			try {
			if(args!=null && args.length == 2 && args[0].equals("-c")){
				FileWriter file = new FileWriter(path+name, true);
				String fileName=args[1];
				
					JSONObject obj = readJsonFile(path + fileName);
					JSONArray arr=obj.getJSONArray("EmbeddedLinksInfo");
					public dtring
					void main()
					console data
					string path=c; download;
					string name=hw2.json file
							if(args=null args.lenthg==args.equals)
								file writer file= new file
		mata fiych dta
		crawler
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

}
