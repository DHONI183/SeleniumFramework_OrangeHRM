package com.orangehrm.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class TestListener implements ITestListener , IAnnotationTransformer{

	
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}
	
	
	/*
	 *  Trigger when Test starts
	 */
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		// Start logging in Extent Reports
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test Started : " + testName  );
	}

	/*
	 *  Triggered when a Test succeeds
	 */
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test Passed Successfully!", "Test End:" + testName);
	}

	/*
	 *  Triggered when a Test Fails
	 */
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failureMessage = result.getThrowable().getMessage();
		ExtentManager.logStep(failureMessage);
		ExtentManager.logFailure(BaseClass.getDriver(), "Test Failed Successfully!", "Test End:" + testName);
	}

	/*
	 * Triggered when a test Skips
	 */
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test Skipped" + testName);
	}

	/*
	 * Triggered when a suite Starts
	 */
	@Override
	public void onStart(ITestContext context) {
		// Initialize the Extent Report
		ExtentManager.getReporter();
	}

	/*
	 * Triggered when the suite ends
	 */
	@Override
	public void onFinish(ITestContext context) {
		// Flush the Extent Reports
		ExtentManager.endTest();
	}

}
