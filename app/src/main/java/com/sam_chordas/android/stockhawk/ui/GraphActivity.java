package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sam_chordas.android.stockhawk.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 * Created by Srinu Mallidi.
 */

public class GraphActivity extends AppCompatActivity {
    ArrayList<String> priceArray2;
    ArrayList<Entry> priceArray;
    ArrayList<String> dateArray;
    LineChart lineChart;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);


        Intent intent = getIntent();
        priceArray = new ArrayList<>();
        dateArray = new ArrayList<>();
        priceArray2 = new ArrayList<>();
        lineChart = (LineChart) findViewById(R.id.lineChart);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, priceArray2);

        StockAsyncTask stockAsyncTask = new StockAsyncTask();
        stockAsyncTask.execute(intent.getStringExtra("Key"));

        LineDataSet dataSet = new LineDataSet(priceArray, "Closing price of stock");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        LineData data = new LineData(dateArray, dataSet);
        lineChart.setData(data);
        lineChart.setDescription(intent.getStringExtra("Key")+" performance over the year");


    }

    private class StockAsyncTask extends AsyncTask<String, Void, LineData> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LineData doInBackground(String... params) {
            Stock stock = null;

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.add(Calendar.YEAR, -1); // from 1 years ago
            List<HistoricalQuote> historicalQuoteList = null;


            try {
                stock = YahooFinance.get(params[0], from, to, Interval.WEEKLY);
                historicalQuoteList = stock.getHistory();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int j = historicalQuoteList.size()-1;
            for (int i = 0; i < historicalQuoteList.size(); i++) {
                String closing = historicalQuoteList.get(j).getClose().toString();
                Float closingPrice = Float.valueOf(closing);
                long date = historicalQuoteList.get(j).getDate().getTimeInMillis();
                String dateString = String.valueOf(date);
                priceArray.add(new Entry(closingPrice, i));
                dateArray.add(dateString);
                j--;
            }

            LineDataSet dataSet = new LineDataSet(priceArray, "Price of stock");
            LineData data = new LineData(dateArray, dataSet);
            return data;
        }

        @Override
        protected void onPostExecute(LineData data) {
            super.onPostExecute(data);
            lineChart.setData(data);
            Toast.makeText(getBaseContext(), "Click on graph to see data.", Toast.LENGTH_SHORT).show();
        }
    }

}
