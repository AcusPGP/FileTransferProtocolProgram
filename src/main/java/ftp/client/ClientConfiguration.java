package ftp.client;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

@Slf4j
public class ClientConfiguration {
    static Scanner sc = new Scanner(System.in);
    private static String[] configValues;

    public ClientConfiguration(String[] configValues) {
        ClientConfiguration.configValues = configValues;
        System.out.println("Client: " + Arrays.toString(configValues));
    }

    /**
     * Running the program
     * Stage 1 : Get and check values
     */
    public static void main(String[] args) {
        getValuesFromConfig();
        checkBeforeClientConnection();
        String inputCommand = sc.nextLine().trim();
        if (inputCommand.equals("")) {
            //Checking stage
            if (!checkConfigurationValues(configValues) || !checkFolderExist(configValues[3]) || !checkPort(configValues)) {
                log.info("Please check the client's configuration in the config.properties file again.");
                System.exit(0);
            }
            //Connect client to the server
            Client clientConnection = new Client(configValues);
            clientConnection.commandToServer();
        } else {
            main(args);
        }
    }

    /**
     * Getting values from the config.properties file for checking stages
     */
    private static void getValuesFromConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            String portCommand = properties.getProperty("portCommand").trim(); // "9000"
            String portData = properties.getProperty("portData").trim(); // "9001"
            String clientIPAddress = properties.getProperty("clientIPAddress").trim(); // 127.0.0.1
            File downloadFile = new File(properties.getProperty("rootDownload").trim());
            String rootDownload = downloadFile.getAbsolutePath(); // /Users/macbook/Desktop/Acus/Projects/FileTransferProtocolProgram/download
            String[] configValues = {portCommand, portData, clientIPAddress, rootDownload};
            ClientConfiguration clientConfiguration = new ClientConfiguration(configValues);
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<getValuesFromConfig>");
        }
    }


    /**
     * Checking stages
     */
    private static void checkBeforeClientConnection() {
        System.out.println("------------------------Check the client's configuration-----------------------");
        System.out.println("Make sure the argument values in the configuration is correct and precise.");
        System.out.println("First value - port command (port for sending command to the server): " + configValues[0]);
        System.out.println("Second value - port data (port for receiving data from the server) : " + configValues[1]);
        System.out.println("Third value - client's IP address: 'localhost' or your IP address: " + configValues[2]);
        System.out.println("Fourth value - direction for 'download' file: " + configValues[3]);
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("If the config.properties is correct, please press 'enter' to connect to the server.");
    }

    private static boolean checkConfigurationValues(String[] args) {
        if (args.length != 4) {
            log.info("The values are invalid. Please check the values in the configuration again.");
            return false;
        }
        return true;
    }

    private static boolean checkFolderExist(String rootDownload) {
        File file = new File(rootDownload);
        if (!file.exists()) {
            log.info("The 'download' file is not existed. PLease create it in the folder 'client' and check the configuration again.");
            return false;
        }
        return true;
    }

    private static boolean checkPort(String[] args) {
        try {
            Integer.parseInt(args[0]);
            Integer.parseInt(args[1]);
            return true;
        } catch (NumberFormatException e) {
            log.info(e.getMessage());
            log.info("<checkPort>");
        }
        return false;
    }
}
