CREATE TABLE movie (
    id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    genre VARCHAR(255),
    director VARCHAR(255),
    cast_members VARCHAR(255),
    release_year INT NOT NULL,
    rating INT NOT NULL,
    thumbnail_url VARCHAR(255),
    movie_duration_minutes INT NOT NULL,
    video_key VARCHAR(255),
    hls_url VARCHAR(255),
    video_status VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;