package edu.ufl.cise.cnt5106c.file;


/**
 * Created by adalton on 3/15/15.
 * Credit to Krishna at http://www.javabeat.net/java-split-merge-files/ for initial design
 */

import edu.ufl.cise.cnt5106c.log.LogHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SplitFile {

    public void process(File inputFile, int partSize){

        FileInputStream inputStream;
        String newFileName;
        FileOutputStream filePart;
        int fileSize = (int) inputFile.length();
        int nChunks = 0, read = 0, readLength = partSize;
        byte[] byteChunkPart;
        try {
            inputStream = new FileInputStream(inputFile);
            while (fileSize > 0) {
                if (fileSize <= 5) {
                    readLength = fileSize;
                }
                byteChunkPart = new byte[readLength];
                read = inputStream.read(byteChunkPart, 0, readLength);
                fileSize -= read;
                assert (read == byteChunkPart.length);
                nChunks++;
                newFileName = inputFile.getParent() + "/parts/" +
                        inputFile.getName() + "/" + Integer.toString(nChunks - 1);
                filePart = new FileOutputStream(new File(newFileName));
                filePart.write(byteChunkPart);
                filePart.flush();
                filePart.close();
                byteChunkPart = null;
                filePart = null;
            }
            inputStream.close();
        } catch (IOException e) {
            LogHelper.getLogger().warning(e);
        }
    }

    public static void main(String[] args) {
        SplitFile sf = new SplitFile();
        sf.process(new File("/Users/adalton/code/uf/cnt5106c/files/ImageFile.jpg"), 5000);
    }
}