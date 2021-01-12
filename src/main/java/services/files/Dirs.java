package services.files;

import services.log.Errors;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dirs {

    /**
     * По умолчанию возвращается Desktop
     *
     * @return
     */
    public File getHomeDir() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getHomeDirectory();
    }

    /**
     * Создает новую директорию с названием типа "ГГГГ.ММ.ДД_ЧЧ.ММ.СС"
     *
     * @param path
     * @return
     */
    public File createNewDir(File path) {
        Date date = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
        File theDir = new File(path.toString() + "/" + formatForDateNow.format(date));

        if (!theDir.exists()) {

            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                Errors.createErrorFile(se);
            }
        }
        return theDir;
    }
}
