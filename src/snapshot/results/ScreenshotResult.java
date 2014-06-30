/*
 * Copyright (C) 2014 kyleb2
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

package snapshot.results;

import semblance.results.Result;

/**
 *
 * @author kyleb2
 */
public class ScreenshotResult extends Result {

    public ScreenshotResult(String uri, boolean hasPassed) {
        super(uri, hasPassed);
        if(hasPassed) {
            message = "Saved screen shot.";
            reason = message;
        } else {
            message = "Failed to save screen shot.";
            reason = message;
        }
    }
    
    public ScreenshotResult(String uri, boolean hasPassed, long timeMs) {
        super(uri, hasPassed);
        if(hasPassed) {
            message = "Saved screen shot.";
            reason = message;
        } else {
            message = "Failed to save screen shot.";
            reason = message;
        }
        this.executionTimeMs = timeMs;
    }
    
    public ScreenshotResult(String uri, boolean hasPassed, String msg) {
        super(uri, hasPassed);
        if(hasPassed) {
            message = msg;
            reason = message;
        } else {
            message = msg;
            reason = message;
        }
    }
    
}
