package AndroidApp.First.QuickCricketScore;

import AndroidApp.First.QuickCricketScore.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Application;

public class ScoreFetcher
{
	
	public  String getContent(String string)  
	{
		// TODO Auto-generated method stub
		try 
		{
			//create client
			HttpClient client = new DefaultHttpClient();
			//create the request
			HttpGet request = new HttpGet(string);
			//using client and request get the response 
			HttpResponse response = client.execute(request);
		
			String html = "";
			//get the response in input stream
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			StringBuilder str = new StringBuilder();
			String line = null;
			
			while((line = reader.readLine()) != null)
			{
				str.append(line);
			}
		
			return str.toString();
		} 
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public  Document stringToDom(String xmlSource) 
            throws SAXException, ParserConfigurationException, IOException 
            
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

	public ArrayList<String> getScoreBoardLinks(String html)
	{
		String[] links =  html.split("'");
		ArrayList<String> finalLinks = new ArrayList<String>();
		int j= 0;
		
		for(int i =0 ; i<links.length; i++)
		{
			if(links[i].contains(".html"))
			{
				finalLinks.add(links[i]);
				j++;
			}
		}
		return finalLinks;
	}

	public ArrayList<String> getXpathContent(String url,String xpathExpression)
	{
		
		String response =  getContent(url);
		
		ArrayList<String> links = getScoreBoardLinks(response);
			
		String myResponse = light_html2xml.Html2Xml(response);
		try
		{
				//creating new instance of XpathFactory
				XPathFactory xpathFactory = XPathFactory.newInstance();
				//creating xpath instance by using xpathfactory object
				XPath xPath = xpathFactory.newXPath();
				
				XPathExpression xPathExpression = xPath.compile(xpathExpression);
				//replacing xml encoding
				myResponse = myResponse.replace("iso-8859-1", "utf-16");
				//compile the expression to get XpathEpression object
				
				Document xmlDocument = stringToDom(myResponse);
				
				myResponse = xPathExpression.evaluate(xmlDocument);
				
				links.add(myResponse);
				
				return links;
		}
		catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
				
	}

	
}
