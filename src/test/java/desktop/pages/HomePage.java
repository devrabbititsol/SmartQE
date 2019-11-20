package desktop.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.winium.WiniumDriver;

import com.utilities.BaseClass;

public class HomePage extends BaseClass{

	public HomePage(WiniumDriver winiumDriver) {
		this.winiumDriver = winiumDriver;
	}
	private static final By JOINMEETING_BUTTON = By.name("Join a Meeting");
	private static final By MEETINGID_FIELD = By.name("Please enter your Meeting ID or Personal Link Name");
	private static final By JOIN_BUTTON = By.name("Join");
	private static final By LEAVE_BUTTON = By.name("Leave");
	private static final By WRONG_MEETING_ID_ALERT = By.name("This meeting ID is not valid. Please check and try again."); 
	
	
	public void clickJoinMeetingButton(WiniumDriver winiumDriver) throws Exception {
		Thread.sleep(2000);
		winiumDriver.findElement(JOINMEETING_BUTTON).click();
	}
	
	public void fillMeetingIdField(WiniumDriver winiumDriver, String meetingId) throws Exception {
		Thread.sleep(2000);
		winiumDriver.findElement(MEETINGID_FIELD).sendKeys(meetingId);
	}
	
	public void clickJoinButton(WiniumDriver winiumDriver) throws Exception {
		Thread.sleep(2000);
		winiumDriver.findElement(JOIN_BUTTON).click();
	}
	
	public void clickLeaveButton(WiniumDriver winiumDriver) throws Exception {
		Thread.sleep(2000);
		winiumDriver.findElement(LEAVE_BUTTON).click();
	}
	

	public String getAlertWorngMeeting(WiniumDriver winiumDriver) throws Exception {
		Thread.sleep(2000);
		return winiumDriver.findElement(WRONG_MEETING_ID_ALERT).getAttribute("Name");
	}
}
