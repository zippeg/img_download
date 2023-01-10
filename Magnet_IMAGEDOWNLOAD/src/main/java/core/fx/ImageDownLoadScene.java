package core.fx;

import core.Image.HttpCore;
import core.timer.Timer;
import core.timer.TimerTask;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import lombok.Data;

import java.io.File;
@Data
public class ImageDownLoadScene  implements Timer {
    private Scene scene;
    private Application application;
    private Pane pane;
    private Label label;
    private Button download;
    private Button selectFile;
    private RadioButton one;
    private RadioButton two;
    private ToggleGroup toggleGroup;
    private TextField textField;
    private Button returnButton;
    private String title;
    private Image image;
    private HttpCore httpCore;
    private Thread downloadThread;
    private TimerTask timerTask;
    private Button jmpButton;
    public ImageDownLoadScene() {
        this.application=application;
        init();
        setLayout();
        bindEvent();
    }

    private void bindEvent() {

        selectFile.setOnAction((click)->{
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(scene.getWindow());
            if (file!=null)httpCore.setPath(file.getPath()+"\\");
        });
        download.setOnAction((d)->{
            String url = textField.getText();
            //下载全部图片
            if (two.isSelected()) {
                downloadThread=new Thread(()->{httpCore.downloadModelAllImage(url);downloadThread=null;});
                if (checkUrl(url))downloadThread.start();
            }
            else{
                if (one.isSelected()){//下载单套图片
                    downloadThread=new Thread(()->{httpCore.downloadAllImgForModel(url); downloadThread=null;});
                    if (checkUrlOnes(url)) downloadThread.start();
                }
            }
            textField.setText("");
        });

    }

    private void init(){
        this.pane=new AnchorPane();
        this.jmpButton=new Button("浏览器");
        this.scene=new Scene(pane,500,330);
        this.label = new Label("暂无下载任务");
        this.download = new Button("下载");
        this. selectFile = new Button("选择文件");
        this.one=new RadioButton("单套图片");
        this.two=new RadioButton("所有图片");
        this.toggleGroup=new ToggleGroup();
        this.one.setToggleGroup(toggleGroup);
        this.two.setToggleGroup(toggleGroup);
        this.textField=new TextField();
        this.returnButton=new Button("返回");
        this.image=new Image("icon.png");
        this.title="图片下载V1.2";
        this.httpCore=new HttpCore();
        this.timerTask=new TimerTask(this,1000L);

    }
    private void setLayout() {
        //textField
        textField.setMinWidth(250);
        textField.setLayoutX(50);
        textField.setLayoutY(50);
        //Button
        download.setLayoutX(330);
        download.setLayoutY(50);
        selectFile.setLayoutX(380);
        selectFile.setLayoutY(50);
        jmpButton.setLayoutX(200);
        jmpButton.setLayoutY(15);
        //RadioButton
        one.setSelected(true);
        one.setLayoutX(50);
        two.setLayoutX(130);
        one.setLayoutY(90);
        two.setLayoutY(90);
        //Label
        label.setLayoutY(20);
        label.setLayoutX(80);
        label.setTextFill(Color.rgb(48,239,15));

        pane.getChildren().addAll(one,two,jmpButton,textField,download,selectFile,label);
        //
    }
    //http://meirentu.cc/pic/734080233697.html
    private boolean checkUrlOnes(String url) {
        boolean flag=false;
        if (url.contains(".html")&&(url.contains("http://meirentu.cc/pic/")||url.contains("https://meirentu.cc/pic/"))) flag=true;
        return flag;
    }

    //http://meirentu.cc/model/%E6%96%87%E8%8A%AE.html
    private boolean checkUrl(String url) {
        boolean flag=false;
        if (url.contains(".html")&&(url.contains("http://meirentu.cc/model/")||url.contains("http://meirentu.cc/model/"))) flag=true;
        return flag;
    }
    private void labelUpdate() {
        if (!label.getText().equals("暂无下载任务")&&(downloadThread==null)){
            Platform.runLater(()->{
                label.setTextFill(Color.GREEN);
                label.setText("暂无下载任务");
            });
        }
        if (downloadThread!=null&&downloadThread.isAlive()){
            label.setTextFill(Color.RED);
            Platform.runLater(()->{label.setText("下载中....");});
        }
    }
    @Override
    public void run() {
        labelUpdate();
    }

    public void startTimer(){
        timerTask.start();
    }
    public void stopTimer(){timerTask.stop();}
}
