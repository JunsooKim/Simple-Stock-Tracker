package com.example.hughkim.stocktracker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class StockDetailsActivity extends AppCompatActivity {
    private Database db = new Database(this);
    private TextView txt;
    private String company;
    public String ticker;
    public Long id;
    final private int REQUEST_INTERNET = 123;

    private String readJSONFeed(String address) {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) { e.printStackTrace(); }

        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) { e.printStackTrace(); };
        try {
            InputStream content = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
        } catch (IOException e) { e.printStackTrace(); }
        finally {
            urlConnection.disconnect(); }
        return stringBuilder.toString();
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject object = new JSONObject(result);

                String ticker1 = object.getString("symbol");
                TextView view = (TextView) findViewById(R.id.StockSymbol);
                view.setText(ticker1);

                String company_name = object.getString("companyName");
                TextView view2 = (TextView) findViewById(R.id.CompanyName);
                view2.setText(company_name);

                Double latest_priced = object.getDouble("latestPrice");
                TextView view3 = (TextView) findViewById(R.id.LatestStockPrice);
                String latest_price = String.valueOf(latest_priced);
                view3.setText(latest_price);

                Double changed = object.getDouble("change");
                TextView view4 = (TextView) findViewById(R.id.LatestPriceChange);
                String change = String.valueOf(changed);
                view4.setText(change);

                Double change_Percentd = object.getDouble("changePercent");
                TextView view5 = (TextView) findViewById(R.id.Pricechangepercentage);
                String change_Percent = String.valueOf(change_Percentd);
                view5.setText(change_Percent);

            }
            catch (JSONException e) { e.printStackTrace(); }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        Intent i = getIntent();

        id = i.getLongExtra("ID", 0);
        Log.d("AppPAGEID", id.toString());
        //Toast.makeText(StockDetailsActivity.this, id.toString(), Toast.LENGTH_SHORT).show();
        db.open();
        Cursor c = db.getStock(id);
        company = c.getString(1);
        ticker = c.getString(2);
        db.close();

        String address1 = "https://api.iextrading.com/1.0/stock/";
        String address2 = "/quote";
        String finaladdress = address1+ticker+address2;

        //TextView view = (TextView) findViewById(R.id.LatestStockPrice);
        //view.setText("Latest Stock Price is 103");

        new ReadJSONFeedTask().execute(finaladdress);
        positive();

        /*

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET);
        } else{
            //new AccessWebServiceTask().execute("football");
            new ReadJSONFeedTask().execute(finaladdress);
        }

        */


        //https://api.iextrading.com/1.0/stock/{stock_symbol}/quote

    }

    public void positive(){
        txt = (TextView) findViewById(R.id.LatestPriceChange);
        String number1 = txt.getText().toString();
        if(number1.contains("-")){
            txt.setTextColor(getResources().getColor(R.color.negative));
        }
        else{
            txt.setTextColor(getResources().getColor(R.color.positive));
        }

        TextView txt2 = (TextView) findViewById(R.id.Pricechangepercentage);
        //int number2 = Integer.parseInt(txt2.getText().toString());
        String number2 = txt2.getText().toString();
        if(number2.contains("-")){
            txt2.setTextColor(getResources().getColor(R.color.negative));
        }
        else{
            txt2.setTextColor(getResources().getColor(R.color.positive));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item1 = menu.add(0,0,0,"Refresh Data");
        MenuItem item2 = menu.add(0,1,1,"Visit Stock Page");
        MenuItem item3 = menu.add(0,2,2,"Delete Stock");
        MenuItem item4 = menu.add(0,3,3,"Exit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 0:
                Log.d("CASE", "1");
                refresh();
                return true;
                //have it send it's own intent back to itself
            case 1:
                Log.d("CASE2", "2");
                visitstock();
                return true;
            case 2:
                Log.d("CASE3", "3");
                deletestock();
                return true;
            case 3:
                Log.d("CASE4", "4");
                exit();
                return true;
        }
        return false;
    }


    public void refresh(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /*

    public void refresh(){
        finish();
        Intent i = new Intent(getApplicationContext(), StockDetailsActivity.class);
        i.putExtra("symbol", ticker);
        i.putExtra("ID", id);
        startActivityForResult(i, 1);
    }

    */

    //public void refresh(){
     //   finish();
     //   startActivity(getIntent());
    //}

    public void visitstock(){
        Intent i = new Intent(getApplicationContext(), StockPageActivity.class);
        i.putExtra("symbol", ticker);
        i.putExtra("ID", id);
        startActivity(i);
    }

    public void deletestock(){
        db.open();
        db.deleteByID(id);
        db.close();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void exit(){
        startActivity(new Intent(this, MainActivity.class));
    }
}
