package com.itmo.managers;

import com.itmo.models.Album;
import com.itmo.models.Coordinates;
import com.itmo.models.MusicBand;
import com.itmo.models.enums.MusicGenre;
import com.itmo.models.abstracts.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager {
    private static final String UPDATE_MUSIC_BAND = """
        UPDATE music_bands SET
            name = ?, coordinates_x = ?, coordinates_y = ?, creation_date = ?,
            number_of_participants = ?, albums_count = ?, genre = ?,
            best_album_name = ?, best_album_tracks = ?, best_album_length = ?,
            best_album_sales = ?
        WHERE id = ? AND owner_id = ?
        """;

    private static final String DELETE_MUSIC_BAND =
        "DELETE FROM music_bands WHERE id = ? AND owner_id = ?";

    private static final String CLEAR_OWNED =
        "DELETE FROM music_bands WHERE owner_id = ?";

    private static final String INSERT_MUSIC_BAND = """
        INSERT INTO music_bands (
            name, coordinates_x, coordinates_y, creation_date,
            number_of_participants, albums_count, genre,
            best_album_name, best_album_tracks, best_album_length,
            best_album_sales, owner_id
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;

    private static final String LOAD_COLLECTION = """
        SELECT id, name, coordinates_x, coordinates_y, creation_date,
               number_of_participants, albums_count, genre,
               best_album_name, best_album_tracks, best_album_length,
               best_album_sales, owner_id
        FROM music_bands
        """;

    public Collection<Element> loadCollection() throws SQLException {
        Collection<Element> collection = new ArrayList<>();

        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(LOAD_COLLECTION);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                collection.add(readMusicBand(resultSet));
            }
        }

        return collection;
    }

    public int insertMusicBand(MusicBand band, int ownerId) throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(INSERT_MUSIC_BAND)) {

            statement.setString(1, band.getName());
            statement.setLong(2, band.getCoordinates().getX());
            statement.setLong(3, band.getCoordinates().getY());
            statement.setObject(4, band.getCreationDate().toOffsetDateTime());
            setNullableLong(statement, 5, band.getNumberOfParticipants());
            statement.setLong(6, band.getAlbumsCount());
            statement.setString(7, band.getGenre().name());
            statement.setString(8, band.getBestAlbum().getName());
            setNullableLong(statement, 9, band.getBestAlbum().getTracks());
            setNullableInteger(statement, 10, band.getBestAlbum().getLength());
            statement.setDouble(11, band.getBestAlbum().getSales());
            statement.setInt(12, ownerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("База данных не вернула id нового объекта");
                }
                return resultSet.getInt("id");
            }
        }
    }

    public boolean updateMusicBand(MusicBand band, int ownerId) throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_MUSIC_BAND)) {
            fillMusicBandParameters(statement, band);
            statement.setInt(12, band.getId());
            statement.setInt(13, ownerId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteMusicBand(int id, int ownerId) throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_MUSIC_BAND)) {
            statement.setInt(1, id);
            statement.setInt(2, ownerId);
            return statement.executeUpdate() == 1;
        }
    }

    public int clearOwned(int ownerId) throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(CLEAR_OWNED)) {
            statement.setInt(1, ownerId);
            return statement.executeUpdate();
        }
    }

    public Set<Integer> deleteMusicBands(Collection<Integer> ids, int ownerId) throws SQLException {
        Set<Integer> deletedIds = new HashSet<>();
        for (Integer id : ids) {
            if (deleteMusicBand(id, ownerId)) {
                deletedIds.add(id);
            }
        }
        return deletedIds;
    }

    private MusicBand readMusicBand(ResultSet resultSet) throws SQLException {
        MusicBand band = new MusicBand(
            resultSet.getString("name"),
            new Coordinates(
                resultSet.getLong("coordinates_x"),
                resultSet.getLong("coordinates_y")
            ),
            getNullableLong(resultSet, "number_of_participants"),
            resultSet.getLong("albums_count"),
            MusicGenre.valueOf(resultSet.getString("genre")),
            new Album(
                resultSet.getString("best_album_name"),
                getNullableLong(resultSet, "best_album_tracks"),
                getNullableInteger(resultSet, "best_album_length"),
                resultSet.getDouble("best_album_sales")
            )
        );

        band.setId(resultSet.getInt("id"));
        band.setOwnerId(resultSet.getLong("owner_id"));
        OffsetDateTime creationDate = resultSet.getObject("creation_date", OffsetDateTime.class);
        band.setCreationDate(creationDate.atZoneSameInstant(ZoneOffset.UTC));
        return band;
    }

    private void fillMusicBandParameters(PreparedStatement statement, MusicBand band) throws SQLException {
        statement.setString(1, band.getName());
        statement.setLong(2, band.getCoordinates().getX());
        statement.setLong(3, band.getCoordinates().getY());
        statement.setObject(4, band.getCreationDate().toOffsetDateTime());
        setNullableLong(statement, 5, band.getNumberOfParticipants());
        statement.setLong(6, band.getAlbumsCount());
        statement.setString(7, band.getGenre().name());
        statement.setString(8, band.getBestAlbum().getName());
        setNullableLong(statement, 9, band.getBestAlbum().getTracks());
        setNullableInteger(statement, 10, band.getBestAlbum().getLength());
        statement.setDouble(11, band.getBestAlbum().getSales());
    }

    private Long getNullableLong(ResultSet resultSet, String column) throws SQLException {
        long value = resultSet.getLong(column);
        return resultSet.wasNull() ? null : value;
    }

    private Integer getNullableInteger(ResultSet resultSet, String column) throws SQLException {
        int value = resultSet.getInt(column);
        return resultSet.wasNull() ? null : value;
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}