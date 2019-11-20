package desktop.testclasses;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.File;
import com.configurations.ExtentConfigurations;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.utilities.BaseClass;

import desktop.pages.HomePage;

public class DesktopTest extends BaseClass {
	ExtentReports reports;
	ExtentTest test;
	ITestResult result;
	private Logger logger;
	private HomePage homePage;
	private String DesktopApplicationURL = "http://175.101.137.192:9999";

	public DesktopTest() throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		logger = Logger.getLogger(DesktopTest.class);
		reports = ExtentConfigurations.getExtentInstance(projectPath + File.separator + "DesktopReports" + File.separator + "DesktopAutomationReport.html", projectPath, "Zoom");
		//reports = new ExtentReports(projectPath + File.separator + "DesktopReports" + File.separator + "DesktopAutomationReport.html", false);
		test = reports.startTest("DesktopTest");
		homePage = new HomePage(winiumDriver);
	}

	@BeforeClass
	public void setup() throws Exception {
		try {
			printInfoLogAndReport(test, logger, "URL : " + DesktopApplicationURL);
			winiumDriver = launchDesktopApp("C:\\Users\\Administrator\\AppData\\Roaming\\Zoom\\bin\\Zoom.exe", DesktopApplicationURL);
			printInfoLogAndReport(test, logger, "Application is Launched");
		} catch (Exception ex) {
			printFailureLogAndReportDesktop(test, logger, "Exception is : " + ex.getMessage());
		}
	}

	@Test
	public void zoomMeeting() throws Exception {
		try {
			// HomePage homePage = PageFactory.initElements(winiumDriver, HomePage.class);
			homePage.clickJoinMeetingButton(winiumDriver);
			printInfoLogAndReport(test, logger, "Clicked on Join a meeting Button");
			homePage.fillMeetingIdField(winiumDriver, "12345678902");
			printInfoLogAndReport(test, logger, "Filled Meeting Id Field : 12345678902");
			homePage.clickJoinButton(winiumDriver);
			printInfoLogAndReport(test, logger, "Clicked on Join Button");
			String alertMessage = homePage.getAlertWorngMeeting(winiumDriver);
			if (alertMessage.contains("This meeting ID is not valid. Please check and try again.")) {
				printSuccessLogAndReport(test, logger, "When meeteing ID is entered wrong, Alert Message is : " + alertMessage);
			}
			homePage.clickLeaveButton(winiumDriver);
			printInfoLogAndReport(test, logger, "Clicked on Leave Button");
		} catch (Exception ex) {
			printFailureLogAndReportDesktop(test, logger, "Exception is : " + ex.getMessage());
		}
	}
	@AfterClass
	public void tearDown() {
		//printSuccessLogAndReport(test, logger, "Test Case is Passed");
		reports.endTest(test);
		reports.flush();
		winiumDriver.quit();
	}
}
