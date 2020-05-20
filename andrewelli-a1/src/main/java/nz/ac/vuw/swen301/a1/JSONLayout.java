package nz.ac.vuw.swen301.a1;
import java.text.Format;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.alibaba.fastjson.JSON;

public class JSONLayout extends Layout{
	private String formattedDate;
	private String jsonArray;

	public String getJsonArray() {
		//Used for monitoring in MBeans (static is needed)
		String jsonArrayMBean = jsonArray;
		return jsonArrayMBean;
		//Essentially the same array string but a copy to avoid needing to use staic on jsonArray
	}

	/**
	 * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
	 * The default pattern just produces the application supplied message.
	 */
	public JSONLayout() {}

	/**
	 * Does not do anything as options become effective
	 */
	public void activateOptions() {}

	/**
	* The PatternLayout does not handle the throwable contained within {@link LoggingEvent LoggingEvents}. Thus, it returns <code>true</code> @since 0.8.4 
	*/
	public boolean ignoresThrowable() {return true;}

	public String formatDate(long timeStamp) {
		Date dateTime = new Date(timeStamp);
		Format dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		formattedDate = dateFormat.format(dateTime);
		return formattedDate;
	}

	/**
	 * Produces a formatted json string and will either use this method or the method below dependening on if a list is involved
	 * Reference: https://mkyong.com/java/fastjson-convert-java-objects-to-from-json/
	 */
	public String format(LoggingEvent event) { //Formats and returns a normal Json String
		LogBasicStore logInfo;
		String jsonStore;

		String logName;
		String logLevel;
		String logTime;
		String logthreadName;
		String logMessage;

		long logunformattedTime;

		logName = event.getLoggerName();
		logLevel = event.getLevel().toString();
		logunformattedTime = event.getTimeStamp();
		logTime = formatDate(logunformattedTime);
		logthreadName = event.getThreadName();
		logMessage = event.getMessage().toString();

		logInfo = new LogBasicStore(logName, logLevel, logTime, logthreadName, logMessage);

		jsonStore = JSON.toJSONString(logInfo, true);

		return jsonStore;
	}

	public String formatList(List<LoggingEvent> logs) { //Formats and returns a Json Array
		List<LogBasicStore> jsonLogs = new ArrayList<>();
		LogBasicStore logInfo;

		String logName;
		String logLevel;
		String logTime;
		String logthreadName;
		String logMessage;

		long logunformattedTime;

		for(LoggingEvent event : logs) {
			logName = event.getLoggerName();
			logLevel = event.getLevel().toString();
			logunformattedTime = event.getTimeStamp();
			logTime = formatDate(logunformattedTime);
			logthreadName = event.getThreadName();
			logMessage = event.getMessage().toString();
			logInfo = new LogBasicStore(logName, logLevel, logTime, logthreadName, logMessage);
			jsonLogs.add(logInfo);
		}

		jsonArray = JSON.toJSONString(jsonLogs, true);
		return jsonArray;
	}
}
