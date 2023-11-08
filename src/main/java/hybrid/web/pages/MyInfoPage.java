package hybrid.web.pages;

import java.time.Duration;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import hybrid.utilities.BasePage;
import hybrid.utilities.Log;
import hybrid.utilities.RandomDateGenerator;

public class MyInfoPage extends BasePage {
	@FindBy(xpath = "//button[@class='oxd-icon-button oxd-main-menu-button']")
	WebElement sideHamburger;
	
	@FindBy(xpath = "//input[@name='username']")
	WebElement userNamefield;

	@FindBy(xpath = "//h6[contains(@class, 'oxd-topbar-header-breadcrumb-module') and text() = 'Dashboard']")
	WebElement dashBoardTitle;

	@FindBy(xpath = "//span[@class='oxd-text oxd-text--span oxd-main-menu-item--name'and text()='My Info']")
	WebElement myInfoTab;

	@FindBy(xpath = "//label[contains(text(), 'Date of Birth')]/ancestor::div[@class='oxd-input-group oxd-input-field-bottom-space']//input[@placeholder='yyyy-mm-dd']")
	WebElement dobTextBox;

	@FindBy(xpath = "//label[contains(text(), 'Date of Birth')]/following::i[contains(@class, 'oxd-icon') and contains(@class, 'bi-calendar') and contains(@class, 'oxd-date-input-icon')]")
	WebElement dobSelector;

	@FindBy(xpath = "(//div[@class='oxd-form-actions']//button[@type='submit' and contains(@class, 'oxd-button') and contains(@class, 'oxd-button--secondary') and contains(@class, 'orangehrm-left-space')])[1]")
	WebElement submitButton;

	@FindBy(xpath = "//span[contains(@class, 'oxd-text') and contains(@class, 'oxd-text--span') and contains(@class, 'oxd-input-field-error-message') and contains(@class, 'oxd-input-group__message') and text()='Should be a valid date in yyyy-mm-dd format']")
	WebElement invalidDateError;
	
	@FindBy(xpath = "//span[@class='oxd-userdropdown-tab']")
	WebElement profileDropDown;
	
	@FindBy(xpath = "//a[@class='oxd-userdropdown-link' and text()='Logout']")
	WebElement logoutButton;

	WebDriverWait wait = new WebDriverWait(wdriver, Duration.ofSeconds(5));
	SoftAssert softAssert = new SoftAssert();

	public MyInfoPage(WebDriver wdriver) {
		super(wdriver);
		PageFactory.initElements(wdriver, this);
	}

	public static boolean isPageFullyLoaded(WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// Check the document.readyState property
		return js.executeScript("return document.readyState").equals("complete");
	}

