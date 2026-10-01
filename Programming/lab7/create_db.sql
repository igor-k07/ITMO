CREATE SEQUENCE music_band_id_seq AS INTEGER;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR( 50 ) UNIQUE NOT NULL,
                       password_md5 CHAR(32) NOT NULL
);

CREATE TABLE music_bands (
                               id INTEGER PRIMARY KEY DEFAULT nextval('music_band_id_seq'),

                             name TEXT NOT NULL,

                             coordinates_x BIGINT NOT NULL,
                             coordinates_y BIGINT NOT NULL,

                             creation_date TIMESTAMP WITH TIME ZONE NOT NULL,

                             number_of_participants BIGINT,
                             albums_count BIGINT NOT NULL,
                             genre VARCHAR(50) NOT NULL,

                             best_album_name TEXT NOT NULL,
                             best_album_tracks BIGINT,
                             best_album_length INTEGER,
                             best_album_sales DOUBLE PRECISION NOT NULL,

                             owner_id BIGINT NOT NULL REFERENCES users(id)
);