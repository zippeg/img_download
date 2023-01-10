package core.fx;


import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;


public class Main extends Application {
    private Stage stage;
    private ImageDownLoadScene imageDownLoadScene;
    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        ImageDownLoadScene imageDownLoadScene = new ImageDownLoadScene();
        imageDownLoadScene.getJmpButton().setOnAction(j->{
                HostServices hostServices=this.getHostServices();
                hostServices.showDocument("https://meirentu.cc");
        });
        stage.setScene(imageDownLoadScene.getScene());
        stage.setTitle(imageDownLoadScene.getTitle());
        stage.getIcons().setAll(imageDownLoadScene.getImage());
        imageDownLoadScene.startTimer();
        stage.show();
    }



}
