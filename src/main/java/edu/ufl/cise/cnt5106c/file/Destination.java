package edu.ufl.cise.cnt5106c.file;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;


/**
 * Created by adalton on 3/22/15.
 */
public class Destination {

    private final File _file;
    private final File  _partsDir;
    private static final String partsLocation = "files/parts/";

    public Destination(int peerId, String fileName){
        _partsDir = new File("./peer_" + peerId + "/" + partsLocation + fileName);
        _partsDir.mkdirs();
        _file = new File(_partsDir.getParent() + "/../" + fileName);
    }

    public byte[][] getAllPartsAsByteArrays(){
        File[] files = _partsDir.listFiles (new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });
        byte[][] ba = new byte[files.length][getPartAsByteArray(1).length];
        for (File file : files) {
            ba[Integer.parseInt(file.getName())] = getByteArrayFromFile(file);
        }
        return ba;
    }

    public byte[] getPartAsByteArray(int partId) {
        File file = new File(_partsDir.getAbsolutePath() + "/" + partId);
        return getByteArrayFromFile(file);
    }

    public void writeByteArrayAsFilePart(byte[] part, int partId){
        FileOutputStream fos;
        File ofile = new File(_partsDir.getAbsolutePath() + "/" + partId);
        try {
            fos = new FileOutputStream(ofile, true);
            fos.write(part);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            LogHelper.getLogger().warning(e);
        } catch (IOException e) {
            LogHelper.getLogger().warning(e);
        }
    }

    private byte[] getByteArrayFromFile(File file){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            int bytesRead = fis.read(fileBytes, 0, (int) file.length());
            fis.close();
            assert (bytesRead == fileBytes.length);
            assert (bytesRead == (int) file.length());
            return fileBytes;
        } catch (FileNotFoundException e) {
            LogHelper.getLogger().warning(e);
        } catch (IOException e) {
            LogHelper.getLogger().warning(e);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException ex) {}
            }
        }
        return null;

    }

    public void splitFile(int partSize){
        SplitFile sf = new SplitFile();
        sf.process(_file, partSize);
    }
}
