package com.utilities;

import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.restassured.services.ReportPaths;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class BaseClass {
	public WebDriver driver;
	public WiniumDriver winiumDriver;
	public static String projectPath = System.getProperty("user.dir");
	public static String reportsPath = projectPath + File.separator + "WebReports" + File.separator + ReportPaths.reportPathName;
	public static String desktopReportsPath = projectPath + File.separator + "DesktopReports" + File.separator + ReportPaths.reportPathName;
	public static String mobileReportsPath = projectPath + File.separator + "MobileReports" + File.separator + ReportPaths.reportPathName;
	private static boolean isElementDispalyed;
	public String chromeDriverPath = projectPath + File.separator + "Resources" + File.separator + "chromedriver.exe";
	public String geckoFireFoxDriverPath = projectPath + File.separator + "Resources" + File.separator + "geckodriver.exe";
	public String iEDriverPath = projectPath + File.separator + File.separator + "Resources" + File.separator + "IEDriverServer.exe";
	public AppiumDriver<MobileElement> appiumDriver;
	public String text = "";
	
	// Explicit wait method
	public static WebElement waitForExpectedElement(WebDriver driver, final By locator, int time) {
		WebDriverWait wait = new WebDriverWait(driver, time);
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public static WebElement waitForExpectedElement(WebDriver driver, WebElement element) {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("");
		}
		//isElementDispalyed = element.isDisplayed();
		initialInputDataClear(element); // if any text in input it will clear
		WebDriverWait wait = new WebDriverWait(driver, 20);
		return wait.until(ExpectedConditions.visibilityOf(element));
	
	}

	public static String initialInputDataClear(WebElement webElement) {
		String str = webElement.toString();
		try {
			if (str != null && str.contains("INPUT")) {
			String[] listString = null;
			if (str.contains("xpath")) {
				listString = str.split("xpath:");
			} else if (str.contains("id")) {
				listString = str.split("id:");
			}
			String last = listString[1].trim();
			String xpath = last.substring(0, last.length() - 1);
			if (xpath != null && xpath.contains("INPUT")) {
				webElement.clear();
			}
			}
		} catch (Exception e) {
			System.out.println("Not editable input");
		}
		return str;
	}

	// Explicit wait
	public static WebElement waitForExpectedElement(WebDriver driver, final By locator) {
		WebDriverWait wait = new WebDriverWait(driver, 120);
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	// Explicit wait method
	public boolean objectExists(WebDriver driver, final By locator) {
		try {
			waitForPageToLoad();
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Method for Retry and It executes the failed test case based on our count
	public class Retry implements IRetryAnalyzer {
		private int count = 0;
		private static final int maxTry = 3;

		public boolean retry(ITestResult iTestResult) {
			if (!iTestResult.isSuccess()) { // Check if test not succeed
				if (count < maxTry) { // Check if maxtry count is reached
					count++; // Increase the maxTry count by 1
					iTestResult.setStatus(ITestResult.FAILURE); // Mark test as failed
					return true; // Tells TestNG to re-run the test
				} else {
					iTestResult.setStatus(ITestResult.FAILURE); // If maxCount reached,test marked as failed
				}
			} else {
				iTestResult.setStatus(ITestResult.SUCCESS); // If test passes, TestNG marks it as passed
			}
			return false;
		}
	}

	// Explicit wait method (While Script Execution we need to pass time limit)
	public boolean objectExists(WebDriver driver, final By locator, int timeout) {
		try {
			waitForPageToLoad();
			WebDriverWait wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Explicit wait method (While Script Execution we need to pass time limit
	public void waitForPageToLoad() {
		(new WebDriverWait(driver, 60)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return (((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.readyState")
						.equals("complete"));
			}
		});
	}
	
	/**
	 * Driver instance for Desktop Applications
	 * 
	 * @param applicationexePath
	 * @param remoteWiniumDriverPath
	 * @return
	 * @throws Exception
	 */
	public WiniumDriver launchDesktopApp(String applicationexePath, String remoteWiniumDriverPath) throws Exception {
		DesktopOptions options = new DesktopOptions(); 
		options.setApplicationPath(applicationexePath); 
		winiumDriver = new WiniumDriver(new URL(remoteWiniumDriverPath), options);
		return winiumDriver;
	}

	@SuppressWarnings("deprecation")
	public WebDriver launchBrowser(String browserName, ConfigFilesUtility configFileObj) {
		if (!isWindows()) {
			if (isSolaris() || isUnix()) {
				chromeDriverPath = chromeDriverPath.replace(".exe", "");
				geckoFireFoxDriverPath = geckoFireFoxDriverPath.replace(".exe", "");
			} else if (isMac()) {
				chromeDriverPath = chromeDriverPath.replace("chromedriver.exe", "macChromeDriver");
				geckoFireFoxDriverPath = geckoFireFoxDriverPath.replace("geckodriver.exe", "macGeckodriver");
			}
		}

		if (browserName.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			
			ChromeOptions options = new ChromeOptions();
			Map<String, Object> prefs=new HashMap<String,Object>();
			prefs.put("profile.default_content_setting_values.notifications", 1);
			//1-Allow, 2-Block, 0-default
			options.setExperimentalOption("prefs",prefs);
			
			if (isSolaris() || isUnix()) {
				
				options.addArguments("start-maximized"); // open Browser in maximized mode
				options.addArguments("disable-infobars"); // disabling infobars
				options.addArguments("--disable-extensions"); // disabling extensions
				options.addArguments("--disable-gpu"); // applicable to windows os only
				options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
				options.addArguments("--no-sandbox"); // Bypass OS security model
				options.addArguments("--headless"); // this line makes run in linux environment with jenkins
				driver = new ChromeDriver(options);
			} else {
				driver = new ChromeDriver(options);
			}

			System.out.println("Chrome Browser is Launched");
		} else if (browserName.equalsIgnoreCase("mozilla")) {
			System.setProperty("webdriver.gecko.driver", geckoFireFoxDriverPath);
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
//			FirefoxProfile profile = new FirefoxProfile();
//			//1-Allow, 2-Block, 0-default
//			profile.setPreference("permissions.default.desktop-notification", 1);
//			capabilities.setCapability(FirefoxDriver.PROFILE, profile);
			
			if (isSolaris() || isUnix()) {
				FirefoxBinary binary = new FirefoxBinary();
				
				capabilities.setCapability("marionette", true);
				FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
				firefoxOptions.setBinary(binary);
			
				firefoxOptions.addArguments("--no-sandbox"); // Bypass OS security model
				firefoxOptions.addArguments("--headless"); 
				driver = new FirefoxDriver(firefoxOptions);
			} else {
				FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
				driver = new FirefoxDriver(firefoxOptions);
			//	driver.manage().window().setPosition(new Point(-2000, 0));
			}
			
			System.out.println("FireFox Browser is Launched");
		} else if (browserName.equalsIgnoreCase("safari")) {
			// Note : Should AllowRemoteAutomation in safari browser DeveloperMenu
			// Directions -- > launchSafariBrowser --> Preferences --> Advanced Tab -->
			// Show Developer Menu --> Click on DevloperMenu --> Enable
			// AllowRemoteAutomation
			// System.setProperty("webdriver.safari.noinstall", "true");
			driver = new SafariDriver();
			//driver.get("http://www.google.com");
			System.out.println("Safari Browser is Launched");
		} else if (browserName.equalsIgnoreCase("ie")) {
			// To run Internet explorer you should enable below configuration in IE
			// Internet Explorer -> Settings -> Security tab -> Enable Protected mode in all zones 
			
			if (!isWindows()) {
				System.out.println("IE Browser not supported for this OS.");
				return null;
			}
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			System.setProperty("webdriver.ie.driver", iEDriverPath);
			driver = new InternetExplorerDriver(capabilities);
			System.out.println("IE Browser is Launched");
		}
		
		driver.get(configFileObj.getProperty("URL"));
		if (isSolaris() || isUnix()) {
			Dimension d = new Dimension(1382, 744);
			// Resize the current window to the given dimension
			driver.manage().window().setSize(d);
		} else {
			java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;

			Dimension d = new Dimension(screenWidth, screenHeight);
			// Resize the current window to the given dimension
			driver.manage().window().setSize(d);
			//driver.manage().window().setPosition(new Point(-2000, 0));
		}
		return driver;
	}

	private String OS = System.getProperty("os.name").toLowerCase();

	public boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}

	//===================== For Report ========================
	public void testLogHeader(ExtentTest test, String data) {
		if (test != null)
			test.log(LogStatus.INFO, "<b style = 'background-color: #ffffff; color : #ff8f00 ; font-size : 18px' >" + data + "</b>");
	}
	
	public String setTestcaseName(String browserName, String tescaseName) {
		
		String chromeURL 	= "https://smartqe.io/pdownload/chrome.jpg";
		String mozillaURL 	= "https://smartqe.io/pdownload/mozilla.jpg";
		String ieURL 		= "https://smartqe.io/pdownload/ie.jpg";
		String safariURL 	= "https://smartqe.io/pdownload/safari.jpg";
		String finalURL = "";
		if(browserName.equalsIgnoreCase("chrome")) {
			finalURL = chromeURL;
		} else if(browserName.equalsIgnoreCase("mozilla")) {
			finalURL = mozillaURL;
		} else if(browserName.equalsIgnoreCase("ie")) {
			finalURL = ieURL;
		} else if(browserName.equalsIgnoreCase("safari")) {
			finalURL = safariURL;
		}
		return "<img style=\"-webkit-user-select: none;margin: auto; height:20px;width:20px;margin-right:10px;\" src=\"" + finalURL + "\" class=\"left\">" +  tescaseName +" </span>";
	}

	public void printSuccessLogAndReport(ExtentTest test, Logger logger, String data) {
		if (test != null) {
			if(data.contains("Password") || data.contains("password")) {
				String[] passwordData = data.split(":");
				data = passwordData[0] + "****";
			}
			test.log(LogStatus.PASS, data);
		}
		if (logger != null)
			logger.info(data);
	}

	public void printFailureLogAndReport(ExtentTest test, Logger logger, String data) {
		if (test != null)
			test.log(LogStatus.FAIL, data);
		if (logger != null)
			logger.error(data);
		String name = "";
		if (data.toString().length() <= 20) {
			name = data.toString();
		} else {
			name = data.toString().substring(0, 10);
		}
		test.log(LogStatus.INFO, "Screenshot Taken : " + Utilities.captureScreenshot(driver, name));
	}

		/***
	 * Fail status for Desktop Application Reoport
	 * 
	 * @param test
	 * @param logger
	 * @param data
	 */
	
	public void printFailureLogAndReportDesktop(ExtentTest test, Logger logger, String data) {
		if (test != null)
			test.log(LogStatus.FAIL, data);
		if (logger != null)
			logger.error(data);
		String name = "";
		if (data.toString().length() <= 20) {
			name = data.toString();
		} else {
			name = data.toString().substring(0, 10);
		}
		test.log(LogStatus.INFO, "Screenshot Taken : " + Utilities.captureScreenshotDesktopApplication(winiumDriver, name));
	}
	

	public void printInfoLogAndReport(ExtentTest test, Logger logger, String data) {
		logger.info(data);
		test.log(LogStatus.INFO, data);
	}

	//============= End Report ===============
	
	public void tearDown(ExtentReports reports, ExtentTest test) throws Exception {
		reports.endTest(test);
		reports.flush();
		driver.quit();
	}

	// mouseHover
	public void mouseHover(WebDriver webDriver, WebElement element) {

		Actions action = new Actions(webDriver);
		action.moveToElement(element).build().perform();
		// action.moveToElement(we).moveToElement(driver.findElement(By.xpath(elementClickXpath))).click().build().perform();
	}

	// window
	String parentHandle = "";

	public void windowHandle(WebDriver webDriver) {
		parentHandle = webDriver.getWindowHandle();
		Set<String> handles = webDriver.getWindowHandles();
		for (String windowHandles : handles) {
			System.out.println(windowHandles);
			webDriver.switchTo().window(windowHandles);
		}
	}

	public void switchToParentWindow1(WebDriver webDriver) {
		if (parentHandle != null && !parentHandle.isEmpty()) {
			webDriver.switchTo().window(parentHandle);
		}
	}

	// upload a file
	public void uploadFile(String name, String xpath) {
		try {
			
			WebElement element = waitForExpectedElement(driver,By.xpath(xpath));	
			element.sendKeys(name);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	// Dropdown
	public String dropDownHandle(String xpath, String optionName) {
		Select oSelect = new Select(driver.findElement(By.xpath(xpath)));
		oSelect.selectByValue(optionName);
		WebElement option = oSelect.getFirstSelectedOption();
		option.getText();
		return "";
	}
	
	//handle table validations
	public String tableDataHandle(ExtentTest test,String xpath) {
		List<WebElement> rows;
		if(xpath.startsWith("/")) {
			waitForExpectedElement(driver, By.xpath(xpath));
			xpath = xpath + "/tbody/tr";
			rows = driver.findElements(By.xpath(xpath));
		} else {
			waitForExpectedElement(driver, By.cssSelector(xpath));
			xpath = xpath + ">tbody>tr";
			rows = driver.findElements(By.cssSelector(xpath));
		}
		//List<WebElement> rows = element.findElements(By.xpath(trXpath));
		//Print data from each row
		test.log(LogStatus.INFO, "Number of rows : " + rows.size());
		for (WebElement row : rows) {
			  System.out.print(row + "\t");
		    List<WebElement> cols = row.findElements(By.tagName("td"));
		    for (WebElement col : cols) {
		        System.out.print(col.getText() + "\t");
		    }
		    System.out.println();
		}
		return "" + rows.size();

	}
	
	// Switching to iframe by position
	public void switchToIframe(String xpath) {
		try {
			Thread.sleep(1000);
			for (int framePosition = 0; framePosition <= 5; framePosition++) {
				try {
					driver.switchTo().frame(framePosition);
					WebElement ele;
					if(xpath.startsWith("/")) {
					 ele = driver.findElement(By.xpath(xpath));
					} else {
					 ele = driver.findElement(By.cssSelector(xpath));
					}
					if (ele.isDisplayed()) {
						//driver.switchTo().frame(framePosition);
						System.out.println(framePosition);
						break;
					}
				} catch (Exception e) {
					driver.switchTo().parentFrame();
				}
			}
		} catch (Exception e) {
			driver.switchTo().parentFrame();
		}
	}
	
	
	public void alertHandle(boolean isAlertAccept) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertMessage = driver.switchTo().alert().getText();
			System.out.println(alertMessage);
			Thread.sleep(5000);
			if(isAlertAccept) {
				alert.accept();
			} else {
				alert.dismiss();
			}
		} catch (Exception e) {

		}
	}
	
	
	public boolean isElementPresent(WebElement e) {
	    boolean flag = true;
	    try {
	        e.isDisplayed();
	        flag = true;
	        isElementDispalyed = true;
	    } catch (Exception exception) {
	        flag = false;
	        isElementDispalyed = false;
	        
	    }
	    return flag;
	}
	
	public boolean skipifElementisNotDisplayed() {
		return isElementDispalyed;
	}
	
	
}
