package com.eduardo.brownianmonkey;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast toast = Toast.makeText(getApplicationContext(), R.string.s_aviso, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 300);
        toast.show();
        final Random rand = new Random();

        // Registrando elementos
        bm1 = findViewById(R.id.bm1);
        bp1 = findViewById(R.id.bp1);
        bm10 = findViewById(R.id.bm10);
        bp10 = findViewById(R.id.bp10);
        bgo = findViewById(R.id.bgo);
        qtd = findViewById(R.id.qtde);
        textRes = findViewById(R.id.textRes);
        textLoad = findViewById(R.id.textLoad);

        // Eventos
        bgo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int qtd = getQtd();
                double valor = 0;
                int valor_int;
                if(qtd < 2) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.s_min, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, -100);
                    toast.show();
                } else {
                    sorteio();
                    while(valor == 0 || valor > 1) {
                        valor = rand.nextGaussian();
                        if (valor < 0)
                            valor -= valor;
                    }
                    valor *= qtd;
                    valor_int = (int)Math.ceil(valor);
                    textRes.setText(String.valueOf(valor_int));
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
        bgo.setEnabled(false);
        textRes.setVisibility(View.INVISIBLE);
        textLoad.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1700);
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
            qtde = 0;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sobre:
                Intent intent = new Intent(getApplicationContext(), SobreActivity.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
