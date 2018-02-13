package com.deloitte.testng_hybrid_framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.testng.TestNG;
import org.testng.annotations.Listeners;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import com.deloitte.testng_hybrid_framework.Keywords;
import com.deloitte.testng_hybrid_framework.util.Constants;
import com.deloitte.testng_hybrid_framework.util.Listener;
import com.deloitte.testng_hybrid_framework.util.Xls_Reader;
import com.relevantcodes.extentreports.ExtentTest;
@Listeners(Listener.class)


public class XmlGenerator {
    public static ExtentTest test;
    public static Keywords app;
    public static XmlSuite suite;
    private static final Logger logger = Logger.getLogger(XmlGenerator.class.getName());
    public static XmlGenerator obj = new XmlGenerator();
    public static String packageName;
    public static TestNG ts = new TestNG();
    public static Properties prop;
    
    public XmlGenerator(){
    	prop = new Properties();
		try{
			FileInputStream fs;
			fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//project.properties");
			prop.load(fs);
		}
		catch (IOException e){
			e.printStackTrace();
			app.getGenericKeywords().logInfo("FATAL", "Not able to load property file");
		}
    }

    // =============MasterSuit Generator========================================
    public static void masterSuitGeneration(Xls_Reader xls) throws IOException {
    	try{
           suite = new XmlSuite();
           suite.setName("Master suite");
           List<String> files = new ArrayList<String>();
           int rows = xls.getRowCount(Constants.TESTSUIT_SHEET);
           String RunMode = "true";
           for (int rnum = 2; rnum <= rows; rnum++) {
                 String tsid = xls.getCellData(Constants.TESTSUIT_SHEET, Constants.TSID_COL, rnum);
                 RunMode = xls.getCellData(Constants.TESTSUIT_SHEET, Constants.RUNMODE_COL, rnum);
                 if (RunMode.equals("Y")) {
                       files.add(System.getProperty("user.dir") + "\\src\\test\\resources\\" + tsid + ".xml");
                 }
           }
           suite.setSuiteFiles(files);
           FileWriter writer = new FileWriter(
                        new File(System.getProperty("user.dir") + "//src//test//resources//MasterSuite.xml"));
           writer.write(suite.toXml());
           writer.flush();
           writer.close();
           System.out.println(new File("MyMasterSuite.xml").getAbsolutePath());
    	}catch(Exception exception){
    		exception.printStackTrace();
    		app.getGenericKeywords().logInfo("FATAL", "Not able to create Master Suite");
    	}
    }

    // ==========Suit Files Generator=============================

    public static void creatTestNgSuiteXml() throws IOException {
    	try{
    	   String suiteName = "";
           File file = new File(Constants.SUITE_PATH);
           File[] files = file.listFiles();
           for (File f : files) {
                 String fileName = f.getName();
                 if (fileName.endsWith("xlsx")){
                	 String[] fullFileName = (fileName.split(".xlsx"));
                 	 suiteName = fullFileName[0];
                 }
                 else{
                	 String[] fullFileName = (fileName.split(".xls"));
                	 suiteName = fullFileName[0];
                 }                 
                 Xls_Reader xls = new Xls_Reader(Constants.SUITE_PATH + "\\" + fileName);
                 suitFileGeneration(xls, suiteName);
           }
    	}catch(Exception exception){
    		exception.printStackTrace();
    		app.getGenericKeywords().logInfo("FATAL", "Not able to create TestNgSuite.xml");
    	}
    }

    // =======================================File Generation========================================

    @SuppressWarnings("deprecation")
	public static void suitFileGeneration(Xls_Reader xls, String SuiteName) throws IOException {
    	try{
           suite = new XmlSuite();
           packageName = obj.getClass().getPackage().getName();
           if (prop.getProperty("parallel").equals("Y")) {
        	   suite.setParallel("classes");
               String x = prop.getProperty("threadCount");
               suite.setThreadCount(Integer.parseInt(x));
               suite.setName(SuiteName);
          	   int rows = xls.getRowCount(Constants.TESTCASES_SHEET);
          	   for (int rnum = 2; rnum <= rows; rnum++) {
          		   String RunMode = "true";
          		   String tcid = xls.getCellData(Constants.TESTCASES_SHEET, Constants.TCID_COL, rnum);
                   RunMode = xls.getCellData(Constants.TESTCASES_SHEET, Constants.RUNMODE_COL, rnum);
                   if (RunMode.equals("Y")) {
                	   ArrayList<XmlClass> classfiles = new ArrayList<XmlClass>();
                	   XmlTest test = new XmlTest(suite);
                	   test.setName("Test" +(rnum-1));
                       classfiles.add(new XmlClass(packageName+"."+SuiteName+"." + tcid + ""));
                       test.setClasses(classfiles);
                   }  
          	   }
          	   System.out.println(suite.toXml());
          	   FileWriter writer = new FileWriter(
               new File(System.getProperty("user.dir") + "//src//test//resources//" + SuiteName + ".xml"));
          	   writer.write(suite.toXml());
          	   writer.flush();
          	   writer.close();
           } else {
        	   suite.setName(SuiteName);
        	   int rows = xls.getRowCount(Constants.TESTCASES_SHEET);
        	   for (int rnum = 2; rnum <= rows; rnum++) {
        		   String RunMode = "true";
                   String tcid = xls.getCellData(Constants.TESTCASES_SHEET, Constants.TCID_COL, rnum);
                   RunMode = xls.getCellData(Constants.TESTCASES_SHEET, Constants.RUNMODE_COL, rnum);
                   if (RunMode.equals("Y")) {
                	    XmlTest test = new XmlTest(suite);
                	    test.setName("Test" +(rnum-1));
                	   	ArrayList<XmlClass> classfiles = new ArrayList<XmlClass>();
                        classfiles.add(new XmlClass(packageName+"."+SuiteName+"." + tcid + ""));
                        test.setClasses(classfiles);
                   }
        	   }
        	   System.out.println(suite.toXml());
        	   FileWriter writer = new FileWriter(
               new File(System.getProperty("user.dir") + "//src//test//resources//" + SuiteName + ".xml"));
        	   writer.write(suite.toXml());
        	   writer.flush();
        	   writer.close();
           }
    	}catch(Exception exception){
    		exception.printStackTrace();
    		app.getGenericKeywords().logInfo("FATAL", "Not able to create SuiteFileGeneration");
    	}
    }

    public static void main(String[] args) throws IOException {
           Xls_Reader xls = new Xls_Reader(Constants.MASTERSUITE_XLS);
           masterSuitGeneration(xls);
           creatTestNgSuiteXml();
    }
}