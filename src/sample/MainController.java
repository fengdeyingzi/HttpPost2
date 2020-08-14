package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;


public class MainController {

    @FXML
    protected TextField input_url;
    @FXML
    protected TextArea input_post;
    @FXML
    protected Button btn_get;
    @FXML
    protected TextArea text_retext;
}
