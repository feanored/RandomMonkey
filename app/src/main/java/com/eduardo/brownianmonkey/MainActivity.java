package com.eduardo.brownianmonkey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button bm, bp, bgo;
    EditText qtd;
    TextView textRes, textLoad;
    SharedPreferences configs;
    SeekBar seekBar;
    final int LIMITE = 1000000;

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
        bm = findViewById(R.id.bm1);
        bp = findViewById(R.id.bp10);
        bgo = findViewById(R.id.bgo);
        qtd = findViewById(R.id.qtde);
        textRes = findViewById(R.id.textRes);
        textLoad = findViewById(R.id.textLoad);
        seekBar = findViewById(R.id.seekBar);

        // recuperando valores das configurações
        qtd.setText(String.valueOf(configs.getInt("qtde", 0)));
        textRes.setText(String.valueOf(configs.getInt("sorteio", 0)));
        int passo = configs.getInt("step", 0);
        seekBar.setProgress(passo);
        passo = (int) Math.pow(10, passo);
        bm.setText(String.format(getString(R.string.sb_menos), passo));
        bp.setText(String.format(getString(R.string.sb_mais), passo));

        // Eventos
        bgo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int qtd = getQtd();
                double valor = 0;
                int valor_int = 0;
                String modelo = configs.getString("modelo", getString(R.string.mod_unif));
                if(qtd < 2) {
                    aviso(R.string.s_min);
                } else {
                    // valida qtde
                    setQtd(0);
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
                    if (valor_int >= LIMITE)
                        valor_int = LIMITE - 1;
                    textRes.setText(String.valueOf(valor_int));
                    // salvando nas configurações
                    SharedPreferences.Editor editor = configs.edit();
                    editor.putInt("sorteio", valor_int);
                    editor.apply();
                }
            }
        });
        bm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int passo = configs.getInt("step", 0);
                passo = (int) Math.pow(10, passo);
                setQtd(-passo);
            }
        });
        bp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int passo = configs.getInt("step", 0);
                passo = (int) Math.pow(10, passo);
                setQtd(passo);
            }
        });
        qtd.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || event != null &&
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // acabou de digitar, verifica se passou do limite
                        setQtd(0);
                        return false;
                    }
                }
                return false; // passa pra frente
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int passo = (int) Math.pow(10, i);
                bm.setText(String.format(getString(R.string.sb_menos), passo));
                bp.setText(String.format(getString(R.string.sb_mais), passo));

                // salvando passo
                SharedPreferences.Editor editor = configs.edit();
                editor.putInt("step", i);
                editor.apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    protected void aviso(int texto_id){
        Toast toast = Toast.makeText(getApplicationContext(), texto_id, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, -100);
        toast.show();
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
                    System.out.println(e.getMessage());
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
            try {
                qtde = Integer.parseInt(txt);
            }
            catch(Exception e){
                qtde = LIMITE;
                aviso(R.string.s_max);
            }
        return qtde;
    }

    protected void setQtd(int valor){
        int atual, novo;
        atual = this.getQtd();
        novo = atual + valor;
        if(novo < 0)
            novo = 0;
        else if(novo > LIMITE) {
            novo = LIMITE;
            aviso(R.string.s_max);
        }
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
