package ninofreno.cf.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Event {

    @JsonProperty("user")
    private final String userId;

    @JsonProperty("movie")
    private final String movieId;

    @JsonProperty("rating")
    private final int rating;

    @JsonCreator
    public Event(@JsonProperty("userId") String userId, @JsonProperty("movieId") String movieId,
            @JsonProperty("rating") int rating) {

        Preconditions.checkArgument(rating >= 1 && rating <= 5);
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public int getRating() {
        return rating;
    }

}
