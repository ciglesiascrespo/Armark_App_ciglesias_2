package com.feedhenry.armark;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHCloudRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RecordarPassword extends AppCompatActivity {

    private Button BtnRegresar;
    private TextView TxtCorreo;
    private Button BtnBack;

    private View mProgressView;
    private View mLoginFormView;

    private static final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordar_password);

        BtnRegresar = (Button) findViewById(R.id.btn_reset_password);
        TxtCorreo = (TextView) findViewById(R.id.email);
        BtnBack = (Button) findViewById(R.id.btn_back);
        mLoginFormView = findViewById(R.id.form_recuperar);
        mProgressView = findViewById(R.id.progressBar);


        BtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TxtCorreo.getText().toString().length() > 0){
                    cloudCallRecuperar(v, TxtCorreo.getText().toString());
                    //Toast.makeText(v.getContext(), "La información fue enviada al correo [ " + TxtCorreo.getText().toString() + " ] ", Toast.LENGTH_LONG).show();
                    //VolverLimpiar();
                }
            }
        });
        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolverLimpiar();
            }
        });
    }

    private void VolverLimpiar(){
        goLogin();
        TxtCorreo.setText("");
    }

    public void cloudCallRecuperar(final View v, final String Usuario) {
        try {

            mProgressView.setVisibility(View.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);

            org.json.fh.JSONObject params = new org.json.fh.JSONObject("{correo: '"+ Usuario +"' }");

            FHCloudRequest request = FH.buildCloudRequest("recuperar", "POST", null, params);
            request.executeAsync(new FHActCallback() {
                @Override
                public void success(FHResponse fhResponse) {
                    Log.d(TAG, "cloudCall - success");
                    v.setEnabled(true);

                    try {
                        JSONObject obj = new JSONObject(fhResponse.getJson().toString());

                        org.json.JSONObject jsonRespuesta = obj.getJSONObject("Respuesta");

                        String Estado =jsonRespuesta.getString("Estado");
                        String Mensaje =jsonRespuesta.getString("Mensaje");

                        if(Estado.equals("OK")){
                            Toast.makeText(v.getContext(), Mensaje, Toast.LENGTH_LONG).show();
                            esperarYCerrar(2000);
                        }else{
                            Toast.makeText(v.getContext(), "Correo no registrado!", Toast.LENGTH_LONG).show();
                            Limpiar();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(v.getContext(), "No se pudo recuperar la contraseña, error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Limpiar();
                    }

                }

                @Override
                public void fail(FHResponse fhResponse) {
                    Log.d(TAG, "cloudCall - fail");
                    Log.e(TAG, fhResponse.getErrorMessage(), fhResponse.getError());
                    v.setEnabled(true);
                    Toast.makeText(v.getContext(), "No se pudo recuperar la contraseña, error: " + fhResponse.getErrorMessage(), Toast.LENGTH_LONG).show();
                    Limpiar();
                }
            });
        } catch (Exception e) {
            Toast.makeText(v.getContext(), "No se pudo recuperar la contraseña, error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage(), e.getCause());
            Limpiar();
        }
    }

    private void goLogin() {
        Intent intent = new Intent(this,Loggin.class);
        startActivity(intent);
    }

    private void Limpiar(){
        TxtCorreo.setText("");
        TxtCorreo.requestFocus();
        mProgressView.setVisibility(View.GONE);
        mLoginFormView.setVisibility(View.VISIBLE);
    }

    public void esperarYCerrar(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                VolverLimpiar();
            }
        }, milisegundos);
    }
}
