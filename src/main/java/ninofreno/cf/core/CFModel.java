package ninofreno.cf.core;

import static ninofreno.cf.service.config.ServletConfig.JACKSON_WRITER;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;

import ninofreno.cf.util.IterateUtil;

public class CFModel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CFModel.class);

    private final Map<String, User> users;
    protected final int numOfNeighbors;

    public CFModel(final int numOfNeighbors) {

        LOGGER.info("Creating empty CF model (# nearest neighbors = {})", numOfNeighbors);
        users = new ConcurrentHashMap<>();
        this.numOfNeighbors = numOfNeighbors;
    }

    public Optional<User> getUser(final String userId) {
        return Optional.fromNullable(users.get(userId));
    }

    public void update(final Collection<Event> events) {
        LOGGER.info("Ingesting training data into CF model...");
        events.parallelStream().forEach(this::update);
    }

    public void update(final Event event) {

        try {
            final String userId = event.getUserId();
            if (users.containsKey(userId)) {
                users.get(userId).update(event);
            } else {
                final User user = User.fromEvent(event);
                users.put(userId, user);
            }
            LOGGER.debug("Ingested event: {}", JACKSON_WRITER.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            LOGGER.error("Exception thrown!", e);
        }
    }

    public List<String> getRecommendations(final String userId, final int k) {
        return this.getRecommendations(userId, k, numOfNeighbors);
    }

    public List<String> getRecommendations(final String userId, final int k, final int numOfNeighbors) {

        if (!users.containsKey(userId)) {
            return Collections.emptyList();
        }
        final User user = users.get(userId);
        final List<User> neighbors = this.getNearestNeighbors(user, numOfNeighbors);
        final Map<String, Double> movieScores = this.getAverageRatings(neighbors);
        final Iterable<String> candidateMovies = movieScores.keySet().stream().filter(movie -> !user.hasWatched(movie))
                .collect(Collectors.toSet());

        return new MovieComparator(movieScores).leastOf(candidateMovies, k);
    }

    protected Iterable<User> getCandidateNeighbors(final User user) {
        return IterateUtil.filter(users.values(),
                candidateNeighbor -> !user.getRatings().keySet().containsAll(candidateNeighbor.getRatings().keySet()));
    }

    private List<User> getNearestNeighbors(final User user, final int k) {

        return new UserComparator(user).leastOf(this.getCandidateNeighbors(user), k);
    }

    private Map<String, Double> getAverageRatings(final Collection<User> neighbors) {

        final double normalizationFactor = 1.0 / neighbors.size();

        return neighbors.stream().map(neighbor -> neighbor.getRatings()).reduce((ratings1, ratings2) -> {
            final Map<String, Integer> sums = new HashMap<>(ratings1);
            ratings2.entrySet().forEach(entry -> {
                final String movieId = entry.getKey();
                sums.put(movieId, sums.getOrDefault(movieId, 0) + entry.getValue());
            });
            return sums;
        }).get().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> normalizationFactor * entry.getValue()));
    }

    private static class UserComparator extends Ordering<User> {

        private final User user;

        private UserComparator(final User user) {

            this.user = user;

        }

        @Override
        public int compare(final User candidate1, final User candidate2) {

            return Double.compare(user.cosineSimilarity(candidate2), user.cosineSimilarity(candidate1));

        }

    }

    private static class MovieComparator extends Ordering<String> {

        private final Map<String, Double> movieScores;

        private MovieComparator(final Map<String, Double> movieScores) {

            this.movieScores = movieScores;

        }

        @Override
        public int compare(final String candidate1, final String candidate2) {

            return movieScores.getOrDefault(candidate2, -Double.MAX_VALUE)
                    .compareTo(movieScores.getOrDefault(candidate1, -Double.MAX_VALUE));

        }

    }

}
