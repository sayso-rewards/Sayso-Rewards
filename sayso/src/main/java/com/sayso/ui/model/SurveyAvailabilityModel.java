package com.sayso.ui.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyAvailabilityModel {

    @SerializedName("rid")
    @Expose
    private String rid;
    @SerializedName("surveysAvailable")
    @Expose
    private Boolean surveysAvailable;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Boolean getSurveysAvailable() {
        return surveysAvailable;
    }

    public void setSurveysAvailable(Boolean surveysAvailable) {
        this.surveysAvailable = surveysAvailable;
    }


}
