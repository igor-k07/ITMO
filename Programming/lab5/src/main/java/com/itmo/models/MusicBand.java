package models;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Модель музыкальной группы.
 */
public class MusicBand extends Entity {
    private Integer id; // генерируется автоматически
    private String name; // не null, не пустая
    private Coordinates coordinates; // не null
    private ZonedDateTime creationDate; // генерируется автоматически
    private Long numberOfParticipants; // может быть null, >0
    private Long albumsCount; // не null, >0
    private MusicGenre genre; // не null
    private Album bestAlbum; // не null

    public MusicBand() {
        this.creationDate = ZonedDateTime.now();
    }

    public MusicBand(String name, Coordinates coordinates, Long numberOfParticipants, Long albumsCount, MusicGenre genre, Album bestAlbum) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        this.numberOfParticipants = numberOfParticipants;
        this.albumsCount = albumsCount;
        this.genre = genre;
        this.bestAlbum = bestAlbum;
    }

    @Override
    public int getId() {
        return id == null ? 0 : id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public Long getNumberOfParticipants() { return numberOfParticipants; }
    public Long getAlbumsCount() { return albumsCount; }
    public MusicGenre getGenre() { return genre; }
    public Album getBestAlbum() { return bestAlbum; }

    @Override
    public boolean validate() {
        // id may be generated automatically on load/insert, so don't require it here
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (numberOfParticipants != null && numberOfParticipants <= 0) return false;
        if (albumsCount == null || albumsCount <= 0) return false;
        if (genre == null) return false;
        if (bestAlbum == null || !bestAlbum.validate()) return false;
        return true;
    }

    @Override
    public void update(Entity newEntity) {
        if (!(newEntity instanceof MusicBand)) return;
        MusicBand other = (MusicBand) newEntity;
        this.name = other.name;
        this.coordinates = other.coordinates;
        this.numberOfParticipants = other.numberOfParticipants;
        this.albumsCount = other.albumsCount;
        this.genre = other.genre;
        this.bestAlbum = other.bestAlbum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicBand that = (MusicBand) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(coordinates, that.coordinates) && Objects.equals(creationDate, that.creationDate) && Objects.equals(numberOfParticipants, that.numberOfParticipants) && Objects.equals(albumsCount, that.albumsCount) && genre == that.genre && Objects.equals(bestAlbum, that.bestAlbum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, numberOfParticipants, albumsCount, genre, bestAlbum);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("id=%d\n", getId()));
        sb.append(String.format("creationDate=%s\n", creationDate));
        sb.append(String.format("name=%s\n", name));
        sb.append(String.format("coordinates=%s\n", coordinates));
        sb.append(String.format("numberOfParticipants=%s\n", numberOfParticipants));
        sb.append(String.format("albumsCount=%s\n", albumsCount));
        sb.append(String.format("genre=%s\n", genre));
        sb.append(String.format("bestAlbum=%s\n", bestAlbum));
        return sb.toString();
    }

    @Override
    protected Comparator<Entity> getComparator() {
        return Comparator.comparing((Entity e) -> ((MusicBand)e).getName(), Comparator.nullsLast(String::compareTo));
    }
}
