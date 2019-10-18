package com.kazurayam.katalon

import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory

/**
 * This class is supposed to be used as a Keyword in Katalon Studio.
 * 
 * This class is a factory which creates instances of ChromeDriver/FirefoxDriver
 * which are configured to use a Proxy Auto-config (PAC).
 * 
 * What is PAC? See https://en.wikipedia.org/wiki/Proxy_auto-config
 * 
 */

public class ProxyAutoConfigBasedWebDriverFactory {

	// constructor is hidden
	private ProxyAutoConfigBasedWebDriverFactory() {}

	/**
	 * This method instanciates a FirefoxDriver which is configured with netword.proxy.autoconfig_url option.
	 * 
	 * For Firefox preferences, see https://developer.mozilla.org/ja/docs/Mozilla_Networking_Preferences
	 * 
	 * The binary of gecko driver bundled in Katalon Studio is employed.
	 * 
	 * The Firefox browser launched by this method will run completely independent of the proxy config you put in Katalon Studio.
	 * 
	 * @param PAC_URL a URL of Proxy Auto-config for your organization. E.g, 'http://172.14.12.23/proxy.pac'
	 * @return an instance of FirefoxDriver
	 */
	@Keyword
	static WebDriver createGeckoDriver(String PAC_URL) {
		validatePacUrl(PAC_URL)
		String geckoDriverPath = DriverFactory.getGeckoDriverPath()
		if (geckoDriverPath != null) {
			KeywordUtil.logInfo(">>> firefoxDriverPath=${geckoDriverPath}")
			System.setProperty("webdriver.gecko.driver", geckoDriverPath)
			// see https://developer.mozilla.org/ja/docs/Mozilla_Networking_Preferences
			FirefoxOptions options = new FirefoxOptions()
			options.addPreference("network.proxy.type", 2)	// PAC
			options.addPreference("network.proxy.autoconfig_url", PAC_URL)
			WebDriver driver = new FirefoxDriver(options)
			return driver
		} else {
			KeywordUtil.logInfo(">>> DriverFactory.getFirefoxDirverPath() returned null.")
			return null
		}
	}

	/**
	 * This method instanciates a ChromeDriver which is configured with Proxy Auto-config.
	 * 
	 * The binary of chromedriver.exe bundled in Katalon Studio is employed.
	 * 
	 * The Chrome browser launched by this method will run completely independent of the proxy config you put in Katalon Studio.
	 * 
	 * @param PAC_URL a URL of Proxy Auto-config for your organization. E.g, 'http://172.24.12.23/proxy.pac'
	 * @return an instance of ChromeDriver
	 */
	@Keyword
	static WebDriver createChromeDriver(String PAC_URL) {
		validatePacUrl(PAC_URL)
		String chromeDriverPath = DriverFactory.getChromeDriverPath()
		if (chromeDriverPath != null) {
			KeywordUtil.logInfo(">>> chromeDriverPath=${chromeDriverPath}")
			System.setProperty("webdriver.chrome.driver", chromeDriverPath)
			Proxy proxy = new Proxy()
			proxy.setProxyAutoconfigUrl(PAC_URL)
			DesiredCapabilities capabilities = DesiredCapabilities.chrome()
			capabilities.setCapability("proxy", proxy)
			ChromeOptions options = new ChromeOptions()
			capabilities.setCapability(ChromeOptions.CAPABILITY, options)
			WebDriver driver = new ChromeDriver(capabilities)
			return driver
		} else {
			KeywordUtil.logInfo(">>> DriverFactory.getChromeDriverPath() returned null.")
			return null
		}
	}

	/**
	 * This method instanciates a WebDriver which is configured with Proxy Auto-config.
	 * 
	 * This can instanciate FirefoxDriver or ChromeDriver. It depends which browser type you choose when you
	 * execute a test case in Katalon Studio.
	 * 
	 * The browser launched by this method will run completely independent of the proxy config you put in Katalon Studio.
	 * 
	 * @param PAC_URL a URL of Proxy Auto-config for your organization. E.g, 'http://172.24.12.23/proxy.pac'
	 * @return
	 */
	@Keyword
	static WebDriver createWebDriver(String PAC_URL) {
		validatePacUrl(PAC_URL)
		String executedBrowser = DriverFactory.getExecutedBrowser().getName()
		WebDriver driver
		switch (executedBrowser) {
			case 'FIREFOX_DRIVER':
				driver = this.createGeckoDriver(PAC_URL)
				break
			case 'CHROME_DRIVER':
				driver = this.createChromeDriver(PAC_URL)
				break
			default:
				throw new IllegalStateException("unsupported browser type: ${executedBrowser}")
				break
		}
		return driver
	}

	private static void validatePacUrl(String PAC_URL) {
		Objects.requireNonNull(PAC_URL, "argument PAC_URL must not be null")
		try {
			URL packUrl = new URL(PAC_URL)
		} catch (MalformedURLException e) {
			e.printStackTrace()
			throw e
		}

	}
}

