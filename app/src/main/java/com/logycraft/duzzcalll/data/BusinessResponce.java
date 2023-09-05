package com.logycraft.duzzcalll.data;



import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BusinessResponce implements Serializable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("lines")
    @Expose
    private List<Line> lines;
    private final static long serialVersionUID = 1470759806049909854L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }
    public class Line implements Serializable
    {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("extension")
        @Expose
        private String extension;
        private final static long serialVersionUID = -138817947905584944L;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

    }
}

