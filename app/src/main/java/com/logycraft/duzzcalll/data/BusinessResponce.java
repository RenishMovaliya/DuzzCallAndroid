package com.logycraft.duzzcalll.data;



import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BusinessResponce implements Serializable
{

    @SerializedName("business_name")
    String businessName;

    @SerializedName("business_logo")
    String businessLogo;

    @SerializedName("line_name")
    String lineName;

    @SerializedName("line_extension")
    String lineExtension;


    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
    }
    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
    public String getLineName() {
        return lineName;
    }

    public void setLineExtension(String lineExtension) {
        this.lineExtension = lineExtension;
    }
    public String getLineExtension() {
        return lineExtension;
    }

}

