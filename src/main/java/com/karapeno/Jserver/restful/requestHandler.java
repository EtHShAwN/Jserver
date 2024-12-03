package com.karapeno.Jserver.restful;

import java.net.Socket;

import com.karapeno.Jserver.Server;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class requestHandler {

	public static void handleGET(String path, Socket client) {
	    System.out.println("[  Log  ] Client sent a GET Request");
	    System.out.println("[  Log  ] Client Request Resource @ " + path);

	    String response = null;

	    if (path.equals("/")) {
	        path = "/index";
	    }
	    
	    path = path.substring(1);

	    
	    if (!path.contains(".")) {
	        path = path + ".html";
	    }

	    if (Server.rootPath != null) {
	        path = Server.rootPath + path;
	    }

	    File res = new File(path);

	    if (!res.exists()) {
	        System.out.println("[  Log  ] Redirect to config/404.html");
	        response = handleNotFound();
	    } else if (res.isFile()) {
	        response = handleFileResponse(path, res);
	    } else {
	        response = handleDirectoryResponse(path);
	    }

	    sendResponse(response, client);
	}

	private static String handleFileResponse(String path, File file) {
	    StringBuilder fileContent = new StringBuilder();
	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            fileContent.append(line).append("\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }


	    String contentType = getContentType(path);
	    return "HTTP/1.1 200 OK\r\n" +
	           "Server: Jserver/1.0-DEV on " +
	           System.getProperty("os.name") + "\r\n" +
	           "Content-Type: " + contentType + "\r\n" +
	           "Content-Length:" + fileContent.length() + "\r\n\r\n" + fileContent.toString();
	}

	private static String handleNotFound() {
	    String response = null;
	    try {
	        BufferedReader reader = new BufferedReader(new FileReader("config/404.html"));
	        StringBuilder fileContent = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            fileContent.append(line).append("\n");
	        }
	        response = "HTTP/1.1 404 Not Found\r\n" +
	                   "Server: Jserver/1.0-DEV on " +
	                   System.getProperty("os.name") + "\r\n" +
	                   "Content-Type: text/html\r\n" +
	                   "Content-Length:" + fileContent.length() + "\r\n\r\n" + fileContent.toString();
	        reader.close();
	    } catch (IOException e) {
	        System.err.println("[ Error ] Unable to read file: 404.html");
	    }
	    return response;
	}

	private static String handleDirectoryResponse(String path) {
	    return "HTTP/1.1 403 Forbidden\r\n" +
	           "Server: Jserver/1.0-DEV on " +
	           System.getProperty("os.name") + "\r\n" +
	           "Content-Type: text/html\r\n" +
	           "Content-Length: 59\r\n\r\n" +
	           "<html><body><h1>403 Forbidden</h1></body></html>";
	}

	private static String getContentType(String path) {
	    // Return the correct MIME type based on the file extension
	    if (path.endsWith(".html") || path.endsWith(".htm")) {
	        return "text/html";
	    } else if (path.endsWith(".css")) {
	        return "text/css";
	    } else if (path.endsWith(".js")) {
	        return "application/javascript";
	    } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
	        return "image/jpeg";
	    } else if (path.endsWith(".png")) {
	        return "image/png";
	    } else if (path.endsWith(".gif")) {
	        return "image/gif";
	    } else if (path.endsWith(".svg")) {
	        return "image/svg+xml";
	    } else if (path.endsWith(".json")) {
	        return "application/json";
	    }
	    return "application/octet-stream";  // Default to binary stream
	}


    public static void handlePOST(String path, Socket client) {
        System.out.println("[  Log  ] Client sent a POST Request");
        System.out.println("[  Log  ] Client Post Data to " + path);
    }

    private static void sendResponse(String response, Socket client) {
        if (response == null) {
            System.err.println("[ Error ] Response is null, cannot send response to client");
            return;
        }
        try (OutputStream os = client.getOutputStream()) {
            os.write(response.getBytes());
            os.flush();
            System.out.println("[  Log  ] Response sent to client");
        } catch (IOException e) {
            System.err.println("[ Error ] Can't initiate write pipeline: " + e.getMessage());
        }
    }
}