package com.eduardo.brownianmonkey;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private List<String> modelos = new ArrayList<>();
    private float SB_MULTIPLIER = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.configs);

        // buscando configurações
        final SharedPreferences configs = getSharedPreferences(getString(R.string.opcoes), 0);
        final TextView txt_tempo_valor;
        Spinner spinner;
        SeekBar seekBar;

        // boas-vindas à macacada
        Toast toast = Toast.makeText(getApplicationContext(), R.string.configs_tip, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 200);
        toast.show();

        // opcoes para os modelos
        modelos.add(getString(R.string.mod_unif));
        modelos.add(getString(R.string.mod_normal));

        // configurando spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, modelos);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(arrayAdapter.getPosition(
                configs.getString("modelo", getString(R.string.mod_unif))));

        // Método do Spinner para capturar o item selecionado
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                // recupera item selecionado e salva nas configurações
                String modelo = parent.getItemAtPosition(posicao).toString();
                SharedPreferences.Editor editor = configs.edit();
                editor.putString("modelo", modelo);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // configurando o tempo do macaco pensar
        txt_tempo_valor = findViewById(R.id.txt_tempo_valor);
        seekBar = findViewById(R.id.seekBar);
        float tempo = configs.getFloat("time", 1.7f);
        seekBar.setProgress(Float.valueOf(tempo * SB_MULTIPLIER).intValue());
        if (tempo <= 0)
            txt_tempo_valor.setText(R.string.txt_tempo_0);
        else if (tempo == 1)
            txt_tempo_valor.setText(R.string.txt_tempo_1);
        else
            txt_tempo_valor.setText(String.format(getString(R.string.txt_tempo), tempo));

        // mudando valor do tempo
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float tempo = i / SB_MULTIPLIER;
                if (tempo <= 0)
                    txt_tempo_valor.setText(R.string.txt_tempo_0);
                else if (tempo == 1)
                    txt_tempo_valor.setText(R.string.txt_tempo_1);
                else
                    txt_tempo_valor.setText(String.format(getString(R.string.txt_tempo), tempo));

                // salvando
                SharedPreferences.Editor editor = configs.edit();
                editor.putFloat("time", tempo);
                editor.apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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
