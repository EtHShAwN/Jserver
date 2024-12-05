package com.karapeno.Jserver.request.restful;

import java.net.Socket;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.karapeno.Jserver.Server;
import com.karapeno.Jserver.request.requestHandler;

/**
 * Focus on processing POST Request and saving data to db.txt
 * @author karapeno
 */
public class handlePOST {
    public static void handle(String data, Socket client) {
        String key = null;
        String val = null;
        String path = data.split(" ")[1];
        System.out.println("[  Log  ] Client sent a POST Request");
        System.out.println("[  Log  ] Client Post Data from " + path);

        String[] requestCache = data.split("\n");
        String contentType = null;
        String content = requestCache[requestCache.length - 1];
        
        // Iterate over the headers
        for (int i = 1; i < requestCache.length - 2; i++) {
            if (requestCache[i].split(":", 2).length == 2) {
                key = requestCache[i].split(":", 2)[0].trim();
                val = requestCache[i].split(":", 2)[1].trim();

                if (key.toLowerCase().equals("content-type")) {
                    contentType = val;

                    // Handle based on content type
                    switch (contentType) {
                        case "application/x-www-form-urlencoded":
                            StringBuilder formData = new StringBuilder();
                            String[] params = content.split("&");
                            for (String param : params) {
                                String[] keyValue = param.split("=");
                                if (keyValue.length == 2) {
                                    formData.append(keyValue[0]).append("=").append(keyValue[1]).append("\n");
                                } else {
                                    System.err.println("[ Error ] Invalid parameter in form data");
                                }
                            }
                            saveToDatabase(formData.toString(),path);
                            break;

                        case "application/json":
                            System.out.println("[  Log  ]Received JSON data: " + content);
                            saveToDatabase(content,path);
                            break;


                        default:
                            System.err.println("[ Error ] Unsupported content type: " + contentType);
                            return;
                    }
                }
            } else {
                System.err.println("[ Error ] Invalid header: " + requestCache[i]);
                return;
            }
        }

        // Responding to the client
        String response = "HTTP/1.1 200 OK\r\n";
        response += "Content-Type: text/plain\r\n";
        response += "Connection: close\r\n";
        response += "\r\n";
        response += "Request processed and data saved to proxy file";

        requestHandler.sendResponse(response, client);
    }


    private static void saveToDatabase(String data,String path) {
    	System.out.println(Server.POSTProxy);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Server.POSTProxy, true))) {
        	writer.write("["+path+"]\n");
            writer.write(data);
            writer.write("\n");
            System.out.println("[  Log  ] Data transfered to proxy");
        } catch (IOException e) {
            System.err.println("[ Error ] Failed to transfer data to proxy");
        }
    }
}
