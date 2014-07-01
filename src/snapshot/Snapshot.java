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

import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import semblance.reporters.Report;
import semblance.reporters.SystemLogReport;
import semblance.results.IResult;
import snapshot.runners.ScreenshotRunner;

/**
 * Uses Selenium WebDriver to take screen-shots
 *
 * @author balnave
 */
public class Snapshot {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        String configUrlOrFilePath = "./config.json";
        int argIndex = 0;
        for (String arg : args) {
            if (args.length >= argIndex + 1) {
                if (arg.equalsIgnoreCase("-cf") || arg.equalsIgnoreCase("-config")) {
                    configUrlOrFilePath = args[argIndex + 1];
                }
            }
            argIndex++;
        }
        ScreenshotRunner runner = new ScreenshotRunner(configUrlOrFilePath);
        try {
            List<IResult> results = runner.run();
            runner.report();
            //
            // log the summary of all results
            Report report = new SystemLogReport(results);
            report.out();
        } catch (Exception ex) {
            Logger.getLogger(Snapshot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
