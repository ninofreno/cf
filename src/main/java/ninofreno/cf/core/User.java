package ninofreno.cf.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class User {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("ratings")
    private final Map<String, Integer> ratings;

    @JsonIgnore
    private double norm;

    public User(final String id) {
        this.id = id;
        this.ratings = new HashMap<>();
        this.norm = 0;
    }

    @JsonCreator
    private User(@JsonProperty("id") final String id, @JsonProperty("ratings") final Map<String, Integer> ratings) {
        this.id = id;
        this.ratings = ratings;
        this.norm = Math.sqrt(ratings.values().stream().mapToDouble(rating -> rating * rating).sum());
    }

    public String getId() {
        return id;
    }

    public Map<String, Integer> getRatings() {
        return Collections.unmodifiableMap(ratings);
    }

    public boolean hasWatched(final String movieId) {
        return ratings.containsKey(movieId);
    }

    public void update(final Event event) {

        Preconditions.checkArgument(event.getUserId().equals(id));
        this.update(event.getMovieId(), event.getRating());
    }

    private void update(final String movieId, final int rating) {

        norm *= norm;
        if (ratings.containsKey(movieId)) {
            int subtrahend = ratings.get(movieId);
            norm -= subtrahend * subtrahend;
        }
        norm += rating * rating;
        norm = Math.sqrt(norm);
        ratings.put(movieId, rating);
    }

    public double cosineSimilarity(final User user) {

        double similarity = 0;
        for (Map.Entry<String, Integer> movieRating : ratings.entrySet()) {
            final String movieId = movieRating.getKey();
            if (user.ratings.containsKey(movieId)) {
                similarity += movieRating.getValue() * user.ratings.get(movieId);
            }
        }
        similarity /= norm * user.norm;

        return similarity;
    }

    public static User fromEvent(final Event event) {

        final User user = new User(event.getUserId());
        user.update(event.getMovieId(), event.getRating());

        return user;
    }

}
