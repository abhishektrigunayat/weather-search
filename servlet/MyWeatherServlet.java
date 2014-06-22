package com.myweather.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Servlet implementation class MyWeatherServlet
 */
@WebServlet("/MyWeatherServlet")
public class MyWeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public MyWeatherServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String location, locationType, temperatureUnit = null;
		URL objUrl = null;
		URLConnection objURLConnection = null;
		InputStream objInputStream = null;
		String feed = null,link = null,img = null,city = null,region = null,country = null,temperature = null,text = null,temp = null;
		String day = null,low = null,high = null,ftext = null;
		try{
			location = request.getParameter("location");
			locationType = request.getParameter("location_type");
			temperatureUnit = request.getParameter("temperature_unit");
			System.out.println("Location : " + location);
			System.out.println("Location Type: " + locationType);
			System.out.println("Temperature Unit : " + temperatureUnit);
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			//response.getWriter().println("Hello");
			//String url = "http://cs-server.usc.edu:34066/MyWeatherPHP.php?location="+URLEncoder.encode(location, "UTF-8")+"&locationType="+URLEncoder.encode(locationType, "UTF-8")+"&unit="+URLEncoder.encode(temperatureUnit, "UTF-8");
			String url = "http://default-environment-enimzhypjz.elasticbeanstalk.com/?location="+URLEncoder.encode(location, "UTF-8")+"&locationType="+URLEncoder.encode(locationType, "UTF-8")+"&unit="+URLEncoder.encode(temperatureUnit, "UTF-8");
			System.out.println("Url to invoke : " + url);
			objUrl = new URL(url);
			objURLConnection = objUrl.openConnection();
			objURLConnection.setAllowUserInteraction(false);
			/*
			objInputStream = objUrl.openStream();
			int num = objInputStream.available();
			System.out.println("Number of bytes available to read : " + num);
			byte[] outputByte = new byte[num];
			objInputStream.read(outputByte, 0, num);
			String output = new String(outputByte);
			System.out.println("Data got from PHP response : " + output);
			*/
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse ( objUrl.openStream());
	        doc.getDocumentElement ().normalize ();
	        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	        JSONObject jsonWeather = new JSONObject();
	        JSONArray jsonForecast = new JSONArray();
	        NodeList nodeList = doc.getDocumentElement().getChildNodes();
	        for(int i=0;i<nodeList.getLength();i++){
	        	Node node = nodeList.item(i);
	        	if(node.getNodeName().equals("feed")){
	        		if(node.hasChildNodes()){
	        			feed = node.getFirstChild().getNodeValue();
	        		}
	        		
	        	}
	        	else if(node.getNodeName().equals("link")){
	        		if(node.hasChildNodes()){
	        			link = node.getFirstChild().getNodeValue();
	        		}
	        	}
	        	else if(node.getNodeName().equals("location")){
	        		if(node.hasAttributes()){
	        			Element element = (Element) node;
	        			city = element.getAttribute("city");
	        			region = element.getAttribute("region");
	        			country = element.getAttribute("country");
	        		}
	        	}
	        	else if(node.getNodeName().equals("units")){
	        		if(node.hasAttributes()){
	        			Element element = (Element) node;
	        			temperature = element.getAttribute("temperature");
	        		}
	        	}
	        	else if(node.getNodeName().equals("condition")){
	        		if(node.hasAttributes()){
	        			Element element = (Element) node;
	        			text = element.getAttribute("text");
	        			temp = element.getAttribute("temp");
	        		}
	        	}
	        	else if(node.getNodeName().equals("img")){
	        		if(node.hasChildNodes()){
	        			img = node.getFirstChild().getNodeValue();
	        		}
	        	}
	        	else if(node.getNodeName().equals("forecast")){
	        		if(node.hasAttributes()){
	        			JSONObject objTemp = new JSONObject();
	        			Element element = (Element) node;
	        			day = element.getAttribute("day");
	        			low = element.getAttribute("low");
	        			high = element.getAttribute("high");
	        			ftext = element.getAttribute("text");
	        			objTemp.put("text", ftext);
	        			objTemp.put("high", high);
	        			objTemp.put("day", day);
	        			objTemp.put("low", low);
	        			jsonForecast.add(objTemp);
	        		}
	        	}
	        }
	        jsonWeather.put("forecast", jsonForecast);
	        //System.out.println("Finally JSON object created : " + jsonWeather);
	        JSONObject objtemp = new JSONObject();
	        objtemp.put("text", text);
	        objtemp.put("temp", temp);
	        jsonWeather.put("condition",objtemp);
	        objtemp = new JSONObject();
	        objtemp.put("region", region);
	        objtemp.put("country", country);
	        objtemp.put("city", city);
	        jsonWeather.put("location",objtemp);
	        jsonWeather.put("link",link);
	        jsonWeather.put("img",img);
	        jsonWeather.put("feed",feed);
	        objtemp = new JSONObject();
	        objtemp.put("temperature", temperature);
	        jsonWeather.put("units",objtemp);
	        JSONObject jsonFinal = new JSONObject();
	        jsonFinal.put("weather",jsonWeather);
	        System.out.println("Finally JSON object created : " + jsonFinal);
	        response.getWriter().println(jsonFinal);
	        
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
