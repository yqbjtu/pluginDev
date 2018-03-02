package com.yq.folder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FolderHelper {

    public static void main(String[] args) {
        if ((args == null) || args.length != 1) {
            System.out.println("properties file should be as a argument");
            return;
        }
        else {
            System.out.println("args[0]:" + args[0]);
        }

        File file = new File(".");
        System.out.println("currentDir:" + file.getAbsolutePath());
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

        String foldername = props.getProperty("foldername");
        System.out.println(foldername);
        File newfile = new File(foldername);
        boolean creationStatus = false;
        creationStatus = newfile.mkdir();
    }

}
