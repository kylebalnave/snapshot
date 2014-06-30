/*
 * Copyright (C) 2014 kyleb2
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package snapshot;

import java.io.File;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import semblance.results.IResult;
import semblance.runners.Runner;
import snapshot.results.ScreenshotResult;
import snapshot.webdriver.WebDriverHelper;
import snapshot.webdriver.WindowSize;

/**
 *
 * @author kyleb2
 */
/**
 * Private Class
 *
 * @author balnave
 */
class ScreenshotSeleniumDriver extends Runner implements Callable<List<IResult>> {

    private String driverName;
    private String driverVersion;
    private String driverHubURL;
    private String url;
    private WindowSize size;
    private File outDir;
    private boolean saveAsJpeg = false;

    public ScreenshotSeleniumDriver(Map config) {
        super(config);
    }

    public ScreenshotSeleniumDriver(String driverName, String driverVersion, String driverHubURL, String url, WindowSize size, File outDir) {
        this(driverName, driverVersion, driverHubURL, url, size, outDir, false);
    }

    public ScreenshotSeleniumDriver(String driverName, String driverVersion, String driverHubURL, String url, WindowSize size, File outDir, boolean saveAsJpeg) {
        this(null);
        this.driverName = driverName;
        this.driverVersion = driverVersion;
        this.driverHubURL = driverHubURL;
        this.url = url;
        this.outDir = outDir;
        this.size = size;
        this.saveAsJpeg = saveAsJpeg;
    }

    @Override
    public List<IResult> call() throws Exception {
        WebDriver driver;
        // create the IResult instance
        List<IResult> localResults = new ArrayList<IResult>();
        ScreenshotResult result = null;
        String screenshotURI = null;
        boolean success = false;
        try {
            long startMs = System.currentTimeMillis();
            if (driverHubURL != null && !driverHubURL.isEmpty()) {
                driver = WebDriverHelper.setupDriver(driverName, driverVersion, new URL(driverHubURL), url, size);
            } else {
                driver = WebDriverHelper.setupDriver(driverName, driverVersion, url, size);
            }
            if (driver == null) {
                throw new Exception(String.format("Driver Name %s is of unknown type", driverName));
            }
            // create a screenshot
            File tmpScreenshot = WebDriverHelper.takeScreenshot(driver);
            String fileName = makeURLSlug(
                    String.format("%s%s-%spx-%s",
                            driverName,
                            driverVersion,
                            size.getStringLabel(),
                            url)) + (saveAsJpeg ? ".jpg" : ".png");
            File permScreenshot = new File(outDir, fileName);
            success = tmpScreenshot.exists();
            if (saveAsJpeg) {
                IMCommand formatCommand = new SaveAsJpgCommand(1.0);
                // save as jpeg to save disk space
                success = formatCommand.execute(tmpScreenshot.getAbsolutePath(), permScreenshot.getAbsolutePath());
            } else {
                tmpScreenshot.renameTo(permScreenshot);
            }
            screenshotURI = success ? permScreenshot.getAbsolutePath() : null;
            // stop the driver
            WebDriverHelper.teardownDriver(driver);
            long endMs = System.currentTimeMillis();
            result = new ScreenshotResult(screenshotURI, screenshotURI != null, endMs - startMs);
        } catch (Exception ex) {
            result = new ScreenshotResult(screenshotURI, success, ex.getMessage());
            //result = new ScreenshotResult(url, false, String.format("Exception creating screenshot %s", ex.getMessage()));
        } catch (Error er) {
            result = new ScreenshotResult(screenshotURI, success, er.getMessage());
            //result = new ScreenshotResult(url, false, String.format("Exception creating screenshot %s", er.getMessage()));
        } finally {
            localResults.add(result);
        }
        return localResults;
    }

    private final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private String makeURLSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public List<IResult> run() throws Exception, Error {
        return call();
    }

}
