package com.chartiq.sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.chartiq.sample.ChartIQ.DataSourceCallback;
import com.chartiq.sample.model.OHLCChart;
import com.google.gson.Gson;
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

		chartIQ.setDataSource(new ChartIQ.DataSource() {
			@Override
			public void pullUpdateData(Map<String, Object> params, DataSourceCallback callback) {
				loadChartData(params, callback);
			}

			@Override
			public void pullPaginationData(Map<String, Object> params, DataSourceCallback callback) {
				loadChartData(params, callback);
			}

			@Override
			public void pullInitialData(Map<String, Object> params, DataSourceCallback callback) {
				loadChartData(params, callback);
			}
		});
	}

	private void loadChartData(Map<String, Object> params, final ChartIQ.DataSourceCallback callback) {
		if (!params.containsKey("start") || params.get("start") == null || "".equals(params.get("start"))) {
			params.put("start", "2016-12-16T16:00:00.000Z");
		}

		if (params.containsKey("end") || "".equals(params.get("start"))) {
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
			df.setTimeZone(tz);
			String endDate = df.format(new Date());
			params.put("end", endDate);
		}

		boolean isMinute = params.containsKey("interval");
		// && TextUtils.isDigitsOnly(String.valueOf(params.get("interval")));
		params.put("interval", isMinute ? "minute" : params.get("interval"));
		params.put("period", isMinute ? "minute" : params.get("period"));

		StringBuilder builder = new StringBuilder();
		builder.append("http://simulator.chartiq.com/datafeed?");
		builder.append("identifier=" + params.get("symbol"));
		builder.append("&startdate=" + params.get("start"));
		if (params.containsKey("end")) {
			builder.append("&enddate=" + params.get("end"));
		}
		builder.append("&interval=" + params.get("interval"));
		builder.append("&period=" + params.get("period"));
		builder.append("&seed=1001");

		final String url = builder.toString();
		final String symbol = String.valueOf(params.get("symbol"));

		ExecutorService executor = Executors.newFixedThreadPool(25);

		Runnable runnableTask = () -> {
			String body = "";
			try {
				System.out.println("URL: " + url);
				URL connectionUrl = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) connectionUrl.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.connect();
				int code = connection.getResponseCode();
				
				InputStream is;
				StringBuilder response;
				if (code >= 200 && code < 400) {
					response = new StringBuilder();
					is = connection.getInputStream();
				} else {
					is = connection.getErrorStream();
					response = new StringBuilder("Error(" + code + "): ");
				}
				if (is != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = "";
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					body = response.toString();
					
					OHLCChart[] data = new Gson().fromJson(body, OHLCChart[].class);
                    callback.execute(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
				OHLCChart[] data = {};
                callback.execute(data);
			}
		};

		executor.execute(runnableTask);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
