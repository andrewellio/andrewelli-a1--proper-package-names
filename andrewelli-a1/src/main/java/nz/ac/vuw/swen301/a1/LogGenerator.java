package nz.ac.vuw.swen301.a1;

import java.util.*;

import org.apache.log4j.*;
/**
 * This class is responsible for logging events and using the log4j.properties file to format these logs in JSON format
 * @author Elliott Andrews
 */
public class LogGenerator {
	// TO RUN THIS LOG GENERATOR, please uncomment either the file appender or stout appender in the log4j.propeties file
	final static Logger logger = Logger.getLogger(LogGenerator.class);

	public static void main(String[] args) {
		LogGenerator logG = new LogGenerator();
		logG.generateLogs();
	}

	public void generateLogs(){
		logger.error("This is a error log");
		logger.warn("This is a warning log");
		logger.info("This is a info log");
		logger.debug("This is a debug log");
	}

}
