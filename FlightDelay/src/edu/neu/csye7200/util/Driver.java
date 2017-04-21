package edu.neu.csye7200.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.print.attribute.standard.Destination;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Driver {

	private static final String FILENAME = "E:\\test\\filename.txt";

	public  String  GetResponse(String FlightNumber, Date Date1,String Origin, String Dest,String OriginRain, String OriginSnow,String DestRain, String DestSnow) throws IOException, JSONException {
		
		System.out.println(FlightNumber +"  "+Date1 +" " +Origin+" "+Dest  +" " +OriginRain +" "+OriginSnow +" "+DestRain+" "+DestSnow);
		
		 String result = null;
		 
		 try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {

			 Calendar c = Calendar.getInstance();
			 c.setTime(Date1);
			 int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
				String content = "1 1:"+ dayOfWeek+"  2:"+OriginSnow+" 3:"+OriginRain+" 4:"+DestSnow+" 5:"+DestRain+" "+Origin+":1 "+Dest+":1" ;
					System.out.println(content);
				bw.write(content);

				// no need to close it.
				//bw.close();

				System.out.println("Done");

			} catch (IOException e) {

				e.printStackTrace();

			}
		 
		 
		 
		 //   String sourceFile1Path = "/home/programcreek/Desktop/s1";
			String sourceFile2Path = "E:\\test\\file2.libsvm";
	 
			String mergedFilePath = "E:\\test\\filenamemerged.libsvm";
	 
			File[] files = new File[2];
			files[0] = new File(FILENAME);
			files[1] = new File(sourceFile2Path);
	 
			File mergedFile = new File(mergedFilePath);
	 
			mergeFiles(files, mergedFile);
		 
			//Call to the method
			//String  filename = "E:\\test\\file2.libsvm";
			
			return   readresponse("E:\\test\\Response.txt");
			
		
	}
	
	public static String readresponse(String Filename){
		String sCurrentLine1 = "";
		try (BufferedReader br = new BufferedReader(new FileReader(Filename))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				sCurrentLine1 += sCurrentLine;  
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sCurrentLine1;
			
	}

	public static void mergeFiles(File[] files, File mergedFile) {
 
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(mergedFile, true);
			 out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
 
		for (File f : files) {
			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
 
				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}
 
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}

}
