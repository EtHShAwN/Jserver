package com.maven.test.restful;

import java.io.*;
import java.util.Scanner;
import java.util.HashMap;

public class configLoader {

    public static HashMap<String, byte[]> readConfig() {

        String configPath = "config/config.conf";
        String parseMode = "";
        boolean inblock = false;
        HashMap<String, byte[]> map = new HashMap<>();
        String root = "";

        try (FileReader fileHandle = new FileReader(configPath);
             Scanner conf = new Scanner(fileHandle)) {
            while (conf.hasNextLine()) {
                String data = conf.nextLine().trim();
                if (!inblock) {
                    switch (data) {
                        case "[server]":
                            if (!conf.hasNextLine()) {
                                System.out.println(System.getProperty("user.dir"));
                                System.err.println("[ Error ] Configuration format is invalid");
                            }
                            inblock = true;
                            parseMode = "server";
                            System.out.println("[  Log  ] Parsing server configuration");
                            break;
                    }
                }

                while (conf.hasNextLine()) {
                    root = parseConf(parseMode, conf.nextLine().replace(" ", ""), root, map);
                }
            }
        } catch (FileNotFoundException f) {
            System.err.println("[ Error ] Can't Find Configuration");
        } catch (IOException e) {
            System.err.println("[ Error ] IO Exception");
        }

        return map;
    }

    private static String parseConf(String mode, String data, String root, HashMap<String, byte[]> map) {

        String response = "";
        String responseHead = "HTTP/1.1 200 OK\r\n" +
                "Server: Jserver/1.0-DEV on " +
                System.getProperty("os.name") + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length:";

        switch (mode) {
            case "server":
                data = data.replace(" ", "");
                String[] tokens = data.split("=");

                if (tokens.length < 2 || tokens[1] == null) {
                    System.err.println("[ Error ] Syntax Error @ config/config.conf");
                    return root;
                }

                if (tokens[0].equals("root")) {
                    /*
                     * specify the folder of your web app's root directory
                     * that means everyone who has access to your site
                     * can visit any asset under root folder
                     */
                    File directory = new File(tokens[1]);
                    if (directory.exists() && directory.isDirectory()) {
                        root = tokens[1];
                        System.out.println("[  Log  ] Map /root to " + root);
                    } else {
                        System.err.println("[ Error ] Can't map /root to " + tokens[1]);
                    }
                } else {
                    try {
                        FileReader File = new FileReader(root + tokens[1]);
                        Scanner fileHandle = new Scanner(File);
                        String fileContent = "";
                        while (fileHandle.hasNextLine()) {
                            fileContent += fileHandle.nextLine();
                        }
                        response = responseHead +
                                fileContent.length() +
                                "\r\n\r\n" +
                                fileContent;
                        System.out.println(response);
                        map.put(tokens[0], response.getBytes());
                        fileHandle.close();
                    } catch (IOException e) {
                        System.err.println("[ Error ] Parser unable to read file: " + tokens[1]);
                    }
                }
                break;

            case "host":
                System.out.println("Host");
                break;
        }
        return root;
    }
}
