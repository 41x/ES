package sample;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class DialogBoxController {
//    public TextArea TextAtea;
    public javafx.scene.control.TextArea TArea;
    private boolean Res=false;

    public void Close(ActionEvent actionEvent) {
        Main.getStage().close();
    }


    public void onCancel(ActionEvent actionEvent) {
        close();
    }

    public void onOk(ActionEvent actionEvent) {
        Res=true;
        close();
    }

    private void close(){
        ((Stage)getTArea().getScene().getWindow()).close();
    }

    public TextArea getTArea() {
        return TArea;
    }

    public boolean getRes() {
        return Res;
    }
}
