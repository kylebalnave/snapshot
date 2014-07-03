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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Halves the size of an image and re-saves
 *
 * @author kyleb2
 */
public class SaveAsJpgCommand implements IMCommand {

    private final double scaleFactor;

    public SaveAsJpgCommand() {
        this.scaleFactor = 1.0;
    }

    public SaveAsJpgCommand(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public boolean execute(String imageInPath, String imageOutPath) {
        BufferedImage bufferedImage;
        boolean result;
        try {
            //read image file
            bufferedImage = ImageIO.read(new File(imageInPath));
            // create a blank, RGB, same width and height, and a white background
            int newWidth = (int) (bufferedImage.getWidth() * scaleFactor);
            int newHeight = (int) (bufferedImage.getHeight() * scaleFactor);
            BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = newBufferedImage.createGraphics();
            graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            result = graphic.drawImage(bufferedImage, 0, 0, newWidth, newHeight, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
            if (result) {
                // write to jpeg file
                result = ImageIO.write(newBufferedImage, "jpg", new File(imageOutPath));
                graphic.dispose();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

}
