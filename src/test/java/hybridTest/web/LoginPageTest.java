package hybridTest.web;

import java.io.IOException;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import hybrid.utilities.BasePage;
import hybrid.utilities.JavaUtils;
import hybrid.web.pages.LoginPage;
import io.appium.java_client.android.AndroidDriver;

public class LoginPageTest {

	JavaUtils javaUtils = new JavaUtils();
	public String APINAME = "login", testType = "no-check", workbook = "WebTestdata", sheetName = "Login";

	public AndroidDriver mdriver;
	LoginPage loginPage;
	private WebDriver wdriver;
	BasePage bPage = new BasePage(wdriver);

	@BeforeSuite
	public void readConfig() {
		javaUtils.readConfigProperties();
	}

	

	@Test(dataProvider = "getTestData")
	public void loginTest(HashMap<String, String> usrData, ITestContext context) throws InterruptedException {
		String testOn = usrData.get("TESTON");
		if (testOn.toUpperCase().equals("MOBILE")) {
			System.out.println("LAUNCHING THE MOBILE APP FOR FLOW : " + usrData.get("TCID"));
			if (mdriver == null) {
				try {
					mdriver = BasePage.launchApp(usrData.get("DEVICE"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (testOn.toUpperCase().equals("WEB")) {
			if (wdriver == null) {
				System.out.println("Launching the web browser for the test case : " + usrData.get("TCID"));
				wdriver = BasePage.launchBrowser();
			} else if ((wdriver != null)) {
				System.out.println("Using the web browser for the test case : " + usrData.get("TCID"));
			}

			context.setAttribute("Test_id", usrData.get("TCID"));
			loginPage = new LoginPage(wdriver);
			loginPage.login(usrData);

		}
	}

	@DataProvider
	public Object[][] getTestData() {
		Object[][] allValues = javaUtils.returnAllUniqueValuesInMap(workbook, sheetName, testType);

		return allValues;

	}

}
