ProxyAutoConfigBasedWebDriverFactory
===============

# What is this?

This is a small [Katalon Studio](https://www.katalon.com/) project for demonstration purpose.
You can download the zip of the project from the [RELASES](https://github.com/kazurayam/ProxyAutoConfigBasedWebDriverFactory/releases) page,
unzip it, and open it with your Katalon Studio.

# Background

Quite a few Katalon Studio users work behind the corporates Proxy server. Me too.

You can setup a Proxy for Katalon Studio.
See ["Proxy Preference"](https://docs.katalon.com/katalon-studio/docs/proxy-preferences.html) page.

Katalon Studio referes to this proxy config in several ways.
1. The browser opend by `WebUI.openBrowser()` on your PC (which is behind the corporate's Proxy) will have the same proxy as configured for Katalon Studio. The Katalon test scripts will have access to the URLs on the Internet through that proxy.
2. The Spy and Recorder features of Katalon Studio implictly opens browsers with the same proxy confing.
3. As of v6.1.0 Katalon Studio requires communicattion with [Plugin Store](https://store.katalon.com/). If you are behind proxy, you MUST configure proxy appropriately. Otherwise you can not use Katalon Stduio with plugins.
4. You may opt in using [Katalon Analytics](https://www.katalon.com/katalon-analytics/) as a backend for your tests in Katalon Studio. If you are behind proxy, you MUST configure proxy appropriately.

# Problem to solve

The [Proxy Preference](https://docs.katalon.com/katalon-studio/docs/proxy-preferences.html) of Katalon Studio implicitly assumes the following.
1. The Katalon Studio's Proxy does not support specifying URL patterns to bypass proxy. See [Chromium option --proxy-bypass-list](https://www.chromium.org/developers/design-documents/network-settings)
2. You have atmost 1 proxy server. The preference does not support having 2 or more Proxy servers in the local network you are in.

Unfortunately, I encountered some difficulities.

## Machines with Private IP Address

For development purpose, I have many machines with [Private IP Address](https://www.lifewire.com/what-is-a-private-ip-address-2625970). What is "Private IP Address"?
The IP address in the range of followings are regarded Private:
- 10.0.0.0 to 10.255.255.255
- 172.16.0.0 to 172.31.255.255
- 192.168.0.0 to 192.168.255.255

Sometimes I need to test a Application-Under-Test on machines with private IP address. They are machines for development purpose. They are kept away from public access. For example: `http://172.23.4.56`. I need to let browsers to make HTTP request to those private IP address DIRECT bypassing any Proxy.

On the other hand, Katalon Studio v6.1.0 requires access to the Plugin Store, therefore
I need to setup Katalon Studio with "Manual Proxy" at a single URL.

If I choose to setup "Manual Proxy" to Katalon Studio, I can no longer test the UAT on machines with Private IP address, because "--proxy-bypass-list" equivalent feature is not supported by Katalon Studio. This is a big issue for me. I can not test UAT on the development machines at all.

## Multiple Proxy servers

I work on a intra-net of my organization with 2 proxy servers. Most of the public URLs are available through the proxy A, but a few of URLs are only available through the proxy B. On the other hand, Katalon Studio naively assumes we have only 1 proxy server. Unfortunately my intra-net is not as simple as this.

# Solution

My organization requires workers to use Proxy Auto-config (a.k.a. PAC). As long as workers launch browsers with URL of proxy.pac appropriately configured to browsers, every URL is accessible.

What is PAC? See https://en.wikipedia.org/wiki/Proxy_auto-config

So I want to, when I execute test cases, launch browsers from Katalon Studio so that browsers employ the PAC script provided by my network administrators.

# How to solve it

I can not rely on Katalon's built-in `WebUI.openBrowser()` instruction any longer. I need to lauch browsers for myself specifying options in detail.

I have developed a custom Groovy class `com.kazurayam.katalon.ProxyAutoConfigBasedWebDriverFactory`. This is a factory class with which you can launch Firefox/Chrome browser with the PAC is configured.

The source code of the class is [here](https://github.com/kazurayam/ProxyAutoConfigBasedWebDriverFactory/blob/master/Keywords/com/kazurayam/katalon/ProxyAutoConfigBasedWebDriverFactory.groovy).

An sample test case which uses this factory class is [here](https://github.com/kazurayam/ProxyAutoConfigBasedWebDriverFactory/blob/master/Scripts/demo/accessDirectToHostWithPrivateAddress/Script1571363427925.groovy).

I would quote the test case fragment here for quick reference

```
// These are just my case. You should change those as you require.
String PAC_URL = 'http://172.xxx.xxx.xxx/proxy.pac'
String targetURL = "http://192.yyy.yyy.yyy/"

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
```

Only Firefox and Chrome is supported by this class. IE/Safari/Edge --- I do not need them.

`ProxyAutoConfigBasedWebDriverFactory` does not refer to the Proxy config for Katalon Studio itself. The browsers lauched by this class are independent of the Katalon Studio's Proxy preferences.

# API Doc

- [Groovy doc](https://kazurayam.github.io/ProxyAutoConfsigBasedWebDriverFactory/api/index.html)
