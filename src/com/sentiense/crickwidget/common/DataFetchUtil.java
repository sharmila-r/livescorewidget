/**
 * CricketScoreWidget
 */
package com.sentiense.crickwidget.common;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.developerworks.android.FeedParser;
import org.developerworks.android.FeedParserFactory;
import org.developerworks.android.Message;
import org.developerworks.android.ParserType;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

/**
 * @author asus
 * Dec 28, 2011
 * 
 */
public class DataFetchUtil {
	private static List<Message> messages;
	
	public static List<String> loadFeed(ParserType type) {
		List<String> titles = null;
		try {
			FeedParser parser = FeedParserFactory.getParser(type);
//			long start = System.currentTimeMillis();
			messages = parser.parse();
			titles = new ArrayList<String>(messages.size());
			for (Message msg : messages) {
				titles.add(msg.getTitle());
			}
		} catch (Throwable t) {
			Log.e("DataFetchUtil", t.getMessage(), t);
		}
		return titles;
	}

	public static String writeXml() {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "messages");
			serializer.attribute("", "number", String.valueOf(messages.size()));
			for (Message msg : messages) {
				serializer.startTag("", "message");
				// serializer.attribute("", "date", msg.getDate());
				serializer.startTag("", "title");
				serializer.text(msg.getTitle());
				serializer.endTag("", "title");
				serializer.startTag("", "url");
				serializer.text(msg.getLink().toExternalForm());
				serializer.endTag("", "url");
				serializer.startTag("", "body");
				serializer.text(msg.getDescription());
				serializer.endTag("", "body");
				serializer.endTag("", "message");
			}
			serializer.endTag("", "messages");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static  String getCountryNames(String feedString){
		String returnVal="";
		String[] resultSet = feedString.split(" v ");
		for(String country:resultSet){
		for(String word:country.split("\\d")){
//		    System.out.println(word);
			returnVal=returnVal+word;
		    break;
		}
		returnVal+=" v ";
		}
		returnVal = returnVal.substring(0,returnVal.lastIndexOf(" v "));
		return returnVal;
	}
}
