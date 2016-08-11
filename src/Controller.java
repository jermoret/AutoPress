import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Controller {

    private Robot robot;

    @FXML
    private TextField tfMouseTimeBeforeStart;

    @FXML
    private TextField tfMouseTimeBetweenPress;

    @FXML
    private TextField tfKeyTimeBeforeStart;

    @FXML
    private TextField tfKeyTimeBetweenPress;

    @FXML
    private Button btnKeyListening;

    @FXML
    private Label lblKey;

    @FXML
    private CheckBox chkMousePress;

    @FXML
    private CheckBox chkKeyPress;

    @FXML
    private CheckBox chkCtrl;

    @FXML
    private CheckBox chkShift;

    @FXML
    private CheckBox chkAlt;

    private KeyEvent key = null;

    @FXML
    private void initialize() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private Timeline generateMouseClicks() {
        double timeBetweenMouseClick = Double.valueOf(tfMouseTimeBetweenPress.getText());
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(timeBetweenMouseClick),
                ae -> {
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        return timeline;
    }

    private Timeline generateKeyClicks() {
        double timeBetweenKeyClick = Double.valueOf(tfKeyTimeBetweenPress.getText());

        List<Integer> extraKeyCodes = new ArrayList<>();
        if(chkCtrl.isSelected()) extraKeyCodes.add(java.awt.event.KeyEvent.VK_CONTROL);
        if(chkAlt.isSelected()) extraKeyCodes.add(java.awt.event.KeyEvent.VK_ALT);
        if(chkShift.isSelected()) extraKeyCodes.add(java.awt.event.KeyEvent.VK_SHIFT);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(timeBetweenKeyClick),
                ae -> {
                    for(Integer keyCode : extraKeyCodes) { robot.keyPress(keyCode); }
                    robot.keyPress(key.getCode().impl_getCode());
                    for(Integer keyCode : extraKeyCodes) { robot.keyRelease(keyCode); }
                    robot.keyRelease(key.getCode().impl_getCode());
                }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        return timeline;
    }

    private void waiting(double seconds, Callable<Timeline> callback){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(seconds),
                ae -> {
                    try {
                        callback.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ));
        timeline.play();
    }

    @FXML
    private void handleStart() {
        if(chkMousePress.isSelected()) {
            double mouseTimeBeforeStart = Double.valueOf(this.tfMouseTimeBeforeStart.getText());
            waiting(mouseTimeBeforeStart, () -> generateMouseClicks());
        }
        if(chkKeyPress.isSelected() && key != null) {
            double keyTimeBeforeStart = Double.valueOf(tfKeyTimeBeforeStart.getText());
            waiting(keyTimeBeforeStart, () -> generateKeyClicks());
        }
    }

    @FXML
    private void clickKeyListening() {
        Platform.runLater(() -> {
            lblKey.setText("none");
            btnKeyListening.setDisable(true);
        });
    }

    public void keyPressed(KeyEvent keyEvent) {
        if(btnKeyListening.isDisabled()){
            key = keyEvent;
            Platform.runLater(() -> {
                lblKey.setText(keyEvent.getCode().getName());
                btnKeyListening.setDisable(false);
            });
        }
    }

    @FXML
    private void clickAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Version 1.0");
        alert.setContentText("Created by Jérôme Moret.");
        alert.showAndWait();
    }
}
