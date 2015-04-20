package edu.ufl.cise.cnt5106c.file;

/**
 * Created by adalton on 3/15/15.
 * Credit to Krishna at http://www.javabeat.net/java-split-merge-files/ for initial design
 */

import edu.ufl.cise.cnt5106c.log.LogHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MergeFile {
    private static final String FILE_NAME = "ImageFile.jpg";

    public static void main(String[] args) {
        File ofile = new File("ImageFile2.jpg");
        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead = 0;
        List<File> list = new ArrayList<>();
        for (int i = 0; i < 211; i++) {
            list.add(new File("parts/" + FILE_NAME + ".part" + i));
        }
        try {
            fos = new FileOutputStream(ofile, true);
            for (File file : list) {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0, (int) file.length());
                assert (bytesRead == fileBytes.length);
                assert (bytesRead == (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;
        } catch (Exception e) {
            LogHelper.getLogger().warning(e);
        }
    }
}
