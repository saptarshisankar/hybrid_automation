# Welcome to my Hybrid Automation Framework

## Prerequisites:

To run the scripts in your local environment, you will need the following:

1. Java
2. Gradle
3. Appium
4. An emulator running in your local environment (optional)

I am using the TestNG framework.
All the code is writted in Java.
Webdriver of choice is Selenium
Android driver of choice is Appium

### For Running Android Automation, you need:

1. Appium installed and running in your local environment.
2. A device that is turned on, unlocked, and has "usb debugging" turned on, or an emulator that is turned on and unlocked.

## How is this framework handling the test assertions?

Assertions in this framework are primarily handled using TestNG's `SoftAssert`. TestNG is a testing framework for Java, and `SoftAssert` allows you to continue executing test steps even if an assertion fails. It collects all assertion failures and reports them at the end of the test, capturing multiple failures in a single test case without stopping the test prematurely.

Here's how assertions are handled using `SoftAssert`:

1. `softAssert.assertTrue(true, "Message")`:

   - Used to make an assertion. If the condition is `true`, the test continues with an associated message. If `false`, the assertion fails without stopping the test.
2. `softAssert.fail("Message")`:

   - Explicitly marks an assertion as failed with an associated message.
3. `softAssert.assertEquals(actualValue, expectedValue)`:

   - Compares actual and expected values and marks the assertion as failed if they are not equal.
4. `softAssert.assertAll()`:

   - Called at the end of the test to trigger reporting of all assertion failures captured by `SoftAssert`. If any assertion has failed, this method throws an exception, marking the test as failed.

Here's how assertions are used in the code:

- Assertions validate different aspects of the page and the data being processed, such as checking if the page has fully loaded, validating the contents of the "dobTextBox," and ensuring that the date of birth was updated successfully.
- In case of assertion failures, appropriate error messages are printed, and the test continues to execute subsequent steps, allowing you to capture multiple assertion failures in one run.
- At the end of the test, after all steps are executed, `softAssert.assertAll()` is called to report any assertion failures. Failures will be logged or reported as test failures.

This approach with `SoftAssert` allows your test script to capture and report multiple assertion failures within a single test run, making it easier to diagnose and fix issues.

## Test Cases:

### Scenario 1: Web Automation

- Test Case 1: Login Page - Authenticate Successfully
- Test Case 2: My Info
- Test Case 3: Logout

### Scenario 2: Mobile Application Automation

- Test Case: Calculator Functionality Test

### Scenario 3: WebSocket API Automation

- Test Case: WebSocket API Test for States List

### Scenario 4: Mobile Responsive Automation

- Test Case: Mobile Responsive Testing

For test execution, execute the following TestNG XML files:

- Scenario_1.xml for UI automation.
- Scenario_2.xml for Android automation.
- MyWebSocketClientTest class for Websocket automation.
- Scenario_4.xml for mobile responsive automation.

Each scenario and test case comes with specific steps, preconditions, and assumptions to ensure proper testing of web applications, mobile applications, WebSocket APIs, and responsive design.
