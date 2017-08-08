package com.chartiq.sample;

import java.util.Map;

import com.chartiq.sample.model.OHLCChart;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSAccessible;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

public class ChartIQ {

	Browser browser;
	BrowserView browserView;

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

				browser.executeJavaScript("attachQuoteFeed(0)");
				//browser.executeJavaScript("nativeQuoteFeed(parameters, cb)");
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
