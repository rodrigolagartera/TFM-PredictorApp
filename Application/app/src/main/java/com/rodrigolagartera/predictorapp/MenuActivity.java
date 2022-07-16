package com.rodrigolagartera.predictorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {
    private RequestQueue queue;
    private Map<String, Object> clinicalDetails, prediction;
    private ArrayList photoDetails;
    TextView showpredi, showacc;
    Dialog popupLegal;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        showpredi = (TextView) findViewById(R.id.predictionView);
        showacc = (TextView) findViewById(R.id.accuracyView);
        popupLegal = new Dialog(this);
    }

    public void prediccionCancer (View view) throws JSONException {
        String url = "http://192.168.1.35:80/imagePredict";
        queue = Volley.newRequestQueue(this);
        Map<String, Object> currentPhoto;
        String img="", timeStamp="";
        JSONArray jsonArray = new JSONArray();


        clinicalDetails = ((GlobalData) this.getApplication()).getClinicalDetails();
        timeStamp = (String) clinicalDetails.get("id_timestamp");
        photoDetails = (ArrayList) clinicalDetails.get("list_photo_details");
        for (int i=0; i< photoDetails.size(); i++){
            currentPhoto = (Map<String, Object>) photoDetails.get(i);
            img = (String) currentPhoto.get("img");
            JSONObject jsonImg = new JSONObject();
            jsonImg.put("img", img);
            jsonArray.put(jsonImg);
        }
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("imgArray",jsonArray);

        String finalTimeStamp = timeStamp;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("LOG_VOLLEY", response.toString());
                double prediccion, porcentaje;
                String diagnose = null;
                String comentarios = "";

                try {
                    prediccion = response.getDouble("prediction");
                    porcentaje = response.getDouble("accuracy");
                    if (prediccion < 0.5 && prediccion >=0){
                        diagnose= "Benigno";
                    }
                    else if(prediccion < 1.5 && prediccion >=0.5){
                        diagnose= "Maligno";
                    }
                    else if(prediccion >=1.5 && prediccion <=2){
                        diagnose= "Potencialmente maligno";
                    }
                    else{
                        diagnose= "PREDICCIÓN NO VÁLIDA";
                    }
                    Log.i("LOG_PRED", diagnose);
                    Log.i("LOG_ACC", String.valueOf(porcentaje));
                    Toast.makeText(getApplicationContext(),"Diagnostico: " + diagnose + " con un "+String.valueOf(porcentaje) + "% de acierto",Toast.LENGTH_LONG);
                    showpredi.setText(diagnose);
                    showacc.setText(String.valueOf(porcentaje));

                    prediction = new HashMap<>();
                    prediction.put("id_timestamp", finalTimeStamp);
                    prediction.put("diagnose", diagnose);
                    prediction.put("percentage", porcentaje);
                    prediction.put("comments", comentarios);
                    db.collection("predictions").document(finalTimeStamp).set(prediction)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("SUCCESS", "DocumentSnapshot added with ID: " + finalTimeStamp);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("FAILURE", "Error adding document", e);
                                }
                            });
                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY_ERROR", error.toString());
                Toast.makeText(getApplicationContext(),"Error al ejecutar peticion: " + error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObjectRequest);

    }

    public void guardarDatos(View view){
        Button aceptar,rechazar;
        popupLegal.setContentView(R.layout.popup_legal_datos);
        aceptar=(Button) popupLegal.findViewById(R.id.aceptarLegal);
        rechazar=(Button) popupLegal.findViewById(R.id.rechazarLegal);
        clinicalDetails = ((GlobalData) this.getApplication()).getClinicalDetails();
        String timeStamp = (String) clinicalDetails.get("id_timestamp");

        rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLegal.dismiss();
            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("clinicalDetails").document(timeStamp).set(clinicalDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SUCCESS", "DocumentSnapshot added with ID: " + timeStamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FAILURE", "Error adding document", e);
                    }
                });

                popupLegal.dismiss();
            }
        });
        popupLegal.show();

    }


    public void volverPaginaPrincipal(View view){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}