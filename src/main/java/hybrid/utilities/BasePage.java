package hybrid.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.paulhammant.ngwebdriver.NgWebDriver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public class BasePage extends JavaUtils {

	public static WebDriver wdriver;
	public static AndroidDriver mdriver;
	public static Map<String, String> dataMap;

	@FindBy(xpath = "//input[(@class = 'select2-search__field') and (@type ='search')]")
	public WebElement dropDownSearchField;

	public BasePage(WebDriver wdriver) {
		this.wdriver = wdriver;
	}

	public static WebDriver launchBrowser() {

		// local machine
		String browser = JavaUtils.configProperties.get("browser");

		if (browser.equalsIgnoreCase("Firefox")) {
			System.setProperty("webdriver.gecko.marionette", "./drivers/geckodriver.exe");
			wdriver = new FirefoxDriver();

		} else if (browser.equalsIgnoreCase("Chrome")) {
			if (JavaUtils.configProperties.get("OperatingSystem").equalsIgnoreCase("WINDOWS")) {

				wdriver = WebDriverSingleton.getWebDriverInstance();
//			    wdriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
			} else if (JavaUtils.configProperties.get("OperatingSystem").equalsIgnoreCase("MAC")) {
				System.setProperty("webdriver.chrome.driver", "/Applications/chromedriver");
			}
		}

		String url = JavaUtils.configProperties.get("baseUrl");

		System.err.println(url);
		wdriver.get(url);
		wdriver.manage().window().maximize();
		wdriver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
		NgWebDriver ngDriver = new NgWebDriver((JavascriptExecutor) wdriver);
		ngDriver.waitForAngularRequestsToFinish();
		return wdriver;
	}
	
	
	

	public void waitUntilBlockUI() {
		try {
			WebElement loadingSpinner = wdriver.findElement(
					By.xpath(".//div[@class='ng-busy-default-sign']//div[@class='ng-busy-default-spinner']"));

			WebElement pleaseWaitText = wdriver.findElement(
					By.xpath(".//div[@class='ng-busy-default-sign']//div[contains(text(),'Please wait...')]"));

			if (pleaseWaitText.isDisplayed() || loadingSpinner.isDisplayed()) {
				while (pleaseWaitText.isDisplayed() || loadingSpinner.isDisplayed()) {
					Log.info("waiting for the page to load...");
					Thread.sleep(2000);
				}
			}

		} catch (InterruptedException | StaleElementReferenceException | NoSuchElementException e) {
//	      System.out.println("Loading spinner not found");
		} catch (Exception e) {

		}
	}

	public void waitUntilElementAppears(WebElement element) {

		try {
			WebDriverWait wait = new WebDriverWait(wdriver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void waitAndScroll(WebElement ele) throws InterruptedException {
		try {
			waitUntilBlockUI();
			waitUntilElementAppears(ele);
//			waitUntilElementClickable(ele);
			Actions action = new Actions(wdriver);
			action.moveToElement(ele).click(ele).build().perform();
		} catch (NoSuchElementException e) {
			Assert.fail("The expected the element is not found!");
		} catch (Exception e) {
			System.out.println("JS click");
			click(ele);

		}
	}

	public void click(WebElement ele) {
		waitUntilBlockUI();
		JavascriptExecutor executor = (JavascriptExecutor) wdriver;
		executor.executeScript("arguments[0].click();", ele);
	}

	public void waitUntilElementClickable(WebElement element) {
		// two attempts to click
		int attempts = 3;
		boolean flag = true;
		do {
			try {
				WebDriverWait wait = new WebDriverWait(wdriver, Duration.ofSeconds(10));
				wait.until(ExpectedConditions.elementToBeClickable(element));
				flag = false;
			} catch (WebDriverException e) {
				--attempts;
				Reporter.log("Element not enabled to click, Trying again", true);
			}
		} while (attempts > 0 && flag);
	}

	public void scrollToWebElement(WebElement element) throws InterruptedException {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) wdriver;

		// Scroll the element into view
		jsExecutor.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);

	}
	
	
	
	

	public void sendKeys(WebElement element, String value) {
		try {
			waitUntilBlockUI();
			waitUntilElementAppears(element);
			waitAndScroll(element);
			element.clear();
			waitUntilBlockUI();
			element.sendKeys(value);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * Actions actions = new Actions(wdriver); actions.moveToElement(element);
		 * actions.click(); // actions.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
		 * actions.sendKeys(value); actions.build().perform();
		 */
	}

	public void captureScreenshotOnFailedTest(ITestResult result, String Tcid) {
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				Log.info("Taking screenshot on failed test");
				File source = ((TakesScreenshot) wdriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(source, new File("./Screenshots/" + Tcid + ".png"));
				Log.info("Screenshot taken");
			} catch (Exception e) {
				System.out.println("Exception while taking screenshot " + e.getMessage());
			}
		}
	}

	public void captureScreenshotOnPassedTest(ITestResult result, Object object) {
		if (ITestResult.SUCCESS == result.getStatus()) {
			try {
				Log.info("Taking screenshot on passed test");
				File source = ((TakesScreenshot) wdriver).getScreenshotAs(OutputType.FILE);

				// Define the base directory for storing screenshots
				String baseDirectory = "./Screenshots/";

				// Create the directory if it doesn't exist
				File directory = new File(baseDirectory);
				if (!directory.exists()) {
					directory.mkdirs();
				}

				// Generate a unique filename to avoid overwriting existing files
				String filename = generateUniqueFilename(baseDirectory, object.toString(), ".png");

				File destination = new File(baseDirectory + filename);
				FileUtils.copyFile(source, destination);
				Log.info("Screenshot taken and saved as " + filename);
				System.out.println("Screenshot Taken: " + filename);
			} catch (Exception e) {
				System.out.println("Exception while taking screenshot " + e.getMessage());
			}
		}
	}

	private String generateUniqueFilename(String directory, String baseFilename, String extension) {
		String filename = baseFilename + extension;
		int count = 1;
		while (new File(directory + filename).exists()) {
			filename = baseFilename + "_" + count + extension;
			count++;
		}
		return filename;
	}
	

	public static AndroidDriver launchApp(String deviceName) throws IOException {

		// dataMap = readDeviceConfig(deviceName);
		String server = JavaUtils.configProperties.get("appiumServer");
		String port = dataMap.get("PORT");
		// super.startAppiumServer(server, port);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (deviceName.equalsIgnoreCase("Emulator1")) {
			DefaultExecutor executor = new DefaultExecutor();
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

			CommandLine launchEmul = new CommandLine("C:/Program Files/Genymobile/Genymotion/player");
			launchEmul.addArgument("--vm-name");
			launchEmul.addArgument("\"" + dataMap.get("DEVICENAME") + "\"");
			executor.setExitValue(1);
			executor.execute(launchEmul, resultHandler);
		}
		try {
			Thread.sleep(60000);
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.UDID, dataMap.get("UDID"));
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, dataMap.get("DEVICENAME"));
			capabilities.setCapability(AndroidMobileCapabilityType.PLATFORM_NAME, "Android");
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, dataMap.get("ANDROIDVERSION"));
			capabilities.setCapability("browserName", "Android");
			capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "in.novopay.sli");
			capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".ui.activities.LoginActivity");
			Thread.sleep(3000);
			mdriver = new AndroidDriver(new URL("http://" + server + ":" + port + "/wd/hub"), capabilities);
			Log.info("Launched the Calculator app successfully");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return mdriver;
	}

}
