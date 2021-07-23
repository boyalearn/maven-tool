package com.maven.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ImportTool {

    public static String PATH = "D:\\IdeaWorkSpace\\Company\\huludao\\resourcecode\\dependency";

    public static void main(String[] args) {
        createBatchMavenInstallCmd(PATH);
    }

    public static void createBatchMavenInstallCmd(String dir) {
        File fileDir = new File(dir);
        if (!fileDir.isDirectory()) {
            throw new RuntimeException("请输入一个文件目录");
        }
        File[] files = fileDir.listFiles();

        for (File file : files) {
            try {
                createMavenInstallCmd(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void createMavenInstallCmd(File file) throws IOException, SAXException, ParserConfigurationException {
        if (!file.isFile() && !file.getName().endsWith(".jar")) {
            throw new RuntimeException("");
        }

        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            if (!jarEntry.isDirectory()) {

                if (jarEntry.getName().endsWith("/pom.xml")) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(inputStream);
                    Node artifactId = doc.getElementsByTagName("artifactId").item(0);
                    Node version = doc.getElementsByTagName("version").item(0);
                    Node groupId = null;
                    NodeList groupIds = doc.getElementsByTagName("groupId");
                    if (groupIds.getLength() <= 0) {
                        NodeList parent = doc.getElementsByTagName("parent");
                        NodeList childNodes = parent.item(0).getChildNodes();
                        for (int i = 0; i < childNodes.getLength(); i++) {
                            Node item = childNodes.item(i);
                            if (item.getNodeName().equals("groupId")) {
                                groupId = item;
                            }
                        }
                    }
                    if (null == groupId) {
                        groupId = groupIds.item(0);
                    }
                    StringBuffer sb=new StringBuffer();
                    sb.append("mvn install:install-file -Dfile=");
                    sb.append(file.getAbsolutePath());
                    sb.append(" -DgroupId=");
                    sb.append(groupId.getTextContent());
                    sb.append(" -DartifactId=");
                    sb.append(artifactId.getTextContent());
                    sb.append(" -Dversion="+version.getTextContent());
                    sb.append(" -Dpackaging=jar;");
                    System.out.println(sb);
                    return;
                }

            }
        }


    }
}
