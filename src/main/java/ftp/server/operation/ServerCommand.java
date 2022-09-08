package ftp.server.operation;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class ServerCommand implements Runnable {
    int portData;
    String serverIPAddress;
    String rootFile;
    Socket serverSocket;
    DataOutputStream dosServer;
    DataInputStream disServer;
    String[] list;

    public ServerCommand(int portData, String serverIPAddress, String rootFile, Socket socket) {
        this.portData = portData;
        this.serverIPAddress = serverIPAddress;
        this.rootFile = rootFile;
        this.serverSocket = socket;
    }

    /**
     * Stage 3: Receive the client's command to operate the server's services
     */
    @Override
    public void run() {
        String result = "run";
        try {
            disServer = new DataInputStream(serverSocket.getInputStream());
            dosServer = new DataOutputStream(serverSocket.getOutputStream());
            while (true) {
                String commandFromClient = disServer.readUTF();
                log.info("Local port: " + serverSocket.getLocalPort() + " Port: " + serverSocket.getPort() + " & Host address: " + serverSocket.getInetAddress().getHostAddress() + " & message from client: " + commandFromClient);
                switch (commandFromClient) {
                    case "1" -> {
                        File file = new File(rootFile);
                        list = file.list();
                        StringBuilder listFiles = new StringBuilder();
                        assert list != null;
                        for (String temp : list) {
                            listFiles.append(temp).append(" ");
                        }
                        dosServer.writeUTF(listFiles.toString());
                        ServerDownload serverDownload = new ServerDownload(portData, rootFile);
                        serverDownload.run();
                    }
                    case "3" -> {
                        result = "exit";
                        dosServer.writeUTF("Bye " + serverSocket.getInetAddress().getHostAddress());
                    }
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<catch - run>");
        } finally {
            try {
                disServer.close();
                dosServer.close();
                if (result.equals("exit")) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                log.info("<finally - run>");
            }
        }
    }
}
