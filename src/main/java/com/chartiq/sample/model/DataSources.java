package com.chartiq.sample.model;

import java.util.Date;
import java.util.Map;

public class DataSources {

    public interface DataSource {
        void pullInitialData(Map<String, Object> params, DataSourceCallback callback);

        void pullUpdateData(Map<String, Object> params, DataSourceCallback callback);

        void pullPaginationData(Map<String, Object> params, DataSourceCallback callback);
    }

    public interface DataSourceCallback {
        void execute(OHLCChart[] data);
    }

    public class OHLCChart {

        public Date DT;
        public double Open;
        public double High;
        public double Low;
        public double Close;
        public double Volume;
        public double AdjClose;

        public OHLCChart(Date date, double open, double high, double low, double close, double volume, double adjClose) {
            this.DT = date;
            this.Open = open;
            this.High = high;
            this.Low = low;
            this.Close = close;
            this.Volume = volume;
            this.AdjClose = adjClose;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"DT\":\"" + DT +
                    "\",\"Open\":" + Open +
                    ",\"High\":" + High +
                    ",\"Low\":" + Low +
                    ",\"Close\":" + Close +
                    ",\"Volume\":" + Volume +
                    ",\"Adj_Close\":" + AdjClose +
                    "}";
        }
    }
}