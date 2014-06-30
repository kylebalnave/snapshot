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
package snapshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import semblance.results.IResult;
import semblance.runners.Runner;
import snapshot.webdriver.WindowSize;

/**
 * Uses Selenium WebDriver to take screen-shots
 *
 * @author balnave
 */
public class ScreenshotRunner extends Runner {

    public ScreenshotRunner(Map config) {
        super(config);
    }

    @Override
    public List<IResult> run() throws Exception {
        ExecutorService execSvc = Executors.newFixedThreadPool(((Number) getConfigValue("threads", 10)).intValue());
        List<String> urls = (List<String>) getConfigValue("urls", new ArrayList());
        List<List<Number>> dimensions = (List<List<Number>>) getConfigValue("dimensions", new ArrayList());
        List<Map<String, String>> drivers = (List<Map<String, String>>) getConfigValue("drivers", new ArrayList());
        final String outDirPath = (String) getConfigValue("out", "./out");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String formattedNow = sdf.format(cal.getTime());
        final File outDir = new File(new File(outDirPath + File.separator + formattedNow), "./");
        outDir.mkdirs();
        //
        // loop through each driver
        for (final Map<String, String> driver : drivers) {
            //
            // loop through each screen dimension setting
            for (final List<Number> dimension : dimensions) {
                boolean hasHubUrl = false;
                List<ScreenshotSeleniumDriver> queue = new ArrayList<ScreenshotSeleniumDriver>();
                //
                // loop through each url 
                for (final String url : urls) {
                    String driverName = driver.containsKey("name") ? driver.get("name") : "firefox";
                    String driverVersion = driver.containsKey("version") ? driver.get("version") : "";
                    String driverHub = driver.containsKey("hub") ? driver.get("hub") : "";
                    hasHubUrl = hasHubUrl || !driverHub.isEmpty();
                    WindowSize size = new WindowSize(driverName, dimension.get(0), dimension.get(1));

                    ScreenshotSeleniumDriver sShot = new ScreenshotSeleniumDriver(driverName, driverVersion, driverHub, url, size, outDir, false);
                    queue.add(sShot);
                }
                List<Future<List<IResult>>> futureResults = execSvc.invokeAll(queue);
                for (Future<List<IResult>> res : futureResults) {
                    if (res.get() != null) {
                        results.addAll(res.get());
                    }
                }
            }
        }
        return results;
    }
}