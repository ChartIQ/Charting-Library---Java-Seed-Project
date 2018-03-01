package com.chartiq.sample;

import com.chartiq.sample.model.DataSources;
import com.google.gson.Gson;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
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

public class ChartIQSample extends Application implements DataSources.DataSource {

	private ChartIQ chartIQ;
	private TextField textField;

	static final String stxUrl = "http://stxdev.local/default/sample-template-native-sdk.html";
	static final String userAgent = "Mozilla/5.0 (Linux; <android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>";

	@Override
	public void init() throws Exception {

		BrowserPreferences.setUserAgent(userAgent);
		BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");

		// On Mac OS X Chromium engine must be initialized in non-UI thread.
		if (Environment.isMac()) {
			BrowserCore.initialize();
		}
	}

	@Override
	public void start(final Stage primaryStage) throws IOException {

		this.chartIQ = new ChartIQ();
		chartIQ.setBrowser(stxUrl);
		BrowserView browserView = chartIQ.getBrowserView();

		System.out.println("Debugging URL:");
		System.out.println(chartIQ.getBrowser().getRemoteDebuggingURL());

		primaryStage.setTitle("ChartIQ Example");

		textField = new TextField();
		textField.setPrefSize(150, 28);
		textField.setPrefColumnCount(15);
		textField.setPromptText("Enter Symbol");
		textField.setFocusTraversable(false);

		ComboBox periodicity = new ComboBox();
		periodicity.getItems().addAll("1 Day", "1 Week", "1 Month", "1 Minute");
		periodicity.getSelectionModel().select(0);

		Button button = new Button("Lookup");
		HBox hb = new HBox();
		hb.getChildren().addAll(textField, button, periodicity);
		hb.setSpacing(10);
		hb.setStyle("-fx-background-color: #336699;");

		BorderPane pane = new BorderPane();
		pane.setTop(hb);
		pane.setCenter(browserView);

		Scene scene = new Scene(pane, 800, 500);
		primaryStage.setTitle("ChartIQ Demo");
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(event -> Platform.exit());

		button.setOnAction(e -> {
            String symbol = textField.getText();
            chartIQ.setSymbol(symbol);
        });

		chartIQ.setDataSource(this);

	}

	// DataSources.DataSource members
	@Override
	public void pullUpdateData(Map<String, Object> params, DataSources.DataSourceCallback callback) {

		System.out.println("pullUpdateData");
		loadChartData(params, callback);
	}

	@Override
	public void pullPaginationData(Map<String, Object> params, DataSources.DataSourceCallback callback) {

		System.out.println("pullPaginationData");
		loadChartData(params, callback);
	}

	@Override
	public void pullInitialData(Map<String, Object> params, DataSources.DataSourceCallback callback) {

		System.out.println("pullInitialData");
		loadChartData(params, callback);
	}

	private void loadChartData(Map<String, Object> params, final DataSources.DataSourceCallback callback) {

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

		ExecutorService executor = Executors.newFixedThreadPool(4);

		Runnable runnableTask = () -> {
			String body = "";
			try {
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
					
					DataSources.OHLCChart[] data = new Gson().fromJson(body, DataSources.OHLCChart[].class);
                    callback.execute(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
				DataSources.OHLCChart[] data = {};
                callback.execute(data);
			}
		};

		executor.execute(runnableTask);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
