package com.chartiq.sample;

import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import com.teamdev.jxbrowser.chromium.events.ConsoleEvent;
import com.teamdev.jxbrowser.chromium.events.ConsoleListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChartIQSample extends Application {

	ChartIQ chartIQ = new ChartIQ();

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
		Button button = new Button("Lookup");
		HBox hb = new HBox();
		hb.getChildren().addAll(label1, textField, button);
		hb.setSpacing(10);
		hb.setStyle("-fx-background-color: #336699;");

		

		// Browser browser = new Browser();
		// BrowserView view = new BrowserView(browser);
		//
		// BorderPane borderPane = new BorderPane();
		// borderPane.setCenter(view);
		// borderPane.setTop(hb);
		//
		// String remoteDebuggingURL = browser.getRemoteDebuggingURL();
		//
		// JFrame frame1 = new JFrame();
		// frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// frame1.add(view, BorderLayout.CENTER);
		// frame1.setSize(700, 500);
		// frame1.setLocationRelativeTo(null);
		// frame1.setVisible(true);

		// Scene scene = new Scene(borderPane, 700, 500);
		// primaryStage.setScene(scene);
		// primaryStage.show();

		chartIQ.setBrowser("http://192.168.1.147:8080/default/sample-template-native-sdk.html");
		BrowserView browserView = chartIQ.getBrowserView();

		// Creates another Browser instance and loads the remote Developer
		// Tools URL to access HTML inspector.
		// Browser browser2 = new Browser();
		// BrowserView browserView2 = new BrowserView(browser2);

		// JFrame frame2 = new JFrame();
		// frame2.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// frame2.add(browserView2, BorderLayout.CENTER);
		// frame2.setSize(700, 500);
		// frame2.setLocationRelativeTo(null);
		// frame2.setVisible(true);

		// browser2.loadURL(browser.getRemoteDebuggingURL());

		System.out.println(chartIQ.getBrowser().getRemoteDebuggingURL());

		BorderPane pane = new BorderPane();
		pane.setTop(hb);
		pane.setCenter(browserView);
		// pane.getChildren()browser.(browserView, BorderLayout.CENTER);
		Scene scene = new Scene(pane, 500, 400);
		primaryStage.setTitle("ChartIQ Demo");
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
			}
		});

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String symbol = "IBM";
				chartIQ.setSymbol("AAPL");
				// browser.executeJavaScript("test2();");
				// browser.executeJavaScript("alert('asdf')");
				// browser.executeJavaScript("stxx.newChart('IBM');");
				// browser.executeJavaScript("window.callNewChart('ibm');");
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
