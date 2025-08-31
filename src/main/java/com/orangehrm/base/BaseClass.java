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
	//protected static WebDriver driver;
	//protected static ActionDriver actionDriver;	
	
	/*
	 * this is done for parallel exicution ;
	 * we have created ThreadLocal type Variables
	 */
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>(); 
	
	
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
	
/*
 * 
 */
	@BeforeMethod
	public synchronized void setup() {
		System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(3);
		logger.info("WebDriver Initialized and Browser Maximized");
		
		/*
		// Initialize the actionDriver only once
		
		if(actionDriver == null) {
		actionDriver = new ActionDriver(driver);
		logger.info("ActionDriver instance is created." + Thread.currentThread().getId());
		}	
		*/	
		
		// Initialize ActionDriver for the Current thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initialized for thread: " + Thread.currentThread().getId());
	}
	
	/*
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	public synchronized void launchBrowser() {

		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {
			//driver = new ChromeDriver();  
			driver.set(new ChromeDriver());
		} else if (browser.equalsIgnoreCase("edge")) {
			//driver = new EdgeDriver(); 
			driver.set(new EdgeDriver());
		} else if (browser.equalsIgnoreCase("firefox")) {
			//driver = new FirefoxDriver(); 
			driver.set(new FirefoxDriver());
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
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));	
		
		// maximize the driver
		getDriver().manage().window().maximize();
		
		// Navigate to URL
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			logger.info("Failed to Navigate to the URL:" + e.getMessage());
		}
	}

	/*
	 * to shutdown the application
	 */
	@AfterMethod
	public synchronized void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				logger.info("unable to quit the driver:" + e.getMessage());
			}
		}
		logger.info("WebDriver instance is closed");
		driver.remove();
		actionDriver.remove();
		//driver = null;
		//actionDriver = null;
	}
	
	/*
	 * getter method for WebDriver
	 */
	public WebDriver getDriver() {
	/*	
		if(driver==null) {
			logger.info("WebDriver is not initialized");
			throw new IllegalStateException("Webdriver is not initialized");
		}
		return driver;
		*/
		
		if(driver.get()==null) {
			logger.info("WebDriver is not initialized");
			throw new IllegalStateException("Webdriver is not initialized");
		}
		return driver.get();
	}
	
	
	/*
	 * getter method for ActionDriver
	 */
	public static ActionDriver getActionDriver() {
		/*
		if(actionDriver==null) {
			logger.info("ActionDriver is not initialized");
			throw new IllegalStateException("Actiondriver is not initialized");
		}
		return actionDriver;
		*/
		if(actionDriver.get()==null) {
			logger.info("ActionDriver is not initialized");
			throw new IllegalStateException("Actiondriver is not initialized");
		}
		return actionDriver.get();
		
	}
	
	/*
	 * Driver setter method
	 */
	public void setDriver(ThreadLocal<WebDriver> driver) {
		this.driver = driver;
	}
	
	/*
	 * Prop getter method
	 */
	public static Properties getProp() {
		return prop;	
	}
	
	
	/*
	 * Static wait for pause
	 */
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
