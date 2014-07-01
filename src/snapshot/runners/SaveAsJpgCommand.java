package snapshot.runners;

import java.awt.Color;
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

        try {
            //read image file
            bufferedImage = ImageIO.read(new File(imageInPath));
            // create a blank, RGB, same width and height, and a white background
            int newWidth = (int) (bufferedImage.getWidth() * scaleFactor);
            int newHeight = (int) (bufferedImage.getHeight() * scaleFactor);
            BufferedImage newBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = newBufferedImage.createGraphics();
            graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            boolean success = graphic.drawImage(bufferedImage, 0, 0, newWidth, newHeight, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
            // write to jpeg file
            ImageIO.write(newBufferedImage, "jpg", new File(imageOutPath));
            graphic.dispose();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
