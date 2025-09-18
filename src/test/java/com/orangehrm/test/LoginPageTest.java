package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass{

	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setupPages() {
		loginPage =	new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}
	
	
	@Test
	public void verifyValidLoginTest(String username , String password) {
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login(username, password);
		ExtentManager.logStep("Verifying Admin tab is Visible or Not");
		Assert.assertTrue(homePage.isAdminTabVisible() , "Admin tab should be visible after successfull login");
		ExtentManager.logStep("Validation Successfull");
		homePage.logout();
		ExtentManager.logStep("Logged out Successfully!");
		staticWait(2);
	}
		
	@Test(dataProvider="inValidLoginData",dataProviderClass=DataProviders.class)
	public void inValidLoginTest(String username , String password) {

		loginPage.login(username, password);
		String expectedErrorMessage = "Invalid credentials";
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage) , "Error message is not shown in the error message area");
		staticWait(2);
	}
}
