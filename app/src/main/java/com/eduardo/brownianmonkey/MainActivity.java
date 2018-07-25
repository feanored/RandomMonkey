package com.eduardo.brownianmonkey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button bm1, bp1, bm10, bp10, bgo;
    EditText qtd;
    TextView textRes, textLoad;
    SharedPreferences configs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Random rand = new Random();
        configs = getSharedPreferences(getString(R.string.opcoes), 0);

        // aviso de boas-vindas ao macaco
        Toast toast = Toast.makeText(getApplicationContext(), R.string.s_aviso, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 300);
        toast.show();

        // Registrando elementos
        bm1 = findViewById(R.id.bm1);
        bp1 = findViewById(R.id.bp1);
        bm10 = findViewById(R.id.bm10);
        bp10 = findViewById(R.id.bp10);
        bgo = findViewById(R.id.bgo);
        qtd = findViewById(R.id.qtde);
        textRes = findViewById(R.id.textRes);
        textLoad = findViewById(R.id.textLoad);

        // recuperando valores para sorteio da configuração
        qtd.setText(String.valueOf(configs.getInt("qtde", 0)));
        textRes.setText(String.valueOf(configs.getInt("sorteio", 0)));

        // Eventos
        bgo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int qtd = getQtd();
                double valor = 0;
                int valor_int = 0;
                String modelo = configs.getString("modelo", getString(R.string.mod_unif));
                if(qtd < 2) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.s_min, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, -100);
                    toast.show();
                } else {
                    sorteio();
                    // MODELO UNIFORME NO INTERVALO
                    if (modelo.equals(getString(R.string.mod_unif))){
                        valor_int = rand.nextInt(qtd);
                        if (valor_int == 0)
                            valor_int = qtd;
                    }
                    // MODELO NORMAL ESCALONADO
                    else if (modelo.equals(getString(R.string.mod_normal))){
                        while(valor == 0 || valor > 1) {
                            valor = rand.nextGaussian();
                            if (valor < 0)
                                valor -= valor;
                        }
                        valor *= qtd;
                        valor_int = (int)Math.ceil(valor);
                    }
                    textRes.setText(String.valueOf(valor_int));
                    // salvando nas configurações
                    SharedPreferences.Editor editor = configs.edit();
                    editor.putInt("sorteio", valor_int);
                    editor.apply();
                }
            }
        });
        bm1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setQtd(-1);
            }
        });
        bp1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setQtd(1);
            }
        });
        bm10.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setQtd(-10);
            }
        });
        bp10.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setQtd(10);
            }
        });
    }

    protected void sorteio(){
        final Handler handler = new Handler();
        final float tempo = configs.getFloat("time", 1.7f);
        bgo.setEnabled(false);
        textRes.setVisibility(View.INVISIBLE);
        if (tempo > 0)
            textLoad.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(Float.valueOf(tempo * 1000).intValue());
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textLoad.setVisibility(View.GONE);
                        textRes.setVisibility(View.VISIBLE);
                        bgo.setEnabled(true);
                    }
                });
            }
        }).start();
    }

    protected int getQtd(){
        String txt = qtd.getText().toString();
        int qtde;
        if(txt.equals(""))
            qtde = configs.getInt("qtde", 0);
        else
            qtde = Integer.parseInt(txt);
        return qtde;
    }

    protected void setQtd(int valor){
        int atual, novo;
        atual = this.getQtd();
        novo = atual + valor;
        if(novo < 0)
            novo = 0;
        qtd.setText(String.valueOf(novo));
        // salvando nas configurações
        SharedPreferences.Editor editor = configs.edit();
        editor.putInt("qtde", novo);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.sobre:
                intent = new Intent(getApplicationContext(), SobreActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.configs:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
