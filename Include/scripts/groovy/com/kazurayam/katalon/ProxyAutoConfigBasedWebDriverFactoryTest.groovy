package com.kazurayam.katalon

import static org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.WebDriver

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import static org.hamcrest.CoreMatchers.*

@RunWith(JUnit4.class)
public class ProxyAutoConfigBasedWebDriverFactoryTest {

	private String PAC_URL = 'http://172.24.172.3/proxy.pac'	// This is just my case. You should change this as your network requires

	private String targetURL = "http://172.24.172.3/"	// This is just my case. You should change this as you want

	private ProxyAutoConfigBasedWebDriverFactory factory

	@Before
	void setup() {
		factory = new ProxyAutoConfigBasedWebDriverFactory()
	}

	@Test
	void test_isUrlAccessible() {
		assertTrue(
			"${PAC_URL} is not accessible. Possibly you are NOT at your work.",
			ProxyAutoConfigBasedWebDriverFactory.isUrlAccessible(PAC_URL))
	}

	@Test
	void test_createGeckoDriver() {
		assertTrue("${PAC_URL} is not accessible. Possibly you are NOT at your work.",
			ProxyAutoConfigBasedWebDriverFactory.isUrlAccessible(PAC_URL))
		//
		String executedBrowser = DriverFactory.getExecutedBrowser().getName()
		switch (executedBrowser) {
			case 'FIREFOX_DRIVER':
			WebDriver driver = ProxyAutoConfigBasedWebDriverFactory.createGeckoDriver(PAC_URL)
			DriverFactory.changeWebDriver(driver)
			WebUI.navigateToUrl(targetURL)
			TestObject to = new TestObject().addProperty('xpath', ConditionType.EQUALS ,"//a[contains(.,'Trend Micro Security')]")
			WebUI.verifyElementPresent(to, 30)
			WebUI.delay(3)
			WebUI.closeBrowser()
			break
			default:
			WebUI.comment("executedBrowser=${executedBrowser}. test_createGeckoDriver() was skipped")
			break
		}
	}

	@Test
	void test_createChromeDriver() {
		assertTrue("${PAC_URL} is not accessible. Possibly you are NOT at your work.",
			ProxyAutoConfigBasedWebDriverFactory.isUrlAccessible(PAC_URL))
		//
		String executedBrowser = DriverFactory.getExecutedBrowser().getName()
		switch (executedBrowser) {
			case 'CHROME_DRIVER':
			WebDriver driver = ProxyAutoConfigBasedWebDriverFactory.createChromeDriver(PAC_URL)
			DriverFactory.changeWebDriver(driver)
			WebUI.navigateToUrl(targetURL)
			TestObject to = new TestObject().addProperty('xpath', ConditionType.EQUALS ,"//a[contains(.,'Trend Micro Security')]")
			WebUI.verifyElementPresent(to, 30)
			WebUI.delay(3)
			WebUI.closeBrowser()
			break
			default:
			WebUI.comment("executedBrowser=${executedBrowser}. test_createChromeDriver() was skipped")
			break
		}
	}

	@Test
	void test_createWebDriver() {
		assertTrue("${PAC_URL} is not accessible. Possibly you are NOT at your work.",
			ProxyAutoConfigBasedWebDriverFactory.isUrlAccessible(PAC_URL))
		//
		String executedBrowser = DriverFactory.getExecutedBrowser().getName()
		switch (executedBrowser) {
			case 'CHROME_DRIVER':
			case 'FIREFOX_DRIVER':
			WebDriver driver = ProxyAutoConfigBasedWebDriverFactory.createWebDriver(PAC_URL)
			DriverFactory.changeWebDriver(driver)
			WebUI.navigateToUrl(targetURL)
			TestObject to = new TestObject().addProperty('xpath', ConditionType.EQUALS ,"//a[contains(.,'Trend Micro Security')]")
			WebUI.verifyElementPresent(to, 30)
			WebUI.delay(3)
			WebUI.closeBrowser()
			break
			default:
			WebUI.comment("executedBrowser=${executedBrowser}. test_createChromeDriver() was skipped")
			break
		}
	}
}
