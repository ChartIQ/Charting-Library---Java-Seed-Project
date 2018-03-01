package com.chartiq.sample;

import com.chartiq.sample.model.DataSources;
import com.google.gson.Gson;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSAccessible;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import java.util.HashMap;
import java.util.Map;

public class ChartIQ implements LoadListener {

	private Browser browser;
	private BrowserView browserView;
	private DataSources.DataSource dataSource;

	public ChartIQ() {

		browser = new Browser();
		browserView = new BrowserView(browser);

		browser.addConsoleListener(event -> System.out.println("Console Message: " + event.getMessage()));
		browser.addLoadListener(this);

	}

	public void setSymbol(String symbol) {

		System.out.println("Setting Symbol:" + symbol);
		browser.executeJavaScript("callNewChart(\"" + symbol + "\");");
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


	public void setDataSource(DataSources.DataSource dataSource) {

		this.dataSource = dataSource;
	}

	@JSAccessible
	public void pullInitialData(final String symbol, int period, String interval, String start, String end, Object meta, final String id) {

		Map<String, Object> params = new HashMap<>();
		params.put("symbol", symbol == null ? "" : symbol);
		params.put("period", period);
		params.put("interval", interval);
		params.put("start", start == null ? "" : start);
		params.put("end", end == null ? "" : end);
		params.put("meta", meta);
		
		if (dataSource != null) {
			dataSource.pullInitialData(params, data -> ChartIQ.this.invokePullCallback(id, data));
		}
	}

	@JSAccessible
	public void pullUpdate(final String symbol, int period, String interval, String start, Object meta, final String callbackId) {

		Map<String, Object> params = new HashMap<>();
		params.put("symbol", symbol == null ? "" : symbol);
		params.put("period", period);
		params.put("interval", interval);
		params.put("start", start == null ? "" : start);
		params.put("meta", meta);

		if (dataSource != null) {
			dataSource.pullUpdateData(params, data -> ChartIQ.this.invokePullCallback(callbackId, data));
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
			dataSource.pullPaginationData(params, data -> ChartIQ.this.invokePullCallback(callbackId, data));
		}
	}

	private void invokePullCallback(String callbackId, DataSources.OHLCChart[] data) {
		String json = new Gson().toJson(data);
		browser.executeJavaScript("parseData('" + json + "', \"" + callbackId + "\");");
	}

	/* LoadListener Methods*/
	@Override
	public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
		// Empty
	}

	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
		// Empty
	}

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
		// Empty
	}

	@Override
	public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {
		// Empty
	}

	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent event) {
		JSValue window = browser.executeJavaScriptAndReturnValue("window");
		window.asObject().setProperty("QuoteFeed", ChartIQ.this);
		browser.executeJavaScript("attachQuoteFeed(1)");
		browser.executeJavaScript("isAndroid=true");
	}

	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent event) {
		System.out.println("Main frame document is loaded.");
	}
}
