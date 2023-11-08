package hybridTest.mobile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.ini4j.InvalidFileFormatException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import hybrid.mobile.CalculatorPage;
import hybrid.utilities.JavaUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

public class CalculatorAppTest {

	JavaUtils javaUtils = new JavaUtils();
	AppiumDriver mdriver = null;

	@BeforeSuite
	public void setUp() throws IOException, InterruptedException {
		javaUtils.readConfigProperties();
		javaUtils.readDeviceId();

		DesiredCapabilities capabilities = new DesiredCapabilities();

		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("noReset", true);
		capabilities.setCapability("autoGrantPermissions", true);
		capabilities.setCapability("clearSystemFiles", true);
		capabilities.setCapability("automationName", "UiAutomator2");
		String apkPath = null;
		try {
			File file = new File(JavaUtils.configProperties.get("apkPath"));
			apkPath = file.getAbsolutePath();
		} catch (Exception e) {
			System.out.println("APK path not valid");
			e.printStackTrace();
		}

		capabilities.setCapability("app", apkPath);
		mdriver = new AndroidDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);

	}

	@Test
	public void testCalculatorOperations() throws InvalidFileFormatException, IOException, InterruptedException {

		CalculatorPage cPage = new CalculatorPage(mdriver);
		// Perform Addition
		cPage.performCalculation("5", "+", "3", "8", mdriver);
		System.out.println("Successfully verified that Addition Operation is working fine!");
		System.out.println("------------------------------");
		// Perform Subtraction
		cPage.performCalculation("8", "-", "4", "4" , mdriver);
		System.out.println("Successfully verified that Subtraction Operation is working fine!");
		System.out.println("------------------------------");
		// Perform Multiplication
		cPage.performCalculation("4", "x", "2", "8" , mdriver);
		System.out.println("Successfully verified that Multiplication Operation is working fine!");
		System.out.println("------------------------------");
		// Perform Division
		cPage.performCalculation("8", "/", "4", "2" , mdriver);
		System.out.println("Successfully verified that Division Operation is working fine!");
		System.out.println("------------------------------");
	}

}
