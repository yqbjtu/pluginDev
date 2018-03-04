package com.yq.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class FileHelper {

    public static void main(String[] args) {
        if ((args == null) || args.length != 1) {
            System.out.println("properties file should be as a argument");
            return;
        }
        else {
            System.out.println("args[0]:" + args[0]);
        }

        File file = new File(".");
        System.out.println("CurrentDir:" + file.getAbsolutePath());
        String fileName = args[0];
        File inPropsFile = new File(fileName);
        Properties props = new Properties();
        try {
            InputStream inStream = new FileInputStream(inPropsFile);
            props.load(inStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String filename = props.getProperty("filename");
        String content = props.getProperty("content");
        Boolean overwrite = false;
        String overwriteString = props.getProperty("overwrite").toLowerCase();
        //checkbox 的默认值是on，当你在网页找那个选中获取checkbox的值，默认为on
        if ("true".equals(overwriteString) || "on".equals(overwriteString)) {
            overwrite = true;
        }

        System.out.println(filename);
        System.out.println(content);
        System.out.println("overwriteString:" + overwriteString);
        System.out.println("overwrite:" + overwrite);
        System.out.println();

        File newfile = new File(filename);
        try {
            if (newfile.exists() && !overwrite) {
                System.out.println("File '"+ newfile.getCanonicalPath() + "' already exists1!");
                System.exit(1); 
            }
            else {
                newfile.createNewFile();
                OutputStream outStream = new FileOutputStream(newfile.getAbsolutePath());
                IOUtils.write(content, outStream);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1); 
        }
    }

}
