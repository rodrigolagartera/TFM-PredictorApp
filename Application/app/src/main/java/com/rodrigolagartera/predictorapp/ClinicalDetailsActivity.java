package com.rodrigolagartera.predictorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClinicalDetailsActivity extends AppCompatActivity {
    private Spinner gender, tobacco , alcohol;
    private EditText email, age, number_cigarettes, number_alcohol,drugs, comorbidities;

    private Map<String,Object> clinicalDetails;
    private ArrayList photoDetails;
    private String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinical_details);

        email = (EditText) findViewById(R.id.email);
        age = (EditText) findViewById(R.id.age);
        gender = (Spinner) findViewById(R.id.gender);
        tobacco = (Spinner) findViewById(R.id.tobacco);
        number_cigarettes = (EditText) findViewById(R.id.number_cigarettes);
        alcohol = (Spinner) findViewById(R.id.alcohol);
        number_alcohol = (EditText) findViewById(R.id.number_alcohol);
        drugs = (EditText) findViewById(R.id.drugs);
        comorbidities = (EditText) findViewById(R.id.comorbidities);

        String [] options_gender = {"Gender","M", "F"};
        String [] options_tobacco = {"Tobacco","Yes", "No"};
        String [] options_alcohol = {"Alcohol","Yes", "No"};

        ArrayAdapter <String> gender_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_gender);
        ArrayAdapter <String> tobacco_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_tobacco);
        ArrayAdapter <String> alcohol_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_alcohol);

        gender.setAdapter(gender_adapter);
        tobacco.setAdapter(tobacco_adapter);
        alcohol.setAdapter(alcohol_adapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    public void pantallaPhoto(View view){
        Intent i = new Intent(this, PhotoDetailsActivity.class);
        boolean send = true;
        timeStamp=String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        String valor_email = email.getText().toString();
        String valor_age_string = age.getText().toString();
        Integer valor_age_int;
        if (TextUtils.isEmpty(valor_age_string))
            valor_age_int = 0;
        else {
            valor_age_int = Integer.parseInt(valor_age_string);
            if (valor_age_int < 0)
                valor_age_int = 0;
        }
        String valor_gender = gender.getSelectedItem().toString();
        String valor_tobacco = tobacco.getSelectedItem().toString();
        String valor_number_cigarettes_string = number_cigarettes.getText().toString();
        Integer valor_number_cigarettes_int;
        if (TextUtils.isEmpty(valor_number_cigarettes_string))
            valor_number_cigarettes_int = 0;
        else{
            valor_number_cigarettes_int = Integer.parseInt(valor_number_cigarettes_string);
            if (valor_number_cigarettes_int < 0)
                valor_number_cigarettes_int = 0;
        }
        String valor_alcohol = alcohol.getSelectedItem().toString();
        String valor_number_alcohol_string = number_alcohol.getText().toString();
        Integer valor_number_alcohol_int;
        if (TextUtils.isEmpty(valor_number_alcohol_string))
            valor_number_alcohol_int = 0;
        else{
            valor_number_alcohol_int = Integer.parseInt(valor_number_alcohol_string);
            if (valor_number_alcohol_int < 0)
                valor_number_alcohol_int = 0;
        }
        String valor_drugs_string = drugs.getText().toString();
        String valor_comorbidities_string = comorbidities.getText().toString();

        if (valor_tobacco == "No")
            valor_number_cigarettes_int = 0;
        if (valor_alcohol == "No")
            valor_number_alcohol_int = 0;
        /*if (valor_gender.equals("Gender") || valor_tobacco.equals("Tobacco") || valor_alcohol.equals("Alcohol"))
            send = false;*/

        photoDetails = new <Map>ArrayList();

        if (send) {
            clinicalDetails = new HashMap<>();
            clinicalDetails.put("id_timestamp", timeStamp);
            clinicalDetails.put("email", valor_email);
            clinicalDetails.put("age", valor_age_int);
            clinicalDetails.put("gender", valor_gender);
            clinicalDetails.put("tobacco", valor_tobacco);
            clinicalDetails.put("number_cigarettes", valor_number_cigarettes_int);
            clinicalDetails.put("alcohol", valor_alcohol);
            clinicalDetails.put("number_alcohol", valor_number_alcohol_int);
            clinicalDetails.put("drugs", valor_drugs_string);
            clinicalDetails.put("comorbidities", valor_comorbidities_string);
            clinicalDetails.put("list_photo_details", photoDetails);

            ((GlobalData) this.getApplication()).setClinicalDetails(clinicalDetails);

            startActivity(i);
        }
        else
            Toast.makeText(this,"Por favor, revisa que los campos sean correctos antes de continuar", Toast.LENGTH_LONG).show();
    }
}