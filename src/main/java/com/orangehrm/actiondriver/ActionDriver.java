package com.orangehrm.actiondriver;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.orangehrm.base.BaseClass;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		String s = BaseClass.getProp().getProperty("explicitWait");
		int a = Integer.parseInt(s);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(a));
	}

	// Wait for element to be clickable
	public WebElement waitForElementToBeClickable(By by) {
		try {
			return wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			System.out.println("Element not clickable: " + by + " | Error: " + e.getMessage());
			return null;
		}
	}

	// Wait for element to be visible
	public WebElement waitForElementToBeVisible(By by) {
		try {
			return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			System.out.println("Element not visible: " + by + " | Error: " + e.getMessage());
			return null;
		}
	}

	// Click on element (with wait)
	public void click(By by) {
		try {
			WebElement element = waitForElementToBeClickable(by);
			if (element != null) {
				element.click();
				System.out.println("Clicked on element: " + by);
			}
		} catch (Exception e) {
			System.out.println("Unable to click element: " + by + " | Error: " + e.getMessage());
		}
	}

	// Type text into element (with wait)
	public void enterText(By by, String text) {
		try {
			WebElement element = waitForElementToBeVisible(by);
			if (element != null) {
				element.clear();
				element.sendKeys(text);
				System.out.println("Typed '" + text + "' into element: " + by);
			}
		} catch (Exception e) {
			System.out.println("Unable to type into element: " + by + " | Error: " + e.getMessage());
		}
	}

	// Get text from element
	public String getText(By by) {
		try {
			WebElement element = waitForElementToBeVisible(by);
			if (element != null) {
				String text = element.getText();
				System.out.println("Text from element " + by + " is: " + text);
				return text;
			}
		} catch (Exception e) {
			System.out.println("Unable to get text from element: " + by + " | Error: " + e.getMessage());
		}
		return null;
	}

	// Check if element is displayed
	public boolean isDisplayed(By by) {
		try {
			WebElement element = waitForElementToBeVisible(by);
			return element != null && element.isDisplayed();
		} catch (Exception e) {
			System.out.println("Element not displayed: " + by + " | Error: " + e.getMessage());
			return false;
		}
	}

	// Compare Two Text
	public void compareText(By by1, By by2) {
		if (getText(by1).equals(getText(by2))) {
			System.out.println("Text Matching");
		} else {
			System.out.println("Text Not Matching");
		}
	}
/*	
	// Wait for the page to load
		public void waitForPageLoad(int timeOutInSec) {
			try {
				wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
						.executeScript("return document.readyState").equals("complete"));
				logger.info("Page loaded successfully.");
			} catch (Exception e) {
				logger.error("Page did not load within " + timeOutInSec + " seconds. Exception: " + e.getMessage());
			}
		}
*/
	
	// Compare Two Text
	public boolean compareText(String ExpectedString, By by) {
		try {
			if(getText(by).equals(ExpectedString)) {
				System.out.println("Text Matching");
				return true ;
			}else {
				System.out.println("Text Not Matching");
				return false;
			}
		} catch (Exception e) {
			System.out.println("Unable to Compare Text " + e.getMessage());
			return false;
		}
	}
	
	// Compare Two Text
	public boolean compareText(By by,String ExpectedString) {
		try {
			if(getText(by).equals(ExpectedString)) {
				System.out.println("Text Matching");
				return true ;
			}else {
				System.out.println("Text Not Matching");
				return false;
			}
		} catch (Exception e) {
			System.out.println("Unable to Compare Text " + e.getMessage());
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
			System.out.println("Unable to locate element: "+ e.getMessage());
		}
	}
	
	

}
