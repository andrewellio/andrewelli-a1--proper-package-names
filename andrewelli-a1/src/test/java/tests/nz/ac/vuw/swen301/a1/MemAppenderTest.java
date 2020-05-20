package tests.nz.ac.vuw.swen301.a1;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

import nz.ac.vuw.swen301.a1.JSONLayout;
import nz.ac.vuw.swen301.a1.LogBasicStore;
import nz.ac.vuw.swen301.a1.MemAppender;

/**
 * Unit test for JSONLayout.java > formatList(List<LoggingEvent> logs) demonstrating a ERROR, WARNING, INFO and DEBUG logs
 */
public class MemAppenderTest{

	private String fqnOfCategoryClass, ndc = null;
	private ThrowableInformation throwable = null;
	private LocationInfo info = null;
	private java.util.Map properties = null;

	JSONLayout json = new JSONLayout(); 

	private Timestamp ts = new Timestamp(System.currentTimeMillis());
	private long timeStamp = ts.getTime();
	private String logTime = json.formatDate(timeStamp);

	Logger loggerName = Logger.getLogger("arrayfoo");
	String threadName = "main";

	Level levelE = Level.ERROR;
	Object messageE = "This is a test error log to be put into a list";
	LoggingEvent eventerrorLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelE, messageE, threadName, 
			throwable, ndc, info, properties);

	Level levelW = Level.WARN;
	Object messageW = "This is a test warn log to be put into a list";
	LoggingEvent eventwarnLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelW, messageW, threadName, 
			throwable, ndc, info, properties);

	Level levelI = Level.INFO;
	Object messageI = "This is a test info log to be put into a list";
	LoggingEvent eventinfoLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelI, messageI, threadName, 
			throwable, ndc, info, properties);

	Level levelD = Level.DEBUG;
	Object messageD = "This is a test debug log to be put into a list";
	LoggingEvent eventdebugLog = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelD, messageD, threadName, 
			throwable, ndc, info, properties);

	MemAppender mAppend = new MemAppender();

	@Test public void nameSet_NameGet() { 
		mAppend.setName("myLogger");
		assertEquals("myLogger", mAppend.getName());
	}

	@Test public void maxSizeGet_DefaultValue() { 
		long expected = 1000; //as junit uses int as default for assertEquals
		assertEquals(expected, mAppend.getMaxSize());
	}

	@Test public void maxSizeSet_MaxSizeGet() { 
		mAppend.setMaxSize(525);
		long expected = 525;
		assertEquals(expected, mAppend.getMaxSize());
	}
	
	@Test public void getLayout() { 
		assertTrue(mAppend.getLayout().getClass().isInstance(json));
		//JSONlayouts will never be exactly the same, so as long as it is an instance, it is acceptablee
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void tryEditUnmodifiableList() {
		mAppend.getCurrentLogs().add(eventerrorLog);
	}

	@Test public void ListSizeGet_Added1000Logs() { //2 inner tests
		//Cannot test starting discardedLogCount assert = 0 here becuase of the way that the next test is run
		//(i.e. order of junit tests) futhermore becuase how discaredLogCount works as a static variable
		for (int i=0;i<mAppend.getMaxSize();i++) {//Just under limit
			mAppend.doAppend(eventerrorLog);
		}
		assertEquals(1000, mAppend.getCurrentLogs().size());
	}

	@Test public void discardedLogCountGet_ListSizeGet_Added1001Logs() { //3 inner tests
		//Only test of discaredLogCount being initalised, due to it being static (i.e. as static is required for MBeans)
		long expected = 0; 
		assertEquals(expected, mAppend.getDiscardedLogCount());
		for (int i=0;i<mAppend.getMaxSize()+1;i++) {//Just over limit
			mAppend.doAppend(eventerrorLog);
		}
		long expected2 = 1;
		assertEquals(expected2, mAppend.getDiscardedLogCount());
		assertEquals(1000, mAppend.getCurrentLogs().size());
	}
	
	@Test public void exportJson_FileName_Exists() { 
		mAppend.exportToJSON("myfile.json");
		File fileName = new File("myfile.json");
		assertTrue(fileName.exists());
		fileName.delete(); //To remove any previous junit runs
	}

	@Test public void addErrorLog_LoggerPropertiesParsed() { //4 inner tests
		mAppend.doAppend(eventerrorLog);
		mAppend.exportToJSON("singleErrorLog.json");

		//LoggingEventList >> LogBasicStoreList >> JSONStringArray >> WriteToFile - This is the normal exporting process to file
		//ReadFromFile >> JSONStringArray >> LogBasicStoreList - This is the importing process to check and test log properties

		List<LogBasicStore> returnedLogsList = new ArrayList<>();

		//Reading from the file and follwing the importing process
		try (Stream<String> lines = Files.lines(Paths.get("singleErrorLog.json"))) {
			String fileContent = lines.collect(Collectors.joining());
			returnedLogsList = JSON.parseArray(fileContent, LogBasicStore.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(LogBasicStore log : returnedLogsList) { //Will just be one log in this test case
			assertEquals(loggerName.getName(), log.getLogger());	
			assertEquals(levelE.toString(), log.getLevel());	
			assertEquals(logTime, log.getStarttime());  
			assertEquals(threadName, log.getThread());    
			assertEquals(messageE, log.getMessage());
		}
		File fileName = new File("singleErrorLog.json");
		fileName.delete();
	}
	
	@Test public void addFourLogsLevels_LoggerPropertiesParsed_MixedPropertiesMatch() { //4 inner tests
		mAppend.doAppend(eventerrorLog);
		mAppend.doAppend(eventwarnLog);
		mAppend.doAppend(eventinfoLog);
		mAppend.doAppend(eventdebugLog);
		mAppend.exportToJSON("mixedLogs.json");
		int error = 0; int warn = 1; int info = 2; int debug = 3;

		List<LogBasicStore> logs = new ArrayList<>();

		try (Stream<String> lines = Files.lines(Paths.get("mixedLogs.json"))) {
			String fileContent = lines.collect(Collectors.joining());
			logs = JSON.parseArray(fileContent, LogBasicStore.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(loggerName.getName(), logs.get(error).getLogger());	
		assertEquals(levelW.toString(), logs.get(warn).getLevel());	
		assertEquals(logTime, logs.get(error).getStarttime());  
		assertEquals(threadName, logs.get(info).getThread());    
		assertEquals(messageD, logs.get(debug).getMessage());
		
		File fileName = new File("mixedLogs.json");
		fileName.delete();
	}

	@Test public void addFourLogLevels_LoggerPropertiesMatch_ListSizeGet() { //18 inner tests
		mAppend.doAppend(eventerrorLog);
		mAppend.doAppend(eventwarnLog);
		mAppend.doAppend(eventinfoLog);
		mAppend.doAppend(eventdebugLog);
		mAppend.exportToJSON("multipleLogLevels.json");

		List<LogBasicStore> logs = new ArrayList<>();
		int error = 0; int warn = 1; int info = 2; int debug = 3;

		try (Stream<String> lines = Files.lines(Paths.get("multipleLogLevels.json"))) {
			String fileContent = lines.collect(Collectors.joining());
			logs = JSON.parseArray(fileContent, LogBasicStore.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals(loggerName.getName(), logs.get(error).getLogger());	
		assertEquals(levelE.toString(), logs.get(error).getLevel());	
		assertEquals(logTime, logs.get(error).getStarttime());  
		assertEquals(threadName, logs.get(error).getThread());    
		assertEquals(messageE, logs.get(error).getMessage());
		
		assertEquals(loggerName.getName(), logs.get(warn).getLogger());	
		assertEquals(levelW.toString(), logs.get(warn).getLevel());	
		assertEquals(logTime, logs.get(warn).getStarttime());  
		assertEquals(threadName, logs.get(warn).getThread());    
		assertEquals(messageW, logs.get(warn).getMessage());
		
		assertEquals(loggerName.getName(), logs.get(info).getLogger());	
		assertEquals(levelI.toString(), logs.get(info).getLevel());	
		assertEquals(logTime, logs.get(info).getStarttime());  
		assertEquals(threadName, logs.get(info).getThread());    
		assertEquals(messageI, logs.get(info).getMessage());
		
		assertEquals(loggerName.getName(), logs.get(debug).getLogger());	
		assertEquals(levelD.toString(), logs.get(debug).getLevel());	
		assertEquals(logTime, logs.get(debug).getStarttime());  
		assertEquals(threadName, logs.get(debug).getThread());    
		assertEquals(messageD, logs.get(debug).getMessage());
		
		assertEquals(4, mAppend.getCurrentLogs().size());
		assertEquals(4, logs.size());

		File fileName = new File("multipleLogLevels.json");
		fileName.delete();
	}

}