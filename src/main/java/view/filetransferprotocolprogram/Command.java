package view.filetransferprotocolprogram;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javafx.event.ActionEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Slf4j
public class Command implements Initializable {
    private Socket clientSocketCommand;
    private Socket clientSocketDownload;
    private String[] configValues;
    @FXML
    Pane downloadPane;
    @FXML
    Button disconnectButton;
    @FXML
    TableView<ListFile> listFileTableView;
    @FXML
    TableColumn<ListFile, Integer> numberColumn;
    @FXML
    TableColumn<ListFile, String> fileNameColumn;
    @FXML
    TextField nameOfFile;
    @FXML
    ProgressBar downloadBar;
    String receiveFromServer;
    String[] listFile;
    String fileName;
    DataOutputStream dosClient;
    DataInputStream disClient;
    ObservableList<ListFile> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectToServer();
        downloadPane.setVisible(false);
    }

    public void connectToServer() {
        readFileConfig();
        try {
            if (clientSocketCommand == null) {
                clientSocketCommand = new Socket(configValues[2], Integer.parseInt(configValues[0]));
            }
            System.out.println("Connected: " + clientSocketCommand);
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

    // Download Button
    public void onDownloadButtonClick(ActionEvent event) {
        disconnectButton.setVisible(false);
        try {
            dosClient = new DataOutputStream(clientSocketCommand.getOutputStream());
            disClient = new DataInputStream(clientSocketCommand.getInputStream());
            //"download" == "1"
            dosClient.writeUTF("1");
            receiveFromServer = disClient.readUTF();
            System.out.println(receiveFromServer);
            listFile = receiveFromServer.split(" ");
            downloadPane.setVisible(true);
            showListFile();
            clientDownload();
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<onDownloadButtonClick>");
        }
    }

    public void clientDownload() {
        try {
            clientSocketDownload = new Socket(configValues[2], Integer.parseInt(configValues[1]));
        } catch (IOException e) {
            log.info(e.getMessage());
            log.info("<clientDonwload>");
        }
    }

    public void onStartDownloadingButtonClick() {
        fileName = nameOfFile.getText().trim();
        System.out.println(fileName);
        if (fileName.equals(" ")) {
            Alert emptyFileName = new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK);
            emptyFileName.setTitle("Error Stage");
            emptyFileName.setContentText("The file name is empty!");
            emptyFileName.initModality(Modality.APPLICATION_MODAL);
            emptyFileName.showAndWait();
            if (emptyFileName.getResult() == ButtonType.OK) {
                emptyFileName.close();
            }
        } else {
            int check = checkFileExist(fileName);
            if (check == 1) {
                try {
                    dosClient = new DataOutputStream(clientSocketDownload.getOutputStream());
                    dosClient.writeUTF(fileName);
                    System.out.println("Downloading.....");
                    disClient = new DataInputStream(clientSocketDownload.getInputStream());
                    FileOutputStream fos = new FileOutputStream(configValues[3] + "/" + fileName);
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = disClient.read(buffer)) >= 0) {
                        fos.write(buffer, 0, count);
                    }
                    progressBar();
                    System.out.println("Downloading successfully.");
                    dosClient.close();
                    dosClient.close();
                    clientSocketDownload.close();
                } catch (IOException e) {
                    log.info(e.getMessage());
                    log.info("<onStartDownloadClickButton>");
                } finally {
                    afterDownloadStage();
                }
            } else {
                Alert fileIsNotExist = new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK);
                fileIsNotExist.setTitle("Error Stage");
                fileIsNotExist.setContentText("This file is not exist!");
                fileIsNotExist.initModality(Modality.APPLICATION_MODAL);
                fileIsNotExist.showAndWait();
                if (fileIsNotExist.getResult() == ButtonType.OK) {
                    fileIsNotExist.close();
                }

            }
        }
    }

    public void choosenRow() {
        listFileTableView.setOnMouseClicked(mouseEvent -> {
            String temp = String.valueOf(listFileTableView.getSelectionModel().getSelectedItem());
            System.out.println(temp);
            nameOfFile.setText(temp.trim());
        });

    }

    public void afterDownloadStage() {
        nameOfFile.clear();
        listFileTableView.getColumns().clear();
        downloadPane.setVisible(false);
        Alert downloadSuccessfull = new Alert(Alert.AlertType.CONFIRMATION, "Download", ButtonType.OK);
        downloadSuccessfull.setTitle("Download Stage");
        downloadSuccessfull.setContentText("The file is downloaded successfully!");
        downloadSuccessfull.initModality(Modality.APPLICATION_MODAL);
        downloadSuccessfull.showAndWait();
        if (downloadSuccessfull.getResult() == ButtonType.OK) {
            downloadSuccessfull.close();
            disconnectButton.setVisible(true);
        }
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

    public void showListFile() {
        for (int i = 0; i < listFile.length; i++) {
            dataList.add(new ListFile(i + 1, listFile[i]));
            numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
            fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
            listFileTableView.setItems(dataList);
        }
    }

    public void progressBar() {
        double count = 0;
        while (count <= 100.0) {
            downloadBar.setProgress(count);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info(e.getMessage());
            }
            count += 1;
        }
    }

    // Disconnect Button
    public void onDisconnectButtonClick(ActionEvent event) {
        Alert changeToConfigurationView = new Alert(Alert.AlertType.CONFIRMATION, "Change View Stage", ButtonType.OK, ButtonType.CANCEL);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        changeToConfigurationView.setTitle("Change View Stage");
        changeToConfigurationView.setContentText("Do you want to disconnect to the Server?");
        changeToConfigurationView.initModality(Modality.APPLICATION_MODAL);
        changeToConfigurationView.initOwner(stage);
        changeToConfigurationView.showAndWait();

        if (changeToConfigurationView.getResult() == ButtonType.OK) {
            try {
                dosClient = new DataOutputStream(clientSocketCommand.getOutputStream());
                disClient = new DataInputStream(clientSocketCommand.getInputStream());
                //"exit" == "3"
                dosClient.writeUTF("3");
                receiveFromServer = disClient.readUTF();
                System.out.println(receiveFromServer);
            } catch (IOException e) {
                log.info(e.getMessage());
                log.info("<catch - onDisconnectButtonClick>");
            } finally {
                try {
                    if (clientSocketCommand != null) {
                        clientSocketCommand.close();
                    }
                    configurationStage(event);
                } catch (IOException e) {
                    log.info(e.getMessage());
                    log.info("<finally - onDisconnectButtonClick>");
                }
            }
        }
    }

    //Change scene: Configuration Stage
    public void configurationStage(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Command.class.getResource("configuration-view.fxml"));
        Parent addParent = loader.load();
        Scene addScene = new Scene(addParent);
        stage.setScene(addScene);
    }
}
