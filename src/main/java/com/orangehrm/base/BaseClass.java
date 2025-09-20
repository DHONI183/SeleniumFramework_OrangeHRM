package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;	
	
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>(); 
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);
	
	
	/*
	 * Load the Configuration File
	 */
	@BeforeSuite
	public void loadConfig() throws IOException {
		prop = new Properties();
		FileInputStream fis = new FileInputStream("./src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.properties file loaded");
		
		// Start the Extent Report
		// ExtentManager.getReporter(); --> This has been implemented in TestListner
	}
	
	// Getter method for soft assert
		public SoftAssert getSoftAssert() {
			return softAssert.get();
		}
	
/*
 * 
 */
	@BeforeMethod
	@Parameters("browser")
	public synchronized void setup(String browser) {
		System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
		launchBrowser(browser);
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
	public synchronized void launchBrowser(String browser) {
/*
		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {
			//driver = new ChromeDriver();  
			driver.set(new ChromeDriver());
			ExtentManager.registerDriver(getDriver());
		} else if (browser.equalsIgnoreCase("edge")) {
			//driver = new EdgeDriver(); 
			driver.set(new EdgeDriver());
			ExtentManager.registerDriver(getDriver());
		} else if (browser.equalsIgnoreCase("firefox")) {
			//driver = new FirefoxDriver(); 
			driver.set(new FirefoxDriver());
			ExtentManager.registerDriver(getDriver());
		} else {
			throw new IllegalArgumentException("Browser Not Supported:" + browser);
		}*/
		

		boolean seleniumGrid = Boolean.parseBoolean(prop.getProperty("seleniumGrid"));
		String gridURL = prop.getProperty("gridURL");
		
		if (seleniumGrid) {
		    try {
		        if (browser.equalsIgnoreCase("chrome")) {
		            ChromeOptions options = new ChromeOptions();
		            options.addArguments( "--disable-gpu", "--window-size=1920,1080");
		            driver.set(new RemoteWebDriver(new URL(gridURL), options));
		        } else if (browser.equalsIgnoreCase("firefox")) {
		            FirefoxOptions options = new FirefoxOptions();
		            options.addArguments("-headless");
		            driver.set(new RemoteWebDriver(new URL(gridURL), options));
		        } else if (browser.equalsIgnoreCase("edge")) {
		            EdgeOptions options = new EdgeOptions();
		            options.addArguments("--headless=new", "--disable-gpu","--no-sandbox","--disable-dev-shm-usage");
		            driver.set(new RemoteWebDriver(new URL(gridURL), options));
		        } else {
		            throw new IllegalArgumentException("Browser Not Supported: " + browser);
		        }
		        logger.info("RemoteWebDriver instance created for Grid in headless mode");
		    } catch (MalformedURLException e) {
		        throw new RuntimeException("Invalid Grid URL", e);
		    }
		} else {

		if (browser.equalsIgnoreCase("chrome")) {
			
			// Create ChromeOptions
			ChromeOptions options = new ChromeOptions();
		//	options.addArguments("--headless=new");
		//	options.addArguments("--headless"); // Run Chrome in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU for headless mode
			options.addArguments("--width=1920"); // Set window size
			options.addArguments("--height=1080"); // Set window size
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
			options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments

			// driver = new ChromeDriver();
			driver.set(new ChromeDriver(options)); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			logger.info("ChromeDriver Instance is created.");
		} else if (browser.equalsIgnoreCase("firefox")) {
			
			// Create FirefoxOptions
			FirefoxOptions options = new FirefoxOptions();
			options.addArguments("--headless"); // Run Firefox in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
			options.addArguments("--width=1920"); // Set browser width
			options.addArguments("--height=1080"); // Set browser height
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD environments
			options.addArguments("--disable-dev-shm-usage"); // Prevent crashes in low-resource environments

			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver(options)); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			logger.info("FirefoxDriver Instance is created.");
		} else if (browser.equalsIgnoreCase("edge")) {
			
			EdgeOptions options = new EdgeOptions();
			options.addArguments("--headless"); // Run Edge in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU acceleration
			options.addArguments("--window-size=1920,1080"); // Set window size
			options.addArguments("--disable-notifications"); // Disable pop-up notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD
			options.addArguments("--disable-dev-shm-usage"); // Prevent resource-limited crashes
			
			// driver = new EdgeDriver();
			driver.set(new EdgeDriver(options)); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			logger.info("EdgeDriver Instance is created.");
		} else {
			throw new IllegalArgumentException("Browser Not Supported:" + browser);
		}
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
	}
	
	/*
	 * getter method for WebDriver
	 */
	public static WebDriver getDriver() {
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
