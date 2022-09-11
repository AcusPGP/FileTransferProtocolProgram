package view.filetransferprotocolprogram;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Slf4j
public class Configuration implements Initializable {
    @FXML
    private Label portCommand;
    @FXML
    private Label portData;
    @FXML
    private Label clientIPAddress;
    @FXML
    private Label connectStatus;
    private String[] configValues;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showConfigValues();
    }

    public void showConfigValues() {
        readFileConfig();
        portCommand.setText(configValues[0]);
        portData.setText(configValues[1]);
        clientIPAddress.setText(configValues[2]);
        checkFolderExist(configValues[3]);
    }

    /**
     * Functions:
     */
    //Exit Button
    public void onExitButtonClick(ActionEvent event) {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION, "Exit Confirmation", ButtonType.OK, ButtonType.CANCEL);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        exitAlert.setTitle("Exit Stage");
        exitAlert.setContentText("Are you sure you want to exit?");
        exitAlert.initModality(Modality.APPLICATION_MODAL);
        exitAlert.initOwner(stage);
        exitAlert.showAndWait();

        if (exitAlert.getResult() == ButtonType.OK) {
            Platform.exit();
        } else {
            exitAlert.close();
        }
    }

    //Refresh Button
    //1. Get values from the client's configuration
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

    //2. Check if the configuration is already up-to-date or not
    public boolean alreadyUpToDate() {
        String oldPortCommand = configValues[0];
        String oldPortData = configValues[1];
        String oldClientIPAddress = configValues[2];
        String oldRootDownload = configValues[3];
        readFileConfig();
        return oldPortCommand.equals(configValues[0]) && oldPortData.equals(configValues[1]) && oldClientIPAddress.equals(configValues[2]) && oldRootDownload.equals(configValues[3]);
    }

    //3. User click on the "Refresh" button
    public void onRefreshButtonClick(ActionEvent event) {
        if (alreadyUpToDate()) {
            Alert upToDateAlert = new Alert(Alert.AlertType.WARNING, " Similar Configuration", ButtonType.OK);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            upToDateAlert.setTitle("Latest Configuration Alert");
            upToDateAlert.setContentText("The configuration is already similar. Check again and press connect button to move on.");
            upToDateAlert.initModality(Modality.APPLICATION_MODAL);
            upToDateAlert.initOwner(stage);
            upToDateAlert.showAndWait();

            if (upToDateAlert.getResult() == ButtonType.OK) {
                upToDateAlert.close();
            }
        } else {
            showConfigValues();
        }
    }

    //Comment Button
    public void onCommentButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Configuration.class.getResource("comment-view.fxml"));
        Parent addParent = loader.load();
        Scene addScene = new Scene(addParent);
        stage.setScene(addScene);
    }

    //'config.properties' link
    public void onLinkConfigClick() {
        try {
            File file = new File("src/main/resources/config.properties");
            Desktop.getDesktop().open(file.getAbsoluteFile());
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<onLinkConfigClick>");
        }
    }

    //'download' folder
    public void onDownloadFolderClick() {
        try {
            File file = new File(configValues[3]);
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<onDownloadFolderClick>");
        }
    }

    //'More info' Button
    public void onMoreInfoButtonClick() {
        try {
            URL url = new URL("https://github.com/AcusPGP/FileTransferProtocolProgram#readme");
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException | URISyntaxException e) {
            log.info(e.getMessage());
            log.info("<onMoreInfoButtonClick>");
        }
    }

    //Connect Button
    public void onConnectButtonClick(ActionEvent event) throws IOException {
        connectStatus.setText("Connecting.....");
        connectStatus.setTextFill(Color.YELLOW);
        Alert changeToConnectView = new Alert(Alert.AlertType.CONFIRMATION, "Change View Stage", ButtonType.OK, ButtonType.CANCEL);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        changeToConnectView.setTitle("Change View Stage");
        changeToConnectView.setContentText("Connect to Server successfully. Please press OK button to go to the next stage.");
        changeToConnectView.initModality(Modality.APPLICATION_MODAL);
        changeToConnectView.initOwner(stage);
        changeToConnectView.showAndWait();

        if (changeToConnectView.getResult() == ButtonType.OK) {
            commandStage(event);
        } else {
            connectStatus.setText("Disconnecting.....");
            connectStatus.setTextFill(Color.WHITE);
        }
    }

    //Change scene to command view
    public void commandStage(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Configuration.class.getResource("command-view.fxml"));
        Parent addParent = loader.load();
        Scene addScene = new Scene(addParent);
        stage.setScene(addScene);
    }

    //Check if the "download" folder is exist
    private static void checkFolderExist(String rootDownload) {
        File file = new File(rootDownload);
        if (file.mkdir()) {
            System.out.println("Folder created: " + file.getName());
        }
    }
}
