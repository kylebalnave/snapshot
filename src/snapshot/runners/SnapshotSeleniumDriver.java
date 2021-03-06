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
package snapshot.runners;

import java.io.File;
import java.net.URL;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.testng.log4testng.Logger;
import semblance.results.ErrorResult;
import semblance.results.FailResult;
import semblance.results.IResult;
import semblance.results.PassResult;
import semblance.runners.Runner;
import snapshot.webdriver.WebDriverHelper;
import snapshot.webdriver.WindowSize;

/**
 * A Class used to take screenshots using Selenium WebDriver
 *
 * @author kyleb2
 */
class SnapshotSeleniumDriver extends Runner implements Callable<List<IResult>> {

    private String driverName;
    private String driverVersion;
    private String driverHubURL;
    private String url;
    private WindowSize size;
    private File outDir;
    private Boolean saveAsJpeg = false;
    private int delayAfterLoadMs = 0;

    public static final String KEY_SAVE_AS_JPEG = "saveAsJpeg";
    public static final String KEY_DIR_OUT = "out";
    public static final String KEY_DELAY_AFTER_LOAD = "delayAfterLoadMs";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";

    public SnapshotSeleniumDriver(Map config) {
        super(config);
    }

    public SnapshotSeleniumDriver(String driverName, String driverVersion, String driverHubURL, String url, WindowSize size) {
        this(null, driverName, driverVersion, driverHubURL, url, size);
    }

    public SnapshotSeleniumDriver(Map config, String driverName, String driverVersion, String driverHubURL, String url, WindowSize size) {
        this(config);
        this.driverName = driverName;
        this.driverVersion = driverVersion;
        this.driverHubURL = driverHubURL;
        this.url = url;
        this.size = size;
        this.saveAsJpeg = (Boolean) getConfigValue(KEY_SAVE_AS_JPEG, false);
        this.delayAfterLoadMs = ((Number)getConfigValue(KEY_DELAY_AFTER_LOAD, 0)).intValue();
        //
        // generate an output directory name
        String outDirPath = (String) getConfigValue(KEY_DIR_OUT, "./out");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String formattedNow = sdf.format(cal.getTime());
        //
        // create the directory if it doesn't exist
        this.outDir = new File(new File(outDirPath + File.separator + formattedNow), "./");
        this.outDir.mkdirs();
    }

    @Override
    public List<IResult> call() throws Exception {
        WebDriver driver;
        // create the IResult instance
        List<IResult> localResults = new ArrayList<IResult>();
        IResult result = null;
        String screenshotURI = null;
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
            //
            // sleep to allow page to finish rendering
            Logger.getLogger(this.getClass()).info(String.format("Sleeping for %sms after loading %s", delayAfterLoadMs, url));
            Thread.sleep(delayAfterLoadMs);
            //
            // create a screenshot
            File tmpScreenshot = WebDriverHelper.takeScreenshot(driver);
            String fileName = makeURLSlug(
                    String.format("%s%s-%spx-%s",
                            driverName,
                            driverVersion,
                            size.getStringLabel(),
                            url)) + (saveAsJpeg ? ".jpg" : ".png");
            File permScreenshot = new File(outDir, fileName);
            boolean success = tmpScreenshot.exists();
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
            if (screenshotURI != null) {
                result = new PassResult(screenshotURI, "Screenshot saved", "Screenshot saved", 0, 0, endMs - startMs);
            } else {
                result = new FailResult(screenshotURI, "Screenshot NOT saved", "Screenshot NOT saved", 0, 0, endMs - startMs);
            }
        } catch (Exception ex) {
            result = new ErrorResult(screenshotURI, ex.getMessage());
        } catch (Error er) {
            result = new ErrorResult(screenshotURI, er.getMessage());
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

}
