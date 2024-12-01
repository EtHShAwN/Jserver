package com.maven.test.restful;

import java.net.Socket;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class requestHandler {

    public static void handleGET(String path, Socket client) {
        System.out.println("[  Log  ] Client sent a GET Request");
        System.out.println("[  Log  ] Client Request Resource @ " + path);

        String response = null;
        HashMap<String, byte[]> map = configLoader.readConfig();

        byte[] content = map.get(path);
        if (content != null) {
            response = new String(content);
        } else if (!path.equals("/test")) {
            System.out.println("[  Log  ] Redirect to config/404.html");
            try {
                BufferedReader reader = new BufferedReader(new FileReader("config/404.html"));
                StringBuilder fileContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line).append("\n");
                }
                response = "HTTP/1.1 200 OK\r\n" +
                        "Server: Jserver/1.0-DEV on " +
                        System.getProperty("os.name") + "\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length:" + fileContent.length() +
                        "\r\n\r\n" + fileContent.toString();
                reader.close();
            } catch (IOException e) {
                System.err.println("[ Error ] Unable to read file: 404.html");
            }
        }

        if (path.equals("/test")) {
            response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: 57\r\n" +
                    "\r\n" +
                    "<!doctype html>\r\n<html><body><p>" +
                    "Welcome</p></body></html>";
        }

        sendResponse(response, client);
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
