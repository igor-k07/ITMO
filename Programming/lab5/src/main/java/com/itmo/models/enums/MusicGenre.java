package com.itmo.models.enums;

public enum MusicGenre {
    HIP_HOP,
    BLUES,
    GRUNGE,
    HEAVY_METAL,
    ROCK,
    POST_ROCK,
    HARD_ROCK,
    PSYCHEDELIC_ROCK,
    FUNK_ROCK;


    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var genreType: values()) {
            nameList.append("\n").append(genreType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length()-2);
    }
}
