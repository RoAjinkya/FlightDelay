package edu.neu.csye7200.util;
import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

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

	
	public  JSONObject  GetResponse(String FlightNumber, Date Date1,String Origin, String Dest) throws IOException, JSONException {
		
		System.out.println(FlightNumber +"  "+Date1 +" " +Origin+" "+Dest);
		
		 String result = null;

		    try {
		      Class<?> clazz = Class.forName("com.example.Foo$"); // Note the trailing '$'
		      Method method = clazz.getDeclaredMethod("bar");
		      Field field = clazz.getField("MODULE$");
		      Object instance = field.get(null);
		      Object obj = method.invoke(instance, new Object[] {});

		      if (obj instanceof String) {
		        result = (String) obj;
		      }

		    } catch (Exception ex) {
		      // SWALLOWING
		    }
		   // return result;
		
		//Call Model with Parameters get values in Json Format or string or arraylist
		return null;
		
		

		
	}

}
