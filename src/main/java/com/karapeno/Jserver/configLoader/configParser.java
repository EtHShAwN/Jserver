package com.karapeno.Jserver.configLoader;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import com.karapeno.Jserver.Server;

/**
 * This module is used to parse configuration
 * in INI format and setup parameters in the server module.
 * 
 * @author karapeno
 */
public class configParser {

    private static int parseMode = 0;
    private static final Map<String, Integer> sectionMap = new HashMap<>();

    static {
        sectionMap.put("path", 1);
        sectionMap.put("server", 2);
        sectionMap.put("logging", 3);
        sectionMap.put("security", 4);
        sectionMap.put("performance", 5);
    }

    public static int parse(String conf) {
        if (conf == null || conf.isEmpty()) {
            System.err.println("[ Error ] Invalid configuration line.");
            return 0;
        }

        switch (conf.charAt(0)) {
            case '#':
            case ';':
                System.out.println("[  Log  ] < Parser >: Comment block");
                return 1;
            case '[':
                System.out.println("[  Log  ] < Parser >: Section block");
                parseMode = parseSection(conf.substring(1, conf.length() - 1));
                break;
        }

        switch (parseMode) {
            case 0:
                System.err.println("[ Error ] < Parser > Unidentified Section: " + conf);
                return 0;
            case 1:
                System.out.println("[  Log  ] < Parser >: Parsing Path Section");
                handlePathConfiguration(conf);
                break;
            case 2:
                System.out.println("[  Log  ] < Parser >: Parsing Server Section");
                handleServerConfiguration(conf);
                break;
            case 3:
                System.out.println("[  Log  ] < Parser >: Parsing Logging Section");
                break;
            case 4:
                System.out.println("[  Log  ] < Parser >: Parsing Security Section");
                break;
            case 5:
                System.out.println("[  Log  ] < Parser >: Parsing Performance Section");
                break;
        }

        return 1;
    }

    public static int parseSection(String section) {
        return sectionMap.getOrDefault(section, 0);  // Defaults to 0 for unknown sections
    }

    public static int handleServerConfiguration(String conf) {
        String[] parts = conf.split("=", 2);
        if (parts.length == 2) {
            String key = parts[0].trim();
            String value = parts[1].trim();
            switch (key) {
                case "port":
                    try {
                        int port = Integer.parseInt(value);
                        if (port < 0 || port > 65535) {
                            System.err.println("[ Error ] Port Number must be in the range of [0, 65535]");
                            return 0;
                        }
                        Server.port = port;
                        System.out.println("[  Log  ] Port Number Changed to " + port);
                    } catch (NumberFormatException e) {
                        System.err.println("[ Error ] Invalid Port Number Format");
                        return 0;
                    }
                    break;
                case "host":
                    if (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
                        Server.host = value.substring(1, value.length() - 1).trim();
                        System.out.println("[  Log  ] Host changed to: " + Server.host);
                    } else if ("anon".equals(value)) {
                        Server.host = null;
                        System.out.println("[  Log  ] Host info erased");
                    }
                    break;
            }
        }
        return 1;
    }

    public static int handlePathConfiguration(String conf) {
        String[] parts = conf.split("=", 2);
        if (parts.length == 2) {
            String key = parts[0].trim();
            String value = parts[1].trim();
            switch (key) {
                case "root":
                	if (value.charAt(value.length() - 1) != '/'){
                		value += '/';
                	}
                    Server.rootPath = value;
                    System.out.println("[  Log  ] Root set to " + Server.rootPath);
                    break;
                case "index":
                    Server.indexPath = value;
                    System.out.println("[  Log  ] Index set to "+ Server.indexPath);
                    break;
            }
        }
        File indexFile = new File(Server.rootPath + Server.indexPath);
        if (indexFile.exists() && indexFile.isFile()) {
        	Server.indexPath = Server.rootPath + Server.indexPath;
        	System.out.println("[  Log  ] Index Page File at "+Server.indexPath);
        } else {
        	System.err.println("[ Error ] Index page doesn't exists");
        }
        return 1;
    }
}
