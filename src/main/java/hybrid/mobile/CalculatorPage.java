package hybrid.mobile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import hybrid.utilities.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class CalculatorPage  extends BasePage{

	

	@AndroidFindBy(id = "op_sub")
	WebElement subtractionButton;

	@AndroidFindBy(id = "com.google.android.calculator:id/op_mul")
	WebElement multiplicationButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@content-desc='plus']")
	WebElement additionButton;

	@AndroidFindBy(id = "op_div")
	WebElement divisionButton;

	@AndroidFindBy(id = "eq")
	WebElement equalsButton;
	


	public CalculatorPage(AppiumDriver mDriver) {
		super(mDriver);
		PageFactory.initElements(new AppiumFieldDecorator(mDriver), this);

	}

	public void performCalculation(String operand1, String operator, String operand2, String expectedResult, AppiumDriver mdriver) throws InterruptedException {
		
		WebElement  inputElement = mdriver.findElement(By.id("digit_" +operand1 ));
		WebElement  inputElement2 = mdriver.findElement(By.id("digit_" +operand2 ));
		switch (operator) {
        case "+":
        	inputElement.click();
            additionButton.click();
            break;
        case "-":
            subtractionButton.click();
            break;
        case "x":
            multiplicationButton.click();
            break;
        case "/":
            divisionButton.click();
            break;
    }
		inputElement2.click();
		
		
		equalsButton.click();
		
		WebElement resultsTest =  mdriver.findElement(By.id("com.google.android.calculator:id/result_final"));
		
		
		
		
		String actualResult = resultsTest.getText();
		assert actualResult.equals(expectedResult) : "Calculation result is incorrect";
	}

}
