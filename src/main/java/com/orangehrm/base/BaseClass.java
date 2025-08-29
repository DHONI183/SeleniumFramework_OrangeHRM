package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;	
	protected static WebDriver driver;
	protected static ActionDriver actionDriver;	
	
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	/*
	 * Load the Configuration File
	 */
	@BeforeSuite
	public void loadConfig() throws IOException {
		prop = new Properties();
		FileInputStream fis = new FileInputStream("./src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");
	}

	@BeforeMethod
	public void setup() {
		System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(3);
		logger.info("WebDriver Initialized and Browser Maximized");
		logger.trace("This is a Trace message");
		logger.error("This is a error message");
		logger.debug("This is a debug message");
		logger.fatal("This is a fatal message");
		logger.warn("This is a warm message");
		
		// Initialize the actionDriver only once
		
		if(actionDriver == null) {
		actionDriver = new ActionDriver(driver);
		logger.info("ActionDriver instance is created");
		}		
	}
	
	/*
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	public void launchBrowser() {

		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {
			driver = new ChromeDriver();  
		} else if (browser.equalsIgnoreCase("edge")) {
			driver = new EdgeDriver(); 
		} else if (browser.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver(); 			
		} else {
			throw new IllegalArgumentException("Browser Not Supported:" + browser);
		}
	}

	/*
	 * Configure browser settings such as implicit wait , maximize the browser and navigate to the url
	 */
	public void configureBrowser() {
		
		// Implicit Wait
		String implicitwait = prop.getProperty("implicitWait");
		int timeout = Integer.parseInt(implicitwait);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));	
		
		// maximize the driver
		driver.manage().window().maximize();
		
		// Navigate to URL
		try {
			driver.get(prop.getProperty("url"));
		} catch (Exception e) {
			logger.info("Failed to Navigate to the URL:" + e.getMessage());
		}
	}

	@AfterMethod
	public void tearDown() {
		if (driver != null) {
			try {
				driver.quit();
			} catch (Exception e) {
				logger.info("unable to quit the driver:" + e.getMessage());
			}
		}
		logger.info("WebDriver instance is closed");
		driver = null;
		actionDriver = null;
	}
	
	// getter method for Driver
	public WebDriver getDriver() {
		
		if(driver==null) {
			logger.info("WebDriver is not initialized");
			throw new IllegalStateException("Webdriver is not initialized");
		}
		return driver;
	}
	
	// getter method for ActionDriver
	public static ActionDriver getActionDriver() {
		
		if(actionDriver==null) {
			logger.info("ActionDriver is not initialized");
			throw new IllegalStateException("Actiondriver is not initialized");
		}
		return actionDriver;
	}
	
	// Driver setter method
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	// Prop getter method
	public static Properties getProp() {
		return prop;	
	}
	
	
	// Static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
