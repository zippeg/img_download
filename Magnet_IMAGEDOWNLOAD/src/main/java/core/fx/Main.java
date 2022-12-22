package core.fx;

import com.sun.javafx.stage.EmbeddedWindow;
import core.Image.HttpCore;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.swing.text.AsyncBoxView;
import java.io.File;
import java.util.*;

public class Main extends Application {
    private Thread downloadThread;
    private List<Scene> scenes;
    private Scene mainScene;
    private AnchorPane pane;
    private static HttpCore httpCore;
    private  Stage stage;
    public static void main(String[] args) {
        httpCore=new HttpCore();
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Properties properties = System.getProperties();
        String imgPath = (String) properties.get("user.dir");
        System.out.println(imgPath);
        this.stage=stage;
        stage.getIcons().add(new Image("icon.png"));
        stage.setResizable(false);
        stage.setTitle("图片下载器");
        scenes=new ArrayList<Scene>();
        pane = new AnchorPane();
        mainScene = new Scene(pane,500,330);
        setInit();
        scenes.add(mainScene);
        stage.setScene(mainScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (httpCore!=null) {
            httpCore.stop();
        }
        downloadThread.stop();
        super.stop();
    }

    private void setInit() {
        Button button = new Button("下载");
        TextField textField = new TextField("");
        Button selectFile = new Button("选择存储目录");
        textField.setLayoutX(100);
        textField.setLayoutY(50);
        button.setLayoutX(270);
        button.setLayoutY(50);
        selectFile.setLayoutX(320);
        selectFile.setLayoutY(50);
        selectFile.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(stage);
            if (file!=null)httpCore.setPath(file.getPath()+"\\");
//            httpCore.setPath(file.getPath()+"\\");
//            httpCore.setPath();
        });


        pane.getChildren().addAll(button,textField,selectFile);
        button.setOnAction(e->{
            String url = textField.getText();
            downloadThread=new Thread(()->httpCore.downloadModelAllImage(url));
           if (checkUrl(url))downloadThread.start();
            textField.setText("");
        });

    }
    //http://meirentu.cc/model/%E6%96%87%E8%8A%AE.html
    private boolean checkUrl(String url) {
        boolean flag=false;
        if (url.contains(".html")&&url.contains("http://meirentu.cc/model/")) flag=true;
        return flag;
    }
}
