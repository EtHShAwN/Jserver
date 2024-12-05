package com.karapeno.Jserver.request;

import java.net.Socket;

import com.karapeno.Jserver.request.restful.*;

import java.io.OutputStream;
import java.io.IOException;

public class requestHandler {

	public static void handleGET(String path, Socket client) {
		handleGET.handle(path, client);
    }
	
    public static void handlePOST(String data, Socket client) {
    	handlePOST.handle(data, client);
    }

    public static void sendResponse(String response, Socket client) {
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