package log;


import org.apache.log4j.Logger; 



import org.apache.log4j.PropertyConfigurator; 



public class MyLogger { 



   static Logger logger = Logger.getLogger(initiator.Compiler.class.getName()); //First step 


   public static Logger getLogger() {
//	   PropertyConfigurator.configure("config/log4j.xml"); //Second step 
	   return logger;
   }

   public static void main(String args[]) { 





      logger.debug("Here is some DEBUG"); //Third step 



      logger.info("Here is some INFO"); 



      logger.warn("Here is some WARN"); 



      logger.error("Here is some ERROR"); 



      logger.fatal("Here is some FATAL"); 



   } 



} 
