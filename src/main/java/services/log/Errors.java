package services.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Errors {
    public static void createErrorFile(Exception e) {
        String str = e.getMessage();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("error.log"));
            writer.write(str);
            writer.close();
        } catch (IOException ioException) {
            //
        }
    }
}
