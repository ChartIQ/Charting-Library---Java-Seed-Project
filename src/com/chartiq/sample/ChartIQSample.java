package com.chartiq.sample;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Demonstrates how to embed Browser instance into JavaFX application.
 */
public class ChartIQSample extends Application {

    @Override
    public void init() throws Exception {
        // On Mac OS X Chromium engine must be initialized in non-UI thread.
        if (Environment.isMac()) {
            BrowserCore.initialize();
        }
    }

    @Override
    public void start(final Stage primaryStage) {
    	Label label1 = new Label("Symbol:");
    	
    	primaryStage.setTitle("ChartIQ Example");
    	TextField textField = new TextField();
    	textField.setPrefSize(50, 28);
    	HBox hb = new HBox();
    	//hb.getChildren().addAll(label1, textField);
    	hb.setSpacing(10);
    	hb.setStyle("-fx-background-color: #336699;");
    	
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);
        
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(view);
        borderPane.setTop(hb);

        Scene scene = new Scene(borderPane, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        browser.loadURL("http://localhost:8080/default/template-basic.html");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
