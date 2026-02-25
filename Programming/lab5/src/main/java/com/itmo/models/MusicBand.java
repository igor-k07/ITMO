package com.itmo.models;

import com.itmo.models.enums.MusicGenre;
import com.itmo.utility.abstracted.Element;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class MusicBand extends Element implements Serializable {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long numberOfParticipants; //Поле может быть null, Значение поля должно быть больше 0
    private Long albumsCount; //Поле не может быть null, Значение поля должно быть больше 0
    private MusicGenre genre; //Поле не может быть null
    private Album bestAlbum; //Поле не может быть null

    public MusicBand(Integer id, String name, Coordinates coordinates,
                     ZonedDateTime creationDate, Long numberOfParticipants,
                     Long albumsCount, MusicGenre genre, Album bestAlbum){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.numberOfParticipants = numberOfParticipants;
        this.albumsCount = albumsCount;
        this.genre = genre;
        this.bestAlbum = bestAlbum;
    }

    public MusicBand(Integer id, String name, Coordinates coordinates,
                     Long numberOfParticipants, Long albumsCount,
                     MusicGenre genre, Album bestAlbum){
        this(id, name, coordinates, ZonedDateTime.now(), numberOfParticipants, albumsCount, genre, bestAlbum);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public Long getAlbumsCount() {
        return albumsCount;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Album getBestAlbum() {
        return bestAlbum;
    }


    @Override
    public int compareTo(Element element){
        return (int)(this.id - element.getId());

    }

    @Override
    public boolean validate(){
        if (id == null || id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (numberOfParticipants <= 0) return false;
        if (albumsCount == null || albumsCount <= 0) return false;
        if (genre == null) return false;
        if (bestAlbum == null || !bestAlbum.validate()) return false;
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MusicBand that = (MusicBand) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate,
                numberOfParticipants, albumsCount, genre, bestAlbum);
    }

    @Override
    public String toString(){
        return name;
    }
}