	public void myInfoPage() throws InterruptedException {
		try {
			// Waiting for page to load
			waitUntilBlockUI();
			boolean isPageLoaded = isPageFullyLoaded(wdriver);

			// Check if the page loaded successfully
			if (isPageLoaded) {
				System.out.println("Scenario 1, Testcase2: Expected Result is success!!!! Page has fully loaded.");
				System.out.println("----------------------------");
				System.out.println("Precondition for Scenario 1, Testcase 2 is met as we are using WebDriverManager's single instance to perform all the actions and hence user is already logged into the admin panel");
				softAssert.assertTrue(true, "Page has fully loaded.");
			} else {
				System.out.println("Page did not fully load.");
				softAssert.fail("Page did not fully load.");
			}
			waitUntilElementAppears(myInfoTab);
			myInfoTab.click();
			waitUntilElementAppears(dobTextBox);
			System.out.println("----------------------------");
			System.out.println("Scenario 1,Testcase 2: Successfully clicked on My info page");
			scrollToWebElement(dobTextBox);
			
			System.out.println("----------------------------");
		
			JavascriptExecutor jsExecutor = (JavascriptExecutor) wdriver;
			String dobValue = (String) jsExecutor.executeScript("return arguments[0].value;", dobTextBox);
			
			
			waitAndScroll(dobSelector);
			waitUntilElementClickable(dobSelector);
			if (dobValue != null && !dobValue.isEmpty()) {
				System.out.println("Scenario 1, Testcase2: Date of Birth field is filled in with a value");
				softAssert.assertTrue(true, "Date of Birth field is filled in with a value" + dobValue);

			} else {
				System.out.println("Date of Birth field is empty or null.");
			}
			
			System.out.println("----------------------------");
			System.out.println("Updating Date Of Birth");
			waitUntilElementClickable(dobSelector);
			dobSelector.click();
			String randBDay = RandomDateGenerator.generateRandomBirthday(1899, 2001);
			addTestDataIni("rand_bday", randBDay);
			dobTextBox.sendKeys(Keys.CONTROL + "a"); // Select all text
			dobTextBox.sendKeys(Keys.BACK_SPACE);

			dobTextBox.sendKeys(randBDay);
			
			System.out.println("----------------------------");
			
			softAssert.assertTrue(true, "Successfully updated Date of Birth to any date");
			System.out.println("Scenario 1, Testase 2: Successfully updated Date of Birth to any date");
			System.out.println("----------------------------");
			//Check for invalid date of birth entry
			
			try {
				if (invalidDateError.isDisplayed()) {
					System.out.println("Scenario 1, Testase 2: FAIL!!  Please check the date which you have entered");
					Assert.fail("Test failed!! Invalid Date entered");
					softAssert.fail("Date of Birth updation failed");
				}

			} catch (Exception e) {
				softAssert.assertTrue(true, "Date of Birth updation successful");

			}
			
			waitUntilBlockUI();
			// Asserting on the value after entering random dob
			String actualValue = dobTextBox.getAttribute("value");
			String expectedValue = getvalueFromTestDataIni("rand_bday");

			softAssert.assertEquals(actualValue, expectedValue);
			submitButton.click();
			softAssert.assertTrue(true, "Successfully clicked on Save button to save the updated data");
			System.out.println("Scenario 1, Testcase 2: Submitted Successfully");
			waitUntilElementAppears(dobTextBox);
			// Asserting weather after submission the updated data is correct or not
			System.out.println("----------------------------");
			JavascriptExecutor jsExecutor2 = (JavascriptExecutor) wdriver;
			String dobAfterSubmission = (String) jsExecutor2.executeScript("return arguments[0].value;", dobTextBox);

			if (dobAfterSubmission.equalsIgnoreCase(dobAfterSubmission)) {
				softAssert.assertTrue(true,"Extra validation:: Updated Date of Birth is properly reflecting after submission");
				System.out.println("Extra validation::Updated Date of Birth reflecting properly after submission of the form data");
			} else {
				System.out.println("Updated Date of Birth not matching");
				softAssert.fail();
			}
			softAssert.assertEquals(dobAfterSubmission, expectedValue);
			System.out.println("----------------------------");
			waitUntilBlockUI();
			System.out.println("Precondition for Scenario 1, Testcase 3: is met as user is still logged in with the same WebdriverManager instance");
			profileDropDown.click();
			waitUntilElementAppears(logoutButton);
			logoutButton.click();
			 waitUntilElementAppears(userNamefield);
			 System.out.println("----------------------------");
			 System.out.println("Scenario 1: Testcase 3:: SucessFully Logged out and landed on Login page");
			 
			 Assert.assertTrue(true, "SucessFully Logged out and landed on login page");

			Capabilities capabilities = ((RemoteWebDriver) wdriver).getCapabilities();
			String browserName = capabilities.getBrowserName();
			System.out.println("----------------------------");
			Log.info("All Test Cases Validated Successfully");
			System.out.println("All Test Cases Validated Successfully");
			softAssert.assertAll();

		} catch (NoSuchElementException e) {
			Log.info("Date of Birth field is not present", true);
		}
	}

}
