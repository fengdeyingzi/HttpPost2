package sample;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.xl.tool.OConnect;
import com.xl.util.JsonFormat;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Main extends Application {


    protected TextField input_url;

    protected TextArea input_post;

    protected Button btn_get;

    protected TextArea text_retext;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("HttpPost2 - 风的影子");
        primaryStage.setScene(new Scene(root, 640, 640));
        primaryStage.show();
        input_url = (TextField)root.lookup("#input_url");
        input_post = (TextArea)root.lookup("#input_post");
        input_post.setWrapText(true);
        btn_get = (Button)root.lookup("#btn_get");
        text_retext = (TextArea)root.lookup("#text_retext");
        btn_get.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("btn_get");

                if(input_url.getText().indexOf("://")<=0){
                    input_url.setText("http://"+input_url.getText());
                }
                String url = input_url.getText();
                String param = input_post.getText();
                if(url.length()==0 || url.equals("http://")){
                    url = parseUrl(param);
                    param = parseParams(param);
                }
                OConnect connect = new OConnect(url, param, new OConnect.PostGetInfoListener() {
                    @Override
                    public void onPostGetText(String text) {
                        if(text.startsWith("{") || text.startsWith("\n{")){
                            text = JsonFormat.formatJson(text);
                        }
                        text_retext.setText(text);
                        System.out.println("获取数据成功");
                    }
                });
                connect.start();
            }
        });

    }

    //解析数据 返回url 和 param
    String parseUrl(String text){
        int type=0;
        String url = "";
        String params = "";
        String[] items = text.trim().split(" ");
        if(items.length>=3){
            if(items[0].equals("-->")){
                if(items[1].equals("GET")){
                    url = items[2];
                }
                else if(items[1].equals("POST")){
                    url = items[2];
                    params = items[3];
                }
                else{
                    System.out.println("未知数据");
                }
            }
        }
        return url;
    }
    //解析数据 返回url 和 param
    String parseParams(String text){
        int type=0;
        String url = "";
        String params = "";
        String[] items = text.split(" ");
        if(items.length>=3){
            if(items[0].equals("-->")){
                if(items[1].equals("GET")){
                    url = items[2];
                }
                else if(items[1].equals("POST")){
                    url = items[2];
                    params = items[3];
                }
                else{
                    System.out.println("未知数据");
                }
            }
        }
        return params;
    }


    public static void main(String[] args) {
        // 启动looper
        Looper.prepare(true);
        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                System.out.println("message....");
            }
        };

        handler.sendMessage(new Message());
        launch(args);
        Looper.loop();
    }
}
