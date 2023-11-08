package hybridTest.web;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import hybrid.utilities.BasePage;
import hybrid.utilities.JavaUtils;
import hybrid.utilities.WebDriverSingleton;
import hybrid.web.pages.MyInfoPage;

public class MyInfoPageTest {

	JavaUtils javaUtils = new JavaUtils();

	MyInfoPage myInfoPage;

	WebDriver wdriver = WebDriverSingleton.getWebDriverInstance();
	BasePage bPage = new BasePage(wdriver);

	@Test
	public void myInfoTest(ITestContext context) throws InterruptedException {

		context.setAttribute("Test_id", "MyInfoPageTest_001");

		myInfoPage = new MyInfoPage(wdriver);
		myInfoPage.myInfoPage();

	}
}
