package ftp.server.operation;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class ServerDownload implements Runnable {
    int portData;
    String fileName;
    String rootFile;
    DataInputStream disServer;
    DataOutputStream dosServer;

    public ServerDownload(int portData, String rootFile) {
        this.portData = portData;
        this.rootFile = rootFile;
    }

    /**
     * Stage 4: Send file to client's storage
     */
    @Override
    public void run() {
        try (ServerSocket download = new ServerSocket(portData)) {
            log.info("Binding to port " + portData + ", please wait.....");
            log.info("Command Server started: " + download);
            log.info("Waiting for a client .....");
            Socket downloadSocket = download.accept();
            log.info("Client accepted: " + downloadSocket);
            disServer = new DataInputStream(downloadSocket.getInputStream());
            fileName = disServer.readUTF();
            System.out.println("The file name is " + fileName);
            dosServer = new DataOutputStream(downloadSocket.getOutputStream());
            String filePath = rootFile + "/" + fileName;
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) >= 0) {
                dosServer.write(buffer, 0, bytesRead);
            }
            disServer.close();
            dosServer.close();
            download.close();
            downloadSocket.close();
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }


}
