package com.chartiq.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.chartiq.sample.model.OHLCChart;
import com.google.gson.Gson;
import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

public class ChartIQ {

	Browser browser;
	BrowserView browserView;
	private DataSource dataSource;
	private ArrayList<OnPullInitialDataCallback> onPullInitialData = new ArrayList<>();
	private ArrayList<OnPullUpdateCallback> onPullUpdate = new ArrayList<>();
	private ArrayList<OnPullPaginationCallback> onPullPagination = new ArrayList<>();

	JavaObject javaObject = new JavaObject();

	public ChartIQ() {

		BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");
		browser = new Browser();
		browserView = new BrowserView(browser);
		browser.addLoadListener(new LoadAdapter() {

			@Override
			public void onDocumentLoadedInFrame(FrameLoadEvent event) {
				System.out.println("Frame document is loaded.");
				JSValue window = browser.executeJavaScriptAndReturnValue("window");
				System.out.println(window);

				window.asObject().setProperty("QuoteFeed", ChartIQ.this);
				// browser.executeJavaScript("testing()");

				browser.executeJavaScript("attachQuoteFeed(1)");
				// browser.executeJavaScript("nativeQuoteFeed(parameters, cb)");
				// executeJavascript("nativeQuoteFeed(parameters, cb)", null);
			}

			@Override
			public void onDocumentLoadedInMainFrame(LoadEvent event) {
				System.out.println("Main frame document is loaded.");
			}
		});
	}

	public void setSymbol(String symbol) {
		browser.executeJavaScript("callNewChart(\"" + symbol + "\");");
		// executeJavascript("callNewChart(\"" + symbol + "\");", toastCallback);
		// addEvent(new Event("CHIQ_setSymbol").set("symbol", symbol));
	}

	public BrowserView getBrowserView() {
		return browserView;
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(String url) {
		browser.loadURL(url);
	}

	@JSAccessible
	public void testJS() {
		System.out.println("TESTING JAVASCRIPT BRIDGE");
	}

	public interface DataSourceCallback {
		void execute(OHLCChart[] data);
	}

	public ArrayList<OnPullInitialDataCallback> getOnPullInitialDataCallbacks() {
		return onPullInitialData;
	}

	public ArrayList<OnPullUpdateCallback> getOnPullUpdateCallbacks() {
		return onPullUpdate;
	}

	public ArrayList<OnPullPaginationCallback> getOnPullPaginationCallbacks() {
		return onPullPagination;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public interface DataSource {
		void pullInitialData(Map<String, Object> params, DataSourceCallback callback);

		void pullUpdateData(Map<String, Object> params, DataSourceCallback callback);

		void pullPaginationData(Map<String, Object> params, DataSourceCallback callback);
	}

	@JSAccessible
	public void pullInitialData(final String symbol, int period, String interval, String start, String end, Object meta,
			final String id) {
		System.out.println(symbol);
		System.out.println(period);

		Map<String, Object> params = new HashMap<>();
		params.put("symbol", symbol == null ? "" : symbol);
		params.put("period", period);
		params.put("interval", interval);
		params.put("start", start == null ? "" : start);
		params.put("end", end == null ? "" : end);
		params.put("meta", meta);
		
		if (dataSource != null) {
			dataSource.pullInitialData(params, new DataSourceCallback() {
				@Override
				public void execute(OHLCChart[] data) {
					ChartIQ.this.invokePullCallback(id, data);
				}
			});
		}
	}

	@JSAccessible
	public void pullUpdate(final String symbol, int period, String interval, String start, Object meta,
			final String callbackId) {
		Map<String, Object> params = new HashMap<>();
		params.put("symbol", symbol == null ? "" : symbol);
		params.put("period", period);
		params.put("interval", interval);
		params.put("start", start == null ? "" : start);
		params.put("meta", meta);

		if (dataSource != null) {
			dataSource.pullUpdateData(params, new DataSourceCallback() {
				@Override
				public void execute(OHLCChart[] data) {
					ChartIQ.this.invokePullCallback(callbackId, data);
				}
			});
		}
	}

	@JSAccessible
	public void pullPagination(final String symbol, int period, String interval, String start, String end, Object meta,
			final String callbackId) {
		Map<String, Object> params = new HashMap<>();
		params.put("symbol", symbol == null ? "" : symbol);
		params.put("period", period);
		params.put("interval", interval);
		params.put("start", start == null ? "" : start);
		params.put("end", end == null ? "" : end);
		params.put("meta", meta);

		if (dataSource != null) {
			dataSource.pullPaginationData(params, new DataSourceCallback() {
				@Override
				public void execute(OHLCChart[] data) {
					ChartIQ.this.invokePullCallback(callbackId, data);
				}
			});
		}
	}

	private void invokePullCallback(String callbackId, OHLCChart[] data) {
		String json = new Gson().toJson(data);
		browser.executeJavaScript("parseData('" + json + "', \"" + callbackId + "\");");
	}

	interface OnPullInitialDataCallback {
		void execute(String symbol, int period, String timeUnit, Date start, Date end, Object meta);
	}

	interface OnPullUpdateCallback {
		void execute(String symbol, int period, String timeUnit, Date start, Object meta);
	}

	interface OnPullPaginationCallback {
		void execute(String symbol, int period, String timeUnit, Date start, Date end, Object meta);
	}

	public class JavaObject {
		@JSAccessible
		public String accessibleField;
		public String nonAccessibleField;

		public void doAction() {
		}

		@JSAccessible
		public void doAccessibleAction() {
		}
	}
}
