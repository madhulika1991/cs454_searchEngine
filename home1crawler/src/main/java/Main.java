import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
 
public class Main {
	public static DB db = new DB();
	static String temp;
 
	public static void main(String[] args) throws SQLException, IOException {
		db.runSql2("TRUNCATE Record;");
		if(args.length==2){
		temp=args[0];
		processPage(args[0],Integer.parseInt(args[1]));
		}else{
			System.out.println("Enter exactly two VM argument");
			System.exit(1);
		}
		extractMetadata();
	}
 
	public static void processPage(String URL,int depth) throws SQLException, IOException{
		//check if the given URL is already in database
		
		if(depth>=0){
		try{
		String sql = "select url from Record where URL = '"+URL+"'";
		
		ResultSet rs = db.runSql(sql);
		if(rs.next()){
 
		//System.out.println(rs.getString("url"));
			
		}else{
			//store the URL to database to avoid parsing again
			//sql = "INSERT INTO  `cs320stu74`.`Record` " + "(`URL`) VALUES " + "(?);";
			sql = "INSERT INTO  `Record` " + "(`URL`) VALUES " + "(?);";
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, URL);
			stmt.execute();
 
			System.out.println(URL);
			//get useful information
			Document doc = Jsoup.connect(URL).get();
 
			depth=depth-1;
					
			//System.out.println(temp);
			//get all links and recursively call the processPage method
			Elements questions = doc.select("a[href]");
			for(Element link: questions){
				//System.out.println(link);
				//System.out.println(temp.split("\\.")[1]+"."+temp.split("\\.")[2]);
				
				//System.out.println(depth);
				if(link.attr("href").contains(temp.split("\\.")[1]+"."+temp.split("\\.")[2]) && !link.attr("href").contains("mailto")){
					//System.out.println(link.attr("abs:href"));
					processPage(link.attr("abs:href"),depth);
				}
			}
		}
		}catch(Exception e){
		//	e.printStackTrace();
		}
		}
	}
	
	
	public static void extractMetadata() throws SQLException, IOException{
		
		  String sql = "select url from Record";
			
			try {
				ResultSet rs = db.runSql(sql);
				while(rs.next()){
					
				String url=rs.getString("url");
				
				
				try{
				Document doc = Jsoup.connect(url).get();
				Elements questions = doc.select("meta");
				StringBuilder sb=new StringBuilder();
				for(Element link: questions){
					sb.append(link);
					
				}
				//System.out.println(sb);
				sql = "UPDATE `Record` SET metadata='"+sb+"' WHERE url='"+url+"';";
				
				 db.runSql2(sql);
				 System.out.println(url);
	 
				}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		
	}
}