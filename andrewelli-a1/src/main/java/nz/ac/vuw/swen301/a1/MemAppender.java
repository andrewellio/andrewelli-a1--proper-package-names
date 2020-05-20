package nz.ac.vuw.swen301.a1;

import java.io.IOException;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.management.*;
import javax.management.remote.*;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.alibaba.fastjson.JSON;

public class MemAppender implements MemAppenderMBean, Appender {
	private List<LoggingEvent> CurrentLogs = new ArrayList<>();
	public List<LoggingEvent> getCurrentLogs() {return Collections.unmodifiableList(CurrentLogs);}
	
	//IMPORTANT NOTE: the variables that are static, need to be so for JVM monitoring to make sure that MBean
	//attributes update correctly. They will work fine without using static, but monitoring the MBean attributes will,
	//not be updated (will remain at 0).
	private static String name; //For the objectName when using MBeans
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}

	private long maxSize = 1000;  //Range in list limited to 0-999 (1000)
	public long getMaxSize() {return maxSize;}
	public void setMaxSize(long maxSize) {this.maxSize = maxSize;}

	private static long discardedLogCount = 0;
	public long getDiscardedLogCount() {return discardedLogCount;}
	
	private static long numLogs = 0; //Using a seperate counter rather than CurrentLogs.size to deal with needing a static 
	public long getLogCount() {return numLogs;}

	JSONLayout json = new JSONLayout(); 

	public String getLogs() {return json.getJsonArray();}
	
	//Sample for when monitiroing with MBeans
	//----------------------------------------------------------------------
		private String fqnOfCategoryClass, ndc = null;
		private ThrowableInformation throwable = null;
		private LocationInfo info = null;
		private java.util.Map properties = null;

		private Timestamp ts = new Timestamp(System.currentTimeMillis());
		private long timeStamp = ts.getTime();
		private String logTime = json.formatDate(timeStamp);

		Logger loggerName = Logger.getLogger("arrayfoo");
		Level levelE = Level.ERROR;
		Object messageE = "This is a test error log to be put into a list";
		String threadName = "main";
	 //------------------------------------------------------------------------

	//Uncomment the commented LoggingEvent eventTest... and CurrentLogs.add(eventTest); then
	//comment the CurrentLogs.add(event); to have a sample for MBeans monitoring
	//Note: When this code is changed to sample with MBeans monitoring, some MemAppender Junit tests will fail
	@Override 
	public void doAppend(LoggingEvent event) {
		while(CurrentLogs.size() >= maxSize) {
			CurrentLogs.remove(0);
			discardedLogCount++;
		}
		//LoggingEvent eventTest = new LoggingEvent(fqnOfCategoryClass, loggerName, timeStamp, levelE, messageE, threadName, 
		//		throwable, ndc, info, properties);
		//CurrentLogs.add(eventTest);
		
		CurrentLogs.add(event); 
		numLogs++;	
	}
		
    
	public void exportToJSON(String fileName) {
		String jsonArray = json.formatList(getCurrentLogs());

		try {
			Files.write(Paths.get(fileName), json.getJsonArray().getBytes());
		} catch (IOException ioe) {
			ioe.printStackTrace(); //Failed to write via IO
		}
	}

	public static void main(String[] args) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
		MemAppender mAppend = new MemAppender();
		mAppend.startRegisterJMX();
		while(true) {//To assist with MBean monitoring (a forever loop)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mAppend.doAppend(null); //Stand in null
			mAppend.exportToJSON("logsList.json");
		}
	}
	
	public void startRegisterJMX() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException{
		name = "nz.ac.vuw.swen301.a1:type=MemAppender";
		ObjectName objectName = new ObjectName(name);
		MBeanServer server = ManagementFactory.getPlatformMBeanServer(); //Exposes the MBeans Object
		server.registerMBean(new MemAppender(), objectName); 

	//Example of what monitoring the action/operation within MBean might have involved (exportingJsonFile)
        //  Create object name
        //    ObjectName  serviceConfigName = objectName; 
        //  Invoke operation
        //     server.invoke(serviceConfigName, "exportToJSON", null, null);
        //  Close JMX connector
        //    server.close();
      
    
	}
	
	@Override public JSONLayout getLayout() {
		return json;
	}
//Unimplemented and required methods for the appender interface
@Override public void setLayout(Layout layout) {}
@Override public void addFilter(Filter newFilter) {}
@Override public Filter getFilter() {return null;}
@Override public void clearFilters() {}
@Override public void close() {}
@Override public void setErrorHandler(ErrorHandler errorHandler) {}
@Override public ErrorHandler getErrorHandler() {return null;}
@Override public boolean requiresLayout() {return false;}
}
