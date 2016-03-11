package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class Main extends Application {

    private static Shell shell;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setScene(new Scene(root));
//        primaryStage.setFullScreen(true);
        stage=primaryStage;

        BufferedReader br = null;
        String kbPath = null;

        File f= new File("lastKB");
        if (f.exists() && !f.isDirectory()){
            try {
                br = new BufferedReader(new FileReader(f.getPath()));
                kbPath = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        shell=new Shell(new MyLIM());
        boolean kbisset=false;
        if (kbPath!=null)
            kbisset=shell.setKnowledgeBase(kbPath);
        primaryStage.setTitle(shell.getKnowledgeBase().getName()+" "+ (kbisset?kbPath:""));

        primaryStage.show();
    }


    public static Shell getShell() {
        return shell;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String ask() {
//        todo
        return null;
    }

}
