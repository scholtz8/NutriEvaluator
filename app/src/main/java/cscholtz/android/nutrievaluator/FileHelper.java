package cscholtz.android.nutrievaluator;

import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileHelper {

    private static final int BUFFER_SIZE = 8192;//2048;
    private static String ExternalDirectory = Environment.getExternalStorageDirectory().toString();
    private static String sourcePathParent = ExternalDirectory+"/PDF/";
    private static String destinationPath = ExternalDirectory+"/ZIPS";

    public static void zip(String destinationFileName,String fileName,Boolean OneFile) {
        FileOutputStream fileOutputStream;
        ZipOutputStream zipOutputStream = null;
        try {
            //Carpeta Zips donde se guardan comprimidos
            File folderDestination = new File(destinationPath);
            if(!folderDestination.exists()){
                folderDestination.mkdirs();
            }
            //Crea archivo zip
            String destination = destinationPath +"/"+ destinationFileName+".zip";
            File file = new File(destination);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

            String sourcePath = sourcePathParent+fileName;
            if(OneFile){
                zipFile(zipOutputStream, sourcePath);
            }else{
                zipFiles(zipOutputStream, sourcePath);
            }

        } catch (Exception e) {
            Log.d("FileHelper", e.getMessage());
            return;
        } finally {
            if (zipOutputStream != null)
                try {
                    zipOutputStream.close();
                } catch (Exception e) {
                    Log.d("zipOutputStream", e.getMessage());
                }
        }
        return;

    }

    private static void zipFile(ZipOutputStream zipOutputStream, String sourcePath) throws IOException {
        File file = new File(sourcePath);
        zipper(zipOutputStream,file,sourcePathParent);
    }

    private static void zipFiles(ZipOutputStream zipOutputStream, String sourcePath) throws IOException {
        File files = new File(sourcePath);
        File[] fileList = files.listFiles();
        for (java.io.File file : fileList) {
            if (file.isDirectory()) {
                zipFiles(zipOutputStream, file.getPath());
            } else {
                zipper(zipOutputStream,file,sourcePath);
            }
        }
    }

    private static void zipper(ZipOutputStream zipOutputStream,File file,String removePath) throws IOException {
        String entryPath = "";
        BufferedInputStream input;

        byte data[] = new byte[BUFFER_SIZE];
        FileInputStream fileInputStream = new FileInputStream(file.getPath());
        input = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
        entryPath = file.getAbsolutePath().replace(removePath, "");

        ZipEntry entry = new ZipEntry(entryPath);
        zipOutputStream.putNextEntry(entry);

        int count;
        while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
            zipOutputStream.write(data, 0, count);
        }
        zipOutputStream.closeEntry();
        input.close();
    }
}


