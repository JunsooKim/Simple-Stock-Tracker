package com.example.hughkim.stocktracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class StockPageActivity extends AppCompatActivity {
    public String symbol;
    public Long ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_page);

        Intent i = getIntent();

        symbol = i.getStringExtra("symbol");
        ID = i.getLongExtra("ID", 0);

        WebView wv = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = wv.getSettings();
        webSettings.setBuiltInZoomControls(true);
        wv.setWebViewClient(new MyWebClient());

        String address1 = "https://www.marketwatch.com/investing/stock/";
        String finaladdress = address1+symbol;
        //Load a URL
        wv.loadUrl(finaladdress);

        //Toast.makeText(StockDetailsActivity.this, id.toString(), Toast.LENGTH_SHORT).show();
    }

    private class MyWebClient extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item1 = menu.add(0,0,0,"Exit");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Intent i = new Intent(getApplicationContext(), StockDetailsActivity.class);
            i.putExtra("symbol", symbol);
            i.putExtra("ID", ID);
            //Log.d("WebPageID", );
            startActivityForResult(i, 1);
        }
        return false;
    }
}
