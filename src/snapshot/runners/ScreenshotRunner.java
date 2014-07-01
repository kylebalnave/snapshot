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
package snapshot.runners;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.data.MapHelper;
import semblance.results.IResult;
import semblance.runners.Runner;
import snapshot.webdriver.WindowSize;

/**
 * Uses Selenium WebDriver to take screen-shots
 *
 * @author balnave
 */
public class ScreenshotRunner extends Runner {

    public static final String KEY_URLS = "urls";
    public static final String KEY_DIR_OUT = "out";
    public static final String KEY_DIMENSIONS = "dimensions";
    public static final String KEY_DRIVERS = "drivers";
    public static final String KEY_THREADS = "threads";
    
    public static final String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";

    public ScreenshotRunner(Map config) {
        super(config);
    }

    public ScreenshotRunner(String configUrlOrFilePath) {
        super(configUrlOrFilePath);
    }

    @Override
    public List<IResult> run() throws Exception {
        int maxThreads = ((Number) getConfigValue(KEY_THREADS, 5)).intValue();
        ExecutorService execSvc = Executors.newFixedThreadPool(maxThreads);
        List<String> urls = (List<String>) getConfigValue(KEY_URLS, new ArrayList());
        List<List<Number>> dimensions = (List<List<Number>>) getConfigValue(KEY_DIMENSIONS, new ArrayList());
        List<Map> drivers = (List<Map>) getConfigValue(KEY_DRIVERS, new ArrayList());
        String outDirPath = (String) getConfigValue(KEY_DIR_OUT, "./out");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String formattedNow = sdf.format(cal.getTime());
        File outDir = new File(new File(outDirPath + File.separator + formattedNow), "./");
        outDir.mkdirs();
        //
        // log status
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Start!");
        //
        // loop through each driver
        for (Map<String, Object> driver : drivers) {
            String driverName = (String) MapHelper.getValue(driver, "name", "firefox");
            String driverVersion = (String) MapHelper.getValue(driver, "version", "");
            String driverHub = (String) MapHelper.getValue(driver, "hub", "");
            //
            // loop through each screen dimension setting
            for (final List<Number> dimension : dimensions) {
                boolean hasHubUrl = false;
                try {
                    List<ScreenshotSeleniumDriver> queue = new ArrayList<ScreenshotSeleniumDriver>();
                    //
                    // loop through each url 
                    for (final String url : urls) {
                        hasHubUrl = hasHubUrl || !driverHub.isEmpty();
                        WindowSize size = new WindowSize(driverName, dimension.get(0), dimension.get(1));
                        ScreenshotSeleniumDriver sShot = new ScreenshotSeleniumDriver(driverName, driverVersion, driverHub, url, size, outDir, true);
                        queue.add(sShot);
                    }
                    //
                    // run the thread pool
                    List<Future<List<IResult>>> futureResults = execSvc.invokeAll(queue);
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "Adding results!");
                    for (Future<List<IResult>> res : futureResults) {
                        if (res.get() != null) {
                            results.addAll(res.get());
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception in thread", ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception in thread", ex);
                } finally {
                    if (!execSvc.isShutdown()) {
                        Logger.getLogger(getClass().getName()).log(Level.INFO, "Shutdown thread pool!");
                        execSvc.shutdown();
                    }

                }
            }
        }
        Logger.getLogger(getClass().getName()).log(Level.INFO, "End!");
        return results;
    }
}
