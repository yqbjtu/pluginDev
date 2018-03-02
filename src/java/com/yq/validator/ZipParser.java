package com.yq.validator;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ZipParser {

    public static void main(String[] args) {
        String zipname = "src\\plugins\\FileUtils.zip";
        Path path = Paths.get(zipname);

        try {
            FileSystem zipfs = FileSystems.newFileSystem(Paths.get(zipname), null);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            unzip(zipname, "output/FileUtils");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        parsePluginXml("output/FileUtils/plugin.xml");
    }

    /**
     * Unzips the specified zip file to the specified destination directory.
     * Replaces any files in the destination, if they already exist.
     * @param zipFilename the name of the zip file to extract
     * @param destFilename the directory to unzip to
     * @throws IOException
     */
    public static void unzip(String zipFilename, String destDirname)
        throws IOException{

      final Path destDir = Paths.get(destDirname);
      //if the destination doesn't exist, create it
      if(Files.notExists(destDir)){
        System.out.println(destDir + " does not exist. Creating...");
        Files.createDirectories(destDir);
      }
      FileSystem zipfs = FileSystems.newFileSystem(Paths.get(zipFilename), null);
      try (FileSystem zipFileSystem = zipfs){
        final Path root = zipFileSystem.getPath("/");

        //walk the zip file tree and copy files to the destination
        Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
          @Override
          public FileVisitResult visitFile(Path file,
              BasicFileAttributes attrs) throws IOException {
              final Path destFile = Paths.get(destDir.toString(), file.toString());
              System.out.printf("Extracting file %s to %s\n", file, destFile);
              Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
              return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult preVisitDirectory(Path dir,
              BasicFileAttributes attrs) throws IOException {
              final Path dirToCreate = Paths.get(destDir.toString(), dir.toString());
              if(Files.notExists(dirToCreate)){
                  System.out.printf("Creating directory %s\n", dirToCreate);
                  Files.createDirectory(dirToCreate);
              }
            return FileVisitResult.CONTINUE;
          }
        });
      }
    }

/*
 */
    static public void parsePluginXml(String file) {
        SAXReader reader = new SAXReader();
        File xmlFile = new File(file);
        Document doc = null;
        try {
           doc = reader.read(xmlFile);
           doc.setXMLEncoding("UTF-8");
        }catch (DocumentException e) {
            e.printStackTrace();
        }

        Element root = doc.getRootElement();

        StringBuffer rootValue = new StringBuffer();
        List attrList = root.attributes();
        int index = 0;
        for (Object obj : attrList) {
            Attribute attr = (Attribute)attrList.get(index);
            index++;
            rootValue.append(",Attr Name=" + attr.getName() + ", Value=" + attr.getValue());
        }

        String idValue = root.attribute("id").getValue();
        String nameValue = root.attribute("name").getValue();
        String versionValue = root.attribute("version").getValue();
        rootValue.append("\r\nAttr Name=" + nameValue + ", idValue=" + idValue + ", versionValue=" + versionValue);

        System.out.println("root " + rootValue );

        Element pluginHeadElement = root.element("head");
        Element pluginDescElement = pluginHeadElement.element("description");
        String pluginDescValue = pluginDescElement.getText();
        System.out.println("plugin description:[" + pluginDescValue + "]");

        Element bodyElement = root.element("body");
        List stepElementList = bodyElement.elements("step");
        System.out.println("step size in body element:[" + stepElementList.size() + "]");

        for(Object obj : stepElementList) {
            if (obj instanceof Element) {
                Element element = (Element)obj;
                String stepName = element.attribute("name").getValue();

                StringBuffer stepValue = new StringBuffer();
                stepValue.append("stepName:["+ stepName + "], ");
                Element descElement = element.element("description");
                String description = descElement.getText();
                stepValue.append("stepDesc:["+ description + "] ");

                Element propsElement = element.element("properties");
                List propList = propsElement.elements("property");
                for(Object object : propList) {
                    if (object instanceof Element) {
                        //<property name="filename" label = "File Name" type="input" defaultValue="file1.txt" description="The name of the file."/>
                        Element pElement = (Element)object;
                        String propName = pElement.attribute("name").getValue();
                        String labelName = pElement.attribute("label").getValue();
                        String proptype = pElement.attribute("type").getValue();
                        Attribute defValAttr = pElement.attribute("defaultValue");
                        String propDefaultValue = "";
                        if (defValAttr != null) {
                            propDefaultValue = defValAttr.getText();
                        }
                        String propDesc = pElement.attribute("description").getValue();

                        StringBuffer propValue = new StringBuffer();
                        propValue.append("propName:["+ propName + "], ");
                        propValue.append("labelName:["+ labelName + "], ");
                        propValue.append("proptype:["+ proptype + "], ");
                        propValue.append("propDefaultValue:["+ propDefaultValue + "], ");
                        propValue.append("propDesc:["+ propDesc + "] ");
                        System.out.println("    property element:" + propValue);
                    }
                }

                Element exeElement = element.element("execution");
                /*
                 * <execution type="java">
          <arg value="-cp"/>
          <arg path="lib/log4j.jar;lib/commons-lang3.jar;lib/fileUtils.jar"/>
          <arg value="-jar"/>
          <arg value="com.yq.folder.FolderHelper"/>
          <arg value="${INPUT_PROPS}"/>
      </execution>
                 */
                if (exeElement != null) {
                    Attribute typeAttr = exeElement.attribute("type");
                    StringBuffer execValue = new StringBuffer();
                    execValue.append("execution type attr :["+ typeAttr.getText() + "] ");
                    List argList = propsElement.elements("arg");

                    for(Object argObj : argList) {
                        Element argElement = (Element)argObj;
                        Attribute valueAttr = exeElement.attribute("value");
                        Attribute pathAttr = exeElement.attribute("path");
                        if (valueAttr != null) {
                            execValue.append("execution value attr :["+ valueAttr.getText() + "] ");
                        }
                        if (pathAttr != null) {
                            execValue.append("execution path attr :["+ pathAttr.getText() + "] ");
                        }
                    }
                    System.out.println("    execution element:" + execValue);
                }

                System.out.println("step element:" + stepValue);
            }
        }
    }


}
