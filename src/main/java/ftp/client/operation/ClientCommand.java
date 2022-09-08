package ftp.client.operation;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class ClientCommand {
    Scanner sc = new Scanner(System.in);
    int portData;
    String clientIPAddress;
    String rootDownload;
    Socket clientSocketCommand;
    String[] listFile;
    String fileName;
    DataOutputStream dosClient;
    DataInputStream disClient;


    public ClientCommand(Socket clientSocketCommand, int portData, String clientIPAddress, String rootDownload) {
        this.clientSocketCommand = clientSocketCommand;
        this.portData = portData;
        this.clientIPAddress = clientIPAddress;
        this.rootDownload = rootDownload;
    }

    /**
     * Stage 3: Send command to the server
     */
    public String sendCommand(String command) {
        String result = "run";
        String receiveFromServer;
        command = command.toLowerCase().trim();
        try {
            dosClient = new DataOutputStream(clientSocketCommand.getOutputStream());
            disClient = new DataInputStream(clientSocketCommand.getInputStream());
            switch (command) {
                //Download stage
                case "1" -> {
                    System.out.println("----------------------------List files on the server---------------------------");

                    dosClient.writeUTF("1");
                    receiveFromServer = disClient.readUTF();
                    listFile = receiveFromServer.split(" ");
                    int i = 1;
                    for (String file : listFile) {
                        System.out.println(i + ". " + file);
                        i++;
                    }
                    System.out.println("-------------------------------------------------------------------------------");
                    ClientDownload clientDownload = new ClientDownload(portData, clientIPAddress, rootDownload, listFile);
                    clientDownload.proceed();
                }
                //Chat stage
                case "2" -> System.out.println("This function is in developing progress.");
                //Exit stage
                case "3" -> {
                    result = "exit";
                    dosClient.writeUTF("3");
                    receiveFromServer = disClient.readUTF();
                    System.out.println(receiveFromServer);
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<sendCommand>");
        }
        return result;
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
