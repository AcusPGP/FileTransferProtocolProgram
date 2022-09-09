package view.filetransferprotocolprogram;

import javafx.fxml.Initializable;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Slf4j
public class Command implements Initializable {
    private Socket clientSocketCommand;
    private String[] configValues;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectToServer();
    }

    public void connectToServer() {
        readFileConfig();
        try {
            if (clientSocketCommand == null) {
                clientSocketCommand = new Socket(configValues[2], Integer.parseInt(configValues[0]));
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<connectToServer>");
        }
    }
    public void readFileConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            String portCommand = properties.getProperty("portCommand");
            String portData = properties.getProperty("portData");
            String clientIPAddress = properties.getProperty("clientIPAddress");
            File downloadFile = new File(properties.getProperty("rootDownload").trim());
            String rootDownload = downloadFile.getAbsolutePath();
            configValues = new String[]{portCommand, portData, clientIPAddress, rootDownload};
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<readFileProperties>");
        }
    }
}
