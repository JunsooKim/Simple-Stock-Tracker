package com.example.hughkim.stocktracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private String[] stocklist = {"AAPL", "TSLA", "NDY"};
    ListView lv;
    EditText et;
    private String company;
    private String ticker;
    private Database db = new Database(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db.open();
        populateListView();
        db.close();


        lv = (ListView)findViewById(R.id.liststock);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent i = new Intent(getApplicationContext(), StockDetailsActivity.class);
                i.putExtra("ID", id);
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item1 = menu.add(0,0,0,"Add a stock");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Toast.makeText(this, "Please add the ticker of the stock you want to track", Toast.LENGTH_SHORT).show();
            // get prompts.xml view

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            //builder.setView(inflater.inflate(R.layout.prompt, null));
            final View v_iew = inflater.inflate(R.layout.prompt, null);
            builder.setView(v_iew);
            builder.setCancelable(false);
            builder.setPositiveButton("enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "successfully added", Toast.LENGTH_SHORT).show();
                    et = (EditText) v_iew.findViewById(R.id.companyadd);
                    String new_company = et.getText().toString();
                    et = (EditText) v_iew.findViewById(R.id.tickeradd);
                    String new_ticker = et.getText().toString();
                    //et = (EditText)findViewById(R.id.tickeradd);
                    //String new_ticker = et.getText().toString();
                    db.open();
                    db.addStock(new_company, new_ticker);
                    db.close();
                    finish();
                    startActivity(getIntent());
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
        return false;
    }


    private void populateListView(){
        Cursor cursor = db.getAllStocks();
        String[] fromFieldNames = new String[] {Database.KEY_COMPANY, Database.KEY_TICKER};
        int[] toView = new int[]{R.id.company, R.id.ticker};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.listlayout, cursor, fromFieldNames, toView, 0);
        lv = (ListView)findViewById(R.id.liststock);
        lv.setAdapter(myCursorAdapter);
        //+" "+Database.KEY_TICKER

    }

}
