package ftp.client.operation;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class ClientDownload {
    Scanner sc = new Scanner(System.in);
    int portData;
    String clientIPAddress;
    String fileName;
    String rootDownload;
    String[] listFile;
    private Socket clientSocketDownload = null;
    DataOutputStream dosClient;
    DataInputStream disClient;

    public ClientDownload(int portData, String clientIPAddress, String rootDownload, String[] listFile) {
        this.portData = portData;
        this.clientIPAddress = clientIPAddress;
        this.rootDownload = rootDownload;
        this.listFile = listFile;
    }

    /**
     * Stage 4: Receive and write file from server to client's storage
     */
    public void proceed() {
        try {
            clientSocketDownload = new Socket(clientIPAddress, portData);
            dosClient = new DataOutputStream(clientSocketDownload.getOutputStream());
            fileName = enterFileName();
            dosClient.writeUTF(fileName);
            System.out.println("Downloading.....");
            disClient = new DataInputStream(clientSocketDownload.getInputStream());
            FileOutputStream fos = new FileOutputStream(rootDownload + "/" + fileName);
            byte[] buffer = new byte[4096];
            int count;
            while ((count = disClient.read(buffer)) >= 0) {
                fos.write(buffer, 0, count);
            }
            System.out.println("Downloading successfully.");
            dosClient.close();
            dosClient.close();
            clientSocketDownload.close();
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<proceed>");
        }
    }

    public String enterFileName() {
        System.out.print("Enter the full name of the file to download: ");
        fileName = sc.nextLine().trim();
        if (fileName.equals("")) {
            System.out.println("Please enter again!");
            return enterFileName();
        } else {
            int check = checkFileExist(fileName);
            if (check == 0) {
                System.out.println("This file is not existed. Please enter again!");
                return enterFileName();
            }
        }
        return fileName;
    }

    public int checkFileExist(String fileName) {
        int check = 0;
        for (String file : listFile) {
            if (fileName.equals(file)) {
                check = 1;
                return check;
            }
        }
        return check;
    }
}
