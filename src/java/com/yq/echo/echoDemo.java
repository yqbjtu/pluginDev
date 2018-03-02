package com.yq.echo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

public class echoDemo {

    public static void main(String[] args) {

        /*
         * String fileName = System.getProperty("propFile");
         * if ((fileName == null) || (fileName.isEmpty()))
         * {
         * System.out.println(
         * "properties file is not been provided by -DpropFile=xxxpath");
         * return;
         * }
         */

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
        String stepPropsValue = props.getProperty("scriptBody");
        System.out.println(stepPropsValue);
    }

}
