package nz.ac.vuw.swen301.a1;

public interface MemAppenderMBean {
	//All methods from Mem appender you expect
	//These variable names for the getters are currently static in MemAppender
	
	public String getName(); //My optional property to be monitored (although won't change)
	
	public String getLogs();
	
	public long getLogCount();
	
	public long getDiscardedLogCount();
	
	//public void exportToJSON();
	//Didn't quite get to the monitoring this action part, but provided an example of some code
	//under jmx register in MemAppender

} 
