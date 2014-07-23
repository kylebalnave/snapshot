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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import semblance.data.MapHelper;
import semblance.runners.MultiThreadRunner;
import semblance.runners.Runner;
import snapshot.webdriver.WindowSize;

/**
 * Uses Selenium WebDriver to take screen-shots
 *
 * @author balnave
 */
public class SnapshotRunner extends MultiThreadRunner {

    public static final String KEY_URLS = "urls";
    public static final String KEY_DIMENSIONS = "dimensions";
    public static final String KEY_DRIVERS = "drivers";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        callRunnerSequence(SnapshotRunner.class, args);
    }

    public SnapshotRunner(Map config) {
        super(config);
    }

    public SnapshotRunner(String configUrlOrFilePath) {
        super(configUrlOrFilePath);
    }

    @Override
    protected List<Runner> getRunnerCollection() {
        List<Runner> queue = new ArrayList<Runner>();
        List<String> urls = (List<String>) getConfigValue(KEY_URLS, new ArrayList());
        List<List<Number>> dimensions = (List<List<Number>>) getConfigValue(KEY_DIMENSIONS, new ArrayList());
        List<Map> drivers = (List<Map>) getConfigValue(KEY_DRIVERS, new ArrayList());
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
                //
                // loop through each url 
                for (final String url : urls) {
                    hasHubUrl = hasHubUrl || !driverHub.isEmpty();
                    WindowSize size = new WindowSize(driverName, dimension.get(0), dimension.get(1));
                    SnapshotSeleniumDriver sShot = new SnapshotSeleniumDriver(config, driverName, driverVersion, driverHub, url, size);
                    queue.add(sShot);
                }
            }
        }
        return queue;
    }
}
