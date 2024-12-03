package com.karapeno.Jserver.configLoader;

import com.karapeno.Jserver.Server;

/**
 * this module is used to parse configuration
 * in INI format and setup parameter in server
 * module
 * 
 * @author karapeno
 */

public class configParser {

    private static int parseMode = 0;

    public static int parse(String conf) {

        switch (conf.charAt(0)) {
            case '#':
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
                // handle "path" section
                break;
            case 2:
                // handle "server" section
            	System.out.println("[  Log  ] < Parser >: parsing Server Section");
            	handleServerConfiguration(conf);
                break;
            case 3:
                // handle "logging" section
                break;
            case 4:
                // handle "security" section
                break;
            case 5:
                // handle "performance" section
                break;
            default:
                System.err.println("[ Error ] < Parser > Invalid parse mode");
                return 0;
        }

        return 0;
    }

    public static int parseSection(String section) {
        switch (section) {
            case "path":
                return 1;
            case "server":
                return 2;
            case "logging":
                return 3;
            case "security":
                return 4;
            case "performance":
                return 5;
            default:
                return 0; // Unknown section
        }
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
	        		    if (port > 65535 || port < 0) {
	        		    	System.err.println(
	        		    	"[ Error ] Port Number must in the range of [0,65535]");
	        		    	return 0;
	        		    }
	        		    Server.port = port;
	        		    System.out.println(
	        		    "[  Log  ] Port Number Changed to "+
	        		    port
	        		    );
	        		} catch (NumberFormatException e) {
	        		    System.err.println(
	        		    "[ Error ] Wrong Port Number Format"
	        		    );
	        		}
	        		break;
	            }
        }
        return 1;
    }

}
