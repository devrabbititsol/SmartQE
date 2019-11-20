package com.fileupload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.json.JSONObject;
import org.testng.annotations.AfterSuite;
import com.configurations.*;
import com.restassured.services.ReportPaths;
import com.utilities.BaseClass;
import com.utilities.PrimaryInfo;

@SuppressWarnings("unused")
public class DesktopAutomationReportUpload extends BaseClass {
	String primaryInfo = "";
	private String projectPath = System.getProperty("user.dir");
	private String reportsPath = projectPath + File.separator + "DesktopReports" + File.separator + "DesktopAutomationReport.html";	
	private String resultCount;
	private String datasetResult;
	private String reportstatus;


	/*
	 * Uploading file to server
	 * @moduleId 
	 * @testCaseId
	 * @userID
	 * @testsetId
	 * @moduleDescription
	 */
	
	@AfterSuite
	public void uploadFile() throws Exception {
//		Class<?> c = Class.forName(primaryInfo);
//		String primaryInfoObject = (String) c.getField("primaryInfo").get(c.getSuperclass().getName());
		new PrimaryInfo().primaryInfoData();
		System.out.println("primary info : " + Constants.PRIMARY_INFO);
		try {
			JSONObject primaryInfoObj = new JSONObject(Constants.PRIMARY_INFO);
			boolean is_web = primaryInfoObj.optBoolean("is_web");
			String mobile = primaryInfoObj.optString("mobile_platform");
			String moduleDescription = primaryInfoObj.optString("module_description");

			
			//resultCount = (Constants.TOTAL_TC - Constants.TOTAL_TC_FAILED) + " PASS / " + Constants.TOTAL_TC_FAILED + " FAIL";
			resultCount  = getTestcasesFailCount(reportsPath);
			datasetResult =  "";
			reportstatus = "";		

			//System.err.println(Constants.testName);
			String client_timezoneId = primaryInfoObj.optString("client_timezone_id");
			String report_upload_url = primaryInfoObj.optString("report_upload_url");
			String testcaseId = primaryInfoObj.optString("testcase_id");
			String datasetId = primaryInfoObj.optString("testcase_id");
			String moduleId = primaryInfoObj.optString("module_id");
			String subModuleId = primaryInfoObj.isNull("sub_module_id") ? null : primaryInfoObj.optString("sub_module_id");
			String testsetId = primaryInfoObj.optString("testset_id");
			String userId = primaryInfoObj.optString("user_id");
			String testsetName = primaryInfoObj.optString("testset_name");
		    String executedUserId = primaryInfoObj.optString("executed_user_id");
			System.out.println("testcaseid" + testcaseId + ",\nmodule_id" + moduleId + ",\ntestset_id" + testsetId
					+ ",\nuser_id" + userId + ",\nresult" + resultCount + ",\nreportStatus" + testsetName);
			new FileUploaderClient().uploadFile(report_upload_url, reportsPath, userId,executedUserId, testcaseId, testsetId, moduleId, subModuleId, is_web, resultCount, testsetName, mobile, client_timezoneId,datasetResult,true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getTestcasesFailCount(String filePath){
		int passcount = 0;
		int failcount = 0;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
		
			
			while (line != null) {
				if(line.contains("Device Information")) {
					break;
				}
				if(line.contains("test-status label right outline capitalize pass")) {
					//System.out.println(line);
					passcount = passcount +1;
				}
				if(line.contains("test-status label right outline capitalize fail")) {
					//System.out.println(line);
					failcount = failcount +1;
				}
				
				
				// read next line
				line = reader.readLine();
			}
			//System.out.println(passcount + "fail " +failcount);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return passcount +  " PASS / " + failcount + " FAIL";
		
	}
	
	private String getDatasetsResult(String filePath) {
		int passcount = 0;
		int failcount = 0;
		BufferedReader reader;
		String result = "";
		try {
			//filePath = "/Users/admin/Desktop/FWCode/SmartQE/MobileReports/2019-02-18-03-27-20-418_Report.html";
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				if(line.contains("Device Information")) {
					break;
				}
				String exactLine = "<span class='test-name'>";
				if(line.contains(exactLine) && (filePath.contains("MobileReports") || filePath.contains("APIReports"))) {
					result = result +  ((result.endsWith("Fail") || result.endsWith("Pass")) ? " $ ": "");
					result = result + line.replace(exactLine,"").replace("</span>", "").replace("\t", "");
				} else if(line.contains(exactLine) && filePath.contains("WebReports")) {{
					result = result +  ((result.endsWith("Fail") || result.endsWith("Pass")) ? " $ ": "");
					result = result +  line.split("class=\"left\">")[1].replaceAll("</span>", "");
				}
				
				}
			
				if(line.contains("test-status label right outline capitalize pass")) {
					passcount = passcount + 1;
					result = result + " - Pass";
					//System.err.println(line);
				}
				
				if(line.contains("test-status label right outline capitalize fail")) {
					result = result + " - Fail";
				}
				// read next line
				line = reader.readLine();
			}
			//System.out.println(passcount + "fail " +failcount);
			reader.close();
			System.err.println("datasetResult" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

}
