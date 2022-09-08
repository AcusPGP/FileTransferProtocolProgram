package ftp.client;

import ftp.client.operation.ClientCommand;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class Client {
    Scanner sc = new Scanner(System.in);
    int portCommand;
    int portData;
    String clientIPAddress;
    String rootDownload;
    private Socket clientSocketCommand;

    public Client(String[] configValues) {
        this.portCommand = Integer.parseInt(configValues[0]);
        this.portData = Integer.parseInt(configValues[1]);
        this.clientIPAddress = configValues[2];
        this.rootDownload = configValues[3];
    }

    /**
     * Stage 2: Connect client to the server
     */
    public void commandToServer() {
        try {
            if (clientSocketCommand == null) {
                clientSocketCommand = new Socket(clientIPAddress, portCommand);
            }
            proceed();
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<commandToServer>");
        }
    }

    public void proceed() {
        try {
            if (clientSocketCommand == null) {
                clientSocketCommand = new Socket(clientIPAddress, portCommand);
            }
            System.out.println("Connected: " + clientSocketCommand);
            while (true) {
                clientMenu();
                //Send a command to the server
                String command = sc.nextLine().trim();
                if (command.equals("")) {
                    System.out.println("Please enter a command to server.");
                } else {
                    ClientCommand clientCommand = new ClientCommand(clientSocketCommand, portData, clientIPAddress, rootDownload);
                    String result = clientCommand.sendCommand(command);
                    if (result.equals("exit")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<catch - proceed>");
        } finally {
            if (clientSocketCommand != null) {
                try {
                    clientSocketCommand.close();
                } catch (IOException e) {
                    log.info(e.getMessage());
                    log.info("<finally - proceed>");
                }
            }
        }
    }

    /**
     * Utilities
     */
    private static void clientMenu() {
        System.out.println("--------------------------------Menu for command-------------------------------");
        System.out.println("1. Show and download file. ");
        System.out.println("2. Chat with server/client. ");
        System.out.println("3. Quit the program. ");
        System.out.println("-------------------------------------------------------------------------------");
        System.out.print("Input command: ");
    }
}
