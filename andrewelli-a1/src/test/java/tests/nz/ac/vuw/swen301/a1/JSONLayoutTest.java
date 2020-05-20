package tests.nz.ac.vuw.swen301.a1;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

import nz.ac.vuw.swen301.a1.JSONLayout;
import nz.ac.vuw.swen301.a1.LogBasicStore;

/**
 * Unit test for JSONLayout.java > format(LoggingEvent event) demonstrating a ERROR, WARNING, INFO and DEBUG logs
 */
public class JSONLayoutTest{
	
	//LoggingEvent constructor parameters that are not needed for testing the JSON parsing
	String fqnOfCategoryClass, ndc = null;
	ThrowableInformation throwable = null;
    LocationInfo info = null;
	java.util.Map properties = null;
	
	//Directly calling the JSONLayout which extends the super class (Layout)
	JSONLayout json = new JSONLayout(); 
	
	//LoggingEvent parameter logTime, to follow the same creation via the log and use the existing formatDate in JSONLayout
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    long timeStamp = ts.getTime();
    String logTime = json.formatDate(timeStamp);
    
    //Other LoggingEvent parameters that are used in the JSON log representation and are to be 'expected' when assertEquals is called
    Logger loggerName = Logger.getLogger("foobar");
    Level levelE = Level.ERROR;
    Object messageE = "This is a error log";
    String threadName = "main";
    
    //Creating the Logging event to represent logger.error("This is a error log");
	LoggingEvent eventerrorLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelE, messageE, threadName, 
			throwable, ndc, info, properties);
	String jsonStringE = json.format(eventerrorLog);
	
	//Parse the JSONString back into a LogStore object
	LogBasicStore logStoreE = JSON.parseObject(jsonStringE, LogBasicStore.class);
	
	@Test public void errorLog_JsonParsed_LogName() {assertEquals(loggerName.getName(), logStoreE.getLogger());}
	
	@Test public void errorLog_JsonParsed_LogLevel() {assertEquals(levelE.toString(), logStoreE.getLevel());}
	
    @Test public void errorLog_JsonParsed_LogTime() {assertEquals(logTime, logStoreE.getStarttime());}
    
    @Test public void errorLog_JsonParsed_LogThreadName() {assertEquals(threadName, logStoreE.getThread());}
    
    @Test public void errorLog_JsonParsed_LogMessage() {assertEquals(messageE, logStoreE.getMessage());}
    
    
    
    
    Level levelW = Level.WARN;
    Object messageW = "This is a warning log";
	LoggingEvent eventwarnLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelW, messageW, threadName, 
			throwable, ndc, info, properties);
	String jsonStringW = json.format(eventwarnLog);
	LogBasicStore logStoreW = JSON.parseObject(jsonStringW, LogBasicStore.class);
	
	@Test public void warnLog_JsonParsed_LogName() {assertEquals(loggerName.getName(), logStoreW.getLogger());}
	
	@Test public void warnLog_JsonParsed_LogLevel() {assertEquals(levelW.toString(), logStoreW.getLevel());}
	
    @Test public void warnLog_JsonParsed_LogTime() {assertEquals(logTime, logStoreW.getStarttime());}
    
    @Test public void warnLog_JsonParsed_LogThreadName() {assertEquals(threadName, logStoreW.getThread());}
    
    @Test public void warnLog_JsonParsed_LogMessage() {assertEquals(messageW, logStoreW.getMessage());}
    
   
    
    
    Level levelI = Level.INFO;
    Object messageI = "This is a info log";
	LoggingEvent eventinfoLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelI, messageI, threadName, 
			throwable, ndc, info, properties);
	String jsonStringI = json.format(eventinfoLog);
	LogBasicStore logStoreI = JSON.parseObject(jsonStringI, LogBasicStore.class);
	
	@Test public void infoLog_JsonParsed_LogName() {assertEquals(loggerName.getName(), logStoreE.getLogger());}
	
	@Test public void infoLog_JsonParsed_LogLevel() {assertEquals(levelI.toString(), logStoreI.getLevel());}
	
    @Test public void infoLog_JsonParsed_LogTime() {assertEquals(logTime, logStoreI.getStarttime());}
    
    @Test public void infoLog_JsonParsed_LogThreadName() {assertEquals(threadName, logStoreI.getThread());}
    
    @Test public void infoLog_JsonParsed_LogMessage() {assertEquals(messageI, logStoreI.getMessage());}
    
    
    
    
    Level levelD = Level.DEBUG;
    Object messageD = "This is a debug log";
	LoggingEvent eventdebugLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelD, messageD, threadName, 
			throwable, ndc, info, properties);
	String jsonStringD = json.format(eventdebugLog);
	LogBasicStore logStoreD = JSON.parseObject(jsonStringD, LogBasicStore.class);
	
	@Test public void debugLog_JsonParsed_LogName() {assertEquals(loggerName.getName(), logStoreE.getLogger());}
	
	@Test public void debugLog_JsonParsed_LogLevel() {assertEquals(levelD.toString(), logStoreD.getLevel());}
	
    @Test public void debugLog_JsonParsed_LogTime() {assertEquals(logTime, logStoreI.getStarttime());}
    
    @Test public void debugLog_JsonParsed_LogThreadName() {assertEquals(threadName, logStoreD.getThread());}
    
    @Test public void debugLog_JsonParsed_LogMessage() {assertEquals(messageD, logStoreD.getMessage());}
      
    @Test public void debugLog_LogStoreFormat_ToStringAll() {
    	String expected = "LogStore [logger=" + loggerName.getName() + "level=" + 
    			levelD + "starttime=" + logTime + "thread=" + threadName + "message=" + messageD + "]";
    	assertEquals(expected, logStoreD.toString());
    }
    
}
