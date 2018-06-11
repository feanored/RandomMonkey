package com.eduardo.brownianmonkey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        Toast toast = Toast.makeText(getApplicationContext(), R.string.sobre_tip, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 200);
        toast.show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
