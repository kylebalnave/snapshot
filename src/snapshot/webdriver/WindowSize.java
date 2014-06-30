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

/**
 * Controls the Dimension of a WebDriver Window Adjusts according to standard
 * scroll bar widths
 *
 * @author balnave
 */
public class WindowSize {

    private final Number width;
    private final Number height;
    private final String stringLabel;

    public WindowSize(String driverName, Number width, Number height) {
        this.stringLabel = String.valueOf(width);
        this.width = width.intValue() + getOffset(driverName).intValue();
        this.height = height;
    }

    public Number getWidth() {
        return width;
    }

    public Number getHeight() {
        return height;
    }

    public String getStringLabel() {
        return stringLabel;
    }
    
    
    private Number getOffset(String driverName) {
        Number offset = 0;
        if (driverName.equalsIgnoreCase("firefox")) {
            offset = 31;
        }
        return offset;
    }

}
