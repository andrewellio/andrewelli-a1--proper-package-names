package nz.ac.vuw.swen301.a1;

public class LogBasicStore { 
	private String logger;
	private String level;
	private String startTime;
	private String thread;
	private String message;

	/**
	 *  This is the constructor for a LogStore object which requires the five parameters below:
	 * @param logName the name for the log event
	 * @param logLevel the priority and type of importance for log event
	 * @param logTime the timestamp for when the log was created
	 * @param logthreadName the thread name for where the log was generated from
	 * @param logMessage additional information explaining the log event
	 */
	public LogBasicStore(String logger, String level, String startTime, String thread, String message) {
		this.logger = logger;
		this.level = level;
		this.startTime = startTime;
		this.thread = thread;
		this.message = message;
	}
	
	/**
	 * @return the log event name as a formatted String
	 */
	public String getLogger() {
		return logger;
	}
	
	/**
	 * @return the log event level as a string
	 */
	public String getLevel() {
		return level;
	}
	
	/**
	 * @return the log event timestamp as a formatted String
	 */
	public String getStarttime() {
		return startTime;
	}
	
	/**
	 * @return the log event thread name as a formatted String
	 */
	public String getThread() {
		return thread;
	}

	/**
	 * @return the log event message as a string
	 */
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "LogStore [logger=" + logger + "level=" 
	+ level + "starttime=" + startTime + "thread=" + thread + "message=" + message + "]";
	}
}
