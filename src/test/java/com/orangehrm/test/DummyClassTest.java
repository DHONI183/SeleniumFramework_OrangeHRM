package com.orangehrm.test;

import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;

public class DummyClassTest extends BaseClass{

	@Test
	public void dummyTest() {
		
		String title = driver.getTitle();
		assert title.equals("OrangeHRM"):"Test Failed - Title is Not Matching";
		System.out.println("Test Passed - Title is Matching");
		
	}
}
