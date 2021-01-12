package services.screen;

import services.log.Errors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Shoot {
    /**
     * Сделать фотку в переданном пути
     *
     * @param path
     */
    public void Shut(File path) {
        Date date = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");

        try {
            ImageIO.write(grabScreen(), "JPG",
                    new File(path, "screen" + formatForDateNow.format(date) + "." + "JPG"));
        } catch (IOException e) {
            Errors.createErrorFile(e);
        }
    }

    private BufferedImage grabScreen() {
        try {
            return new Robot()
                    .createScreenCapture(new Rectangle(Toolkit
                            .getDefaultToolkit()
                            .getScreenSize()));
        } catch (AWTException e) {
            return null;
        }
    }
}
