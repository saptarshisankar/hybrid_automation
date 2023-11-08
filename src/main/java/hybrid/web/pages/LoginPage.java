package hybrid.web.pages;

import java.time.Duration;
import java.util.HashMap;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import hybrid.utilities.BasePage;
import hybrid.utilities.Log;

public class LoginPage extends BasePage {
	@FindBy(xpath = "//input[@name='username']")
	WebElement userNamefield;

	@FindBy(xpath = "//input[@name='password']")
	WebElement passwordfield;

	@FindBy(xpath = "//button[@type='submit']")
	WebElement logInButton;

	@FindBy(xpath = "//p[@class='oxd-text oxd-text--p oxd-alert-content-text' and text()='Invalid credentials']")
	WebElement incorrectCredMsg;

	WebDriverWait wait = new WebDriverWait(wdriver, Duration.ofSeconds(5));

	public LoginPage(WebDriver wdriver) {
		super(wdriver);
		PageFactory.initElements(wdriver, this);
	}

	public void login(HashMap<String, String> usrData) throws InterruptedException {
		waitUntilBlockUI();
		try {
			sendKeys(userNamefield, usrData.get("Username"));
			sendKeys(passwordfield, usrData.get("Password"));
			waitUntilElementAppears(logInButton);
			waitUntilElementClickable(logInButton);
			waitAndScroll(logInButton);
			System.out.println(" Scenario 1, Testcase 1: Clicking Login Button Success!!!! ");
			waitUntilBlockUI();
			SoftAssert softAssert = new SoftAssert();
			

			try {
				if (incorrectCredMsg.isDisplayed()) {
					System.out.println("Please check the given username and password!");
					Assert.fail("Please check the given username and password!");
					softAssert.fail("Login failed.");
				}

			} catch (Exception e) {
				System.out.println("User name and Password are correct!");
				System.out.println("----------------------------");
				softAssert.assertTrue(true, "User Logged in Successfully");
				
			}

			Log.info("User Logged in Successfully");
			System.out.println("User Logged in Successfully");
			softAssert.assertAll();
			
		} catch (NoSuchElementException e) {
			Log.info("User already logged in", true);
		}
	}

}
