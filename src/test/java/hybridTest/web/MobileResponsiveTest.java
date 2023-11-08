package hybridTest.web;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MobileResponsiveTest {
	public static WebDriver driver;

	@Test(dataProvider = "screenSizeTestData")

	public void fillProfileForm(int screenWidth, int screenHeight) throws Exception {

		WebDriverManager.chromedriver().setup();

		driver = new ChromeDriver();

		driver.get("https://derivfe.github.io/qa-test/settings");

		Dimension dimension = new Dimension(screenWidth, screenHeight);

		driver.manage().window().setSize(dimension);

		System.out.println("Screen size set");

		WebElement profileTab = driver.findElement(By.linkText("Profile"));
		profileTab.click();

		WebElement salutation = driver.findElement(By.id("salutation"));

		Select salutation_dd = new Select(salutation);

		salutation_dd.selectByValue("mrs");

		WebElement firstName = driver.findElement(By.id("fname"));

		firstName.sendKeys("TestUser1");

		WebElement dob = driver.findElement(By.id("dob"));

		dob.sendKeys("01/01/1995");

		String salutation_value = salutation.getAttribute("value");

		Assert.assertEquals("mrs", salutation_value);

		String firstNameValue = firstName.getText();

		Assert.assertNotEquals("TestUser3", firstNameValue);

		WebElement updateProfile = driver.findElement(By.id("btn-submit-profile"));

		updateProfile.click();

		System.out.println("profile updated");

		driver.quit();

	}

	@DataProvider(name = "screenSizeTestData")

	public Object[][] screenSizeData() {

		Object[][] data = new Object[2][2];

		data[0][0] = 1440;

		data[0][1] = 3120;

		data[1][0] = 720;

		data[1][1] = 1600;

		return data;

	}
}
