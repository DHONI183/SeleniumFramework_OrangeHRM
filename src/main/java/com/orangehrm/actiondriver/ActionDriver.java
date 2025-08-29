package com.orangehrm.actiondriver;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.LoggerManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger logger = LoggerManager.getLogger(ActionDriver.class);
	

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		String s = BaseClass.getProp().getProperty("explicitWait");
		int a = Integer.parseInt(s);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(a));
		logger.info("WebDriver instance is created");
	}

	// Wait for element to be clickable
	public WebElement waitForElementToBeClickable(By by) {
		try {
			return wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Element not clickable: " + by + " | Error: " + e.getMessage());
			return null;
		}
	}

	// Wait for element to be visible
	public WebElement waitForElementToBeVisible(By by) {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Element not visible: " + by + " | Error: " + e.getMessage());
			return null;
		}
	}

	// Click on element (with wait)
	public void click(By by) {
		
		String elementDescription = getElementDescription(by);
		try {
			WebElement element = waitForElementToBeClickable(by);
			if (element != null) {
				element.click();
				logger.info("Clicked on element: " + elementDescription);
			}
		} catch (Exception e) {
			logger.error("Unable to click element: " + elementDescription + " | Error: " + e.getMessage());
		}
	}

	// Type text into element (with wait)
	public void enterText(By by, String text) {
		try {
			WebElement element = waitForElementToBeVisible(by);
			if (element != null) {
				element.clear();
				element.sendKeys(text);
				logger.info("Typed '" + text + "' into element: " + by);
			}
		} catch (Exception e) {
			logger.error("Unable to type into element: " + by + " | Error: " + e.getMessage());
		}
	}

	// Get text from element
	public String getText(By by) {
		try {
			WebElement element = waitForElementToBeVisible(by);
			if (element != null) {
				String text = element.getText();
				logger.info("Text from element " + by + " is: " + text);
				return text;
			}
		} catch (Exception e) {
			logger.error("Unable to get text from element: " + by + " | Error: " + e.getMessage());
		}
		return null;
	}

	// Check if element is displayed
	public boolean isDisplayed(By by) {
		try {
			WebElement element = waitForElementToBeVisible(by);
			return element != null && element.isDisplayed();
		} catch (Exception e) {
			logger.error("Element not displayed: " + by + " | Error: " + e.getMessage());
			return false;
		}
	}

	// Compare Two Text
	public void compareText(By by1, By by2) {
		if (getText(by1).equals(getText(by2))) {
			logger.info("Text Matching");
		} else {
			logger.error("Text Not Matching");
		}
	}

	
	// Compare Two Text
	public boolean compareText(String ExpectedString, By by) {
		try {
			if(getText(by).equals(ExpectedString)) {
				logger.info("Text Matching");
				return true ;
			}else {
				logger.error("Text Not Matching");
				return false;
			}
		} catch (Exception e) {
			logger.error("Unable to Compare Text " + e.getMessage());
			return false;
		}
	}
	
	// Compare Two Text
	public boolean compareText(By by,String ExpectedString) {
		try {
			if(getText(by).equals(ExpectedString)) {
				logger.info("Text Matching");
				return true ;
			}else {
				logger.error("Text Not Matching");
				return false;
			}
		} catch (Exception e) {
			logger.error("Unable to Compare Text " + e.getMessage());
			return false;
		}
	}
	
	// Scroll to an Element
	public void scrollToElement(By by) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0],scrollIntoViw(true);", element);
		} catch (Exception e) {
			logger.error("Unable to locate element: "+ e.getMessage());
		}
	}
	
	// Method to get the description of an element using By locator

	/*
	 * 
	 */
	public String getElementDescription(By locator) {
		// Check for null driver or locator to avoid NullPointerException
		if (driver == null) {
			return "Driver is not initialized.";
		}
		if (locator == null) {
			return "Locator is null.";
		}

		try {
			// Find the element using the locator
			WebElement element = driver.findElement(locator);

			// Get element attributes
			String name = element.getDomProperty("name");
			String id = element.getDomProperty("id");
			String text = element.getText();
			String className = element.getDomProperty("class");
			String placeholder = element.getDomProperty("placeholder");

			// Return a description based on available attributes
			if (isNotEmpty(name)) {
				return "Element with name: " + name;
			} else if (isNotEmpty(id)) {
				return "Element with ID: " + id;
			} else if (isNotEmpty(text)) {
				return "Element with text: " + truncate(text, 50);
			} else if (isNotEmpty(className)) {
				return "Element with class: " + className;
			} else if (isNotEmpty(placeholder)) {
				return "Element with placeholder: " + placeholder;
			} else {
				return "Element located using: " + locator.toString();
			}
		} catch (Exception e) {
			return "Unable to describe element due to error: " + e.getMessage();
		}
	}

	
	// Utility method to check a String is not Null or empty
	private boolean isNotEmpty(String value) {
		return value!=null && !value.isEmpty();
	}
	
	// Utility Method to truncate long String
	private String truncate(String value , int maxLength) {
		if(value == null || value.length()<=maxLength) {
			return value;
		}
		return value.substring(0,maxLength) + "....";
	}
	
	
	
	
	
}
