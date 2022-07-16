package com.rodrigolagartera.predictorapp;

import android.app.Application;

import java.util.Map;

public class GlobalData extends Application {

    private Map<String, Object> clinicalDetails;

    public Map getClinicalDetails() {
        return clinicalDetails;
    }

    public void setClinicalDetails(Map clinicalDetails) {
        this.clinicalDetails = clinicalDetails;
    }






}
