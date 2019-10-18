import org.openqa.selenium.WebDriver

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kazurayam.katalon.ProxyAutoConfigBasedWebDriverFactory

// These are just my case. You should change those as you require.
String PAC_URL = 'http://172.24.172.3/proxy.pac'
String targetURL = "http://172.24.172.3/"

// You want to open browser which is configured to use Proxy Auto-config of your organization
WebDriver driver = ProxyAutoConfigBasedWebDriverFactory.createWebDriver(PAC_URL)
DriverFactory.changeWebDriver(driver)
// only Firefox and Chrome supported

// do whatever as usual in Katalon test
WebUI.navigateToUrl(targetURL)
TestObject to = new TestObject().addProperty('xpath', ConditionType.EQUALS ,
	"//a[contains(.,'Trend Micro Security')]")
WebUI.verifyElementPresent(to, 30)
WebUI.delay(3)
WebUI.closeBrowser()