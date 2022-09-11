package view.filetransferprotocolprogram;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Comment {

    @FXML
    private DatePicker picker;
    @FXML
    private TextArea notes;
    @FXML
    private Map<LocalDate, String> data = new HashMap<>();

    public void initialize() {
        load();
        picker.valueProperty().addListener((o, oldDate, date) -> {
            notes.setText(data.getOrDefault(date, ""));
        });
        picker.setValue(LocalDate.now());
    }

    public void updateNotes() {
        data.put(picker.getValue(), notes.getText());
    }

    private void save() {
        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(Paths.get("comment.data")))) {
            stream.writeObject(data);
            System.out.println("Saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Path file = Paths.get("comment.data");
        if (Files.exists(file)) {
            try (ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(file))) {
                data = (Map<LocalDate, String>) stream.readObject();
                System.out.println("Loaded!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onExitButtonCLick(ActionEvent event) {
        save();
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

    public void onReturnButtonClick(ActionEvent event) throws IOException {
        save();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(Configuration.class.getResource("configuration-view.fxml"));
        Parent addParent = loader.load();
        Scene addScene = new Scene(addParent);
        stage.setScene(addScene);
    }
}
