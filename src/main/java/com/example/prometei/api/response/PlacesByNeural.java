package com.example.prometei.api.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PlacesByNeural {
    private String city;
    private String airport;
    private String address;
    @SerializedName("name_ru")
    private String namePlace;
}
