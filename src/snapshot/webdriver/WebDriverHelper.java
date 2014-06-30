/*
 * Copyright (C) 2014 balnave
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package snapshot.webdriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

/**
 * Utility methods used with Selenium WebDriver
 *
 * @author balnave
 */
public class WebDriverHelper {

    private static Proxy proxy = null;

    public static void setProxyDetails(String proxyUrl) {
        proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(proxyUrl)
                .setFtpProxy(proxyUrl)
                .setSslProxy(proxyUrl);
    }

    private static DesiredCapabilities getCapabilities(String driverName, String version, boolean includeDriver) {
        DesiredCapabilities capabilities = null;
        if (includeDriver && driverName.equalsIgnoreCase("firefox")) {
            capabilities = DesiredCapabilities.firefox();
        } else if (includeDriver && driverName.equalsIgnoreCase("chrome")) {
            capabilities = DesiredCapabilities.chrome();
        } else if (includeDriver && driverName.equalsIgnoreCase("ie") || driverName.equalsIgnoreCase("internet explorer")) {
            capabilities = DesiredCapabilities.internetExplorer();
        } else if (includeDriver && driverName.equalsIgnoreCase("phantomjs")) {
            capabilities = DesiredCapabilities.phantomjs();
        } else if (includeDriver && driverName.equalsIgnoreCase("ipad")) {
            capabilities = DesiredCapabilities.ipad();
        } else if (includeDriver && driverName.equalsIgnoreCase("iphone")) {
            capabilities = DesiredCapabilities.iphone();
        } else if (includeDriver && driverName.equalsIgnoreCase("android")) {
            capabilities = DesiredCapabilities.android();
        } else if (includeDriver && driverName.equalsIgnoreCase("opera")) {
            capabilities = DesiredCapabilities.opera();
        }
        if(capabilities == null) {
            capabilities = DesiredCapabilities.firefox();
        }
        if (version != null) {
            capabilities.setVersion(version);
        }
        if (proxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        }
        return capabilities;
    }

    /**
     * Gets a Remote WebDriver
     *
     * @param driverName
     * @param version
     * @param hubURL
     * @return
     * @throws MalformedURLException
     */
    public static WebDriver getDriver(String driverName, String version, URL hubURL) throws MalformedURLException {
        DesiredCapabilities capabilities = getCapabilities(driverName, version, true);
        return new RemoteWebDriver(hubURL, capabilities);
    }

    /**
     * Gets a Local WebDriver
     *
     * @param driverName
     * @param version
     * @return
     * @throws MalformedURLException
     */
    public static WebDriver getDriver(String driverName, String version) throws MalformedURLException {
        WebDriver driver = null;
        DesiredCapabilities capabilities = getCapabilities(driverName, version, false);
        if (driverName.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver(capabilities);
        } else if (driverName.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver(capabilities);
        } else if (driverName.equalsIgnoreCase("ie") || driverName.equalsIgnoreCase("internet explorer")) {
            driver = new InternetExplorerDriver(capabilities);
        } else if (driverName.equalsIgnoreCase("phantomjs")) {
            driver = new PhantomJSDriver(capabilities);
        }
        return driver;
    }

    /**
     * Starts a WebDriver with Screen Dimensions
     *
     * @param driver
     * @param url
     * @param size
     * @return
     */
    public static WebDriver setupDriver(WebDriver driver, String url, WindowSize size) {
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(size.getWidth().intValue(), size.getHeight().intValue()));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(url);
        return driver;
    }

    public static WebDriver setupDriver(String driverName, String version, String url, WindowSize size) throws MalformedURLException {
        return setupDriver(getDriver(driverName, version), url, size);
    }

    public static WebDriver setupDriver(String driverName, String version, URL hubUrl, String url, WindowSize size) throws MalformedURLException {
        return setupDriver(getDriver(driverName, version, hubUrl), url, size);
    }

    /**
     * Closes the current WebDriver
     *
     * @param driver
     * @return
     */
    public static void teardownDriver(WebDriver driver) {
        driver.quit();
    }

    /**
     * Takes a screenshot
     *
     * @param driver
     * @return
     */
    public static File takeScreenshot(WebDriver driver) {
        WebDriver augmentedDriver = new Augmenter().augment(driver);
        return ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
    }

}
