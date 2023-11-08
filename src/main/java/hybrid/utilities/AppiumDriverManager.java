package hybrid.utilities;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

public class AppiumDriverManager {
    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    private static AppiumDriverManager instance;

    public AppiumDriverManager() {
        // Private constructor to prevent external instantiation
    }

    public static AppiumDriverManager getInstance() {
        if (instance == null) {
            instance = new AppiumDriverManager();
        }
        return instance;
    }

    public AppiumDriver getDriver() {
        return driver.get();
    }

    public void setDriver(AppiumDriver appiumDriver) {
        driver.set(appiumDriver);
    }

    public void initializeDriver(String appiumServerURL, DesiredCapabilities capabilities) throws MalformedURLException {
        if (getDriver() == null) {
            AndroidDriver appiumDriver = new AndroidDriver(new URL(appiumServerURL), capabilities);
            setDriver(appiumDriver);
        }
    }
}
