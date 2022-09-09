module view.filetransferprotocolprogram {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires lombok;
    requires org.slf4j;
    requires java.desktop;

    opens view.filetransferprotocolprogram to javafx.fxml;
    exports view.filetransferprotocolprogram;
}