package hybrid.utilities;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class CustomTestNgListener implements ITestListener {

	private static ITestResult testResult;

	WebDriver wDriver;
	BasePage bpage = new BasePage(wDriver);

	@Override
	public void onTestStart(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		System.out.println("Test method " + methodName + " is starting.");
		testResult = result;
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// This method is called when a test method passes successfully.
		String methodName = result.getMethod().getMethodName();
		ITestContext context = result.getTestContext();

		System.out.println("Test method " + methodName + " has passed successfully.");
		if (result.getStatus() == ITestResult.SUCCESS) {
			bpage.captureScreenshotOnPassedTest(result, context.getAttribute("Test_id"));

		}
	}

	// Create a public method to access the testResult.
	public static ITestResult getTestResult() {
		return testResult;
	}

}
