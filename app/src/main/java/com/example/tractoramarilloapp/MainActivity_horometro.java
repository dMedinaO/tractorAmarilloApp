package com.example.tractoramarilloapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.tractoramarilloapp.InternetStatus.isOnline;

public class MainActivity_horometro extends AppCompatActivity {

    private ImageView imageComentario;
    private ImageView imageSync,imageSignal;
    private TextView tituloHorometro,textComentario;
    private EditText inputHorometro;
    private Button buttonHorometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horometro);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_action_bar);

        View customActionBarView = actionBar.getCustomView();

        inputHorometro = (EditText) findViewById(R.id.inputHorometro);
        buttonHorometro = (Button) findViewById(R.id.buttonHorometro);
        tituloHorometro = (TextView) findViewById(R.id.textHorometro);
        imageSignal = (ImageView) findViewById(R.id.imageSignal);
        imageSync = (ImageView) findViewById(R.id.imageSync);

        // botones action bar
        imageComentario = (ImageView) findViewById(R.id.imageComentario);
        textComentario = (TextView) findViewById(R.id.textComentarioLink);
        imageComentario.setColorFilter(Color.rgb(206, 206, 206));
        textComentario.setTextColor(Color.rgb(206, 206, 206));

        SharedPreferences prefs =
                getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        final Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            Log.e("TAG HOROMETRO","valor: "+bundle.getString("flagHorometro"));

            if (bundle.getString("flagHorometro").equalsIgnoreCase("1")){
                tituloHorometro.setText(R.string.horometro_inicio);
            }else if(bundle.getString("flagHorometro").equalsIgnoreCase("2") || bundle.getString("flagHorometro").equalsIgnoreCase("3")){
                tituloHorometro.setText(R.string.horometro_fin);
            }

        }


        buttonHorometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity_horometro.this,"Horómetro: "+inputHorometro.getText().toString(),Toast.LENGTH_SHORT).show();

                if (inputHorometro.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(MainActivity_horometro.this,"Debe ingresar horómetro para continuar.", Toast.LENGTH_LONG).show();
                }else{

                    // INICIO DE HOROMETRO
                    if (bundle.getString("flagHorometro").equalsIgnoreCase("1")){
                        editor.putString("inicio_horometro",inputHorometro.getText().toString());
                        editor.commit();

                        Intent intent = new Intent(MainActivity_horometro.this,MainActivity_implemento.class);
                        startActivity(intent);
                        finish();

                    }
                    // FIN DE HOROMETRO
                    if (bundle.getString("flagHorometro").equalsIgnoreCase("2")){
                        editor.putString("fin_horometro",inputHorometro.getText().toString());
                        Intent output = new Intent();
                        //output.putExtra("horometro", inputHorometro.getText().toString());
                        setResult(RESULT_OK, output);
                        finish();
                    }
                    // FIN DE HOROMETRO FORZADO
                    if (bundle.getString("flagHorometro").equalsIgnoreCase("3")){

                        editor.putString("fin_horometro",inputHorometro.getText().toString());
                        editor.commit();

                        Intent intent = new Intent(MainActivity_horometro.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        });

        // CHECK INTERNET CONNECTION
        if(isOnline(getApplicationContext())){
            imageSignal.setImageResource(R.mipmap.signal);
        }else{
            imageSignal.setImageResource(R.mipmap.signal_off);
        }

    }

    @Override
    public void onBackPressed() { }


}
