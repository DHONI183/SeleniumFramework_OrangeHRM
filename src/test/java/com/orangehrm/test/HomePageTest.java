package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class HomePageTest extends BaseClass{

	private LoginPage loginPage;
	private HomePage homePage;
	
	@BeforeMethod
	public void setupPages() {
		loginPage =	new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}
	
	@Test(dataProvider="validLoginData",dataProviderClass=DataProviders.class  )
	public void verifyOrangeHRMLogo(String username , String password) {
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login(username, password);
		Assert.assertTrue(homePage.verifyOrangeHRMlogo() , "This is not HomePage");
		ExtentManager.logStep("Validation Successful");
		ExtentManager.logStep("logged out Successfully!");
		
	}
	
}
