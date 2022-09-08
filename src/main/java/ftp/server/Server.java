package ftp.server;

import ftp.server.operation.ServerCommand;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server {
    int portCommand;
    int portData;
    String serverIPAddress;
    String rootFile;

    private static final int NUM_OF_THREAD = 5;

    public Server(String[] configValues) {
        this.portCommand = Integer.parseInt(configValues[0]);
        this.portData = Integer.parseInt(configValues[1]);
        this.serverIPAddress = configValues[2];
        this.rootFile = configValues[3];
    }

    /**
     * Stage 2: Accept the client's connection
     */
    public void startCommandServer() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_THREAD);
        ServerSocket serverSocket = null;
        try {
            log.info("Binding to port " + portCommand + ", please wait.....");
            serverSocket = new ServerSocket(portCommand);
            log.info("Command Server started: " + serverSocket);
            log.info("Waiting for a client .....");
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    log.info("Client accepted: " + socket);
                    //Stage 3: Waiting for client's command
                    ServerCommand serverCommandOperation = new ServerCommand(portData, serverIPAddress, rootFile, socket);
                    executorService.execute(serverCommandOperation);
                } catch (IOException e) {
                    log.info("Can't connect to the client");
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<startCommandServer>");
        } finally {
            executorService.shutdown();
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}
