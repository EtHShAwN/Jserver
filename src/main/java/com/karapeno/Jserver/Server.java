package com.karapeno.Jserver;

import com.karapeno.Jserver.configLoader.*;
import com.karapeno.Jserver.request.*;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;


public class Server {
	
	public static int port = 80;
	public static boolean signal = true;
	public static String rootPath = null;
	public static String indexPath = null;
	public static int connectionCount = 0;
	public static String host = "JServer/DEV-1.0.0";
	public static final String version = "1.0.0-DEV";
	public static String configPath = "config/conf.ini";
	public static String POSTProxy = null;

    public static ServerSocket createServerSocket(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("[  Log  ] Server started at Port: " + port);
            return serverSocket;
        } catch (IOException IOE) {
            System.err.println("[ Error ] " + IOE);
        }
        return null;
    }

    public static Socket getClientConnection(ServerSocket server) {
        try {
            Socket client = server.accept();
            client.setSoTimeout(30000);
            System.out.println("[  Log  ] Client connected: " + client.getInetAddress());
            return client;
        } catch (IOException IOE) {
            System.err.println("[ Error ] " + IOE);
        }
        return null;
    }

    public static String getClientData(Socket client) {
        try (InputStream clientInput = client.getInputStream()) {
            // Read data from client
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = clientInput.read(buffer)) != -1) {
                String data = new String(buffer, 0, bytesRead);
                System.out.println("[  Log  ] Data received from client");
                // parse request type here otherwise once return the socket close
                getRequestType(data,client);
                return data;
            }
        } catch (IOException IOE) {
            System.err.println("[ Error ] Failed to read client data");
            signal = false;
        }
        return null;
    }

    public static void closeConnection(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[  Log  ] Client connection closed.");
            }
        } catch (IOException IOE) {
            System.err.println("[ Error ] Failed to close client socket");
            signal = false;
        }
    }

    public static void closeServer(ServerSocket server) {
        try {
            if (server != null && !server.isClosed()) {
                server.close();
                System.out.println("[  Log  ] Server socket closed.");
            }
        } catch (IOException IOE) {
            System.err.println("[ Error ] Failed to close server socket");
        }
    }
    
    public static String getRequestType(String data,Socket client) {
    	String[] tokens  = data.split(" ");
    	String method = tokens[0];
    	if (method.equals("GET")) {
        	requestHandler.handleGET(tokens[1],client);
            return "GET";
        } else if (method.equals("POST")) {
        	requestHandler.handlePOST(data,client);
            return "POST";
        } else if (method.equals("PUT")) {
            return "PUT";
        } else if (method.equals("DELETE")) {
            return "DELETE";
        } else {
            return "INVALID";
        }
    }

    public static void main(String[] args) {
    	System.out.println("Jserver Version "+version);
    	System.out.println("[  Log  ] Jserver Start");
    	configLoader.readConfig();
    	
        ServerSocket server = createServerSocket(port);

        if (server != null) {
        	while(signal) {
        		Socket client = getClientConnection(server);
        		clientHandler handler = new clientHandler(client);
        		handler.start();
        	}
        	closeServer(server);
        } else {
            System.err.println("[ Error ] Server could not be started");
        }
    }
}

class clientHandler extends Thread{
	// create a client Thread pool
	
	private Socket client;
	
	public clientHandler(Socket client){
		this.client =  client;
	}
	
	public void run() {
		Server.connectionCount++;
		System.out.println("[  Log  ] Connected client count:"+Server.connectionCount);
        Server.getClientData(client);
        Server.closeConnection(client);
	    Server.connectionCount--;
	}
}