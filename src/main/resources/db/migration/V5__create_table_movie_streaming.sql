CREATE TABLE movie_streaming (
    movie_id INTEGER,
    streaming_id INTEGER,
    constraint fk_movie_streaming_movie FOREIGN KEY(movie_id) REFERENCES movie(id),
    constraint fk_movie_streaming_streaming FOREIGN KEY(streaming_id) REFERENCES streaming(id)
);