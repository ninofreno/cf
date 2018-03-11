package ninofreno.cf.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import info.debatty.java.lsh.LSHSuperBit;
import ninofreno.cf.util.IterateUtil;
import ninofreno.cf.util.MovieLensUtil;

public class LSHCFModel extends CFModel {

    private final int lastStageIndex;
    private final int numOfDimensions;
    private final LSHSuperBit lsh;
    private final Map<String, Integer> users2buckets;
    private final List<Set<User>> buckets;

    public LSHCFModel(final int numOfNeighbors, final int numOfStages, final int numOfBuckets,
            final int numOfDimensions) {

        super(numOfNeighbors);
        LOGGER.info("Initializing LSH model (# stages = {}, # buckets = {}, # dimensions = {})", numOfStages,
                numOfBuckets, numOfDimensions);
        this.lastStageIndex = numOfStages - 1;
        this.numOfDimensions = numOfDimensions;
        this.lsh = new LSHSuperBit(numOfStages, numOfBuckets, numOfDimensions);
        this.users2buckets = new ConcurrentHashMap<>();
        this.buckets = IntStream.range(0, numOfBuckets).mapToObj(i -> new HashSet<User>()).collect(Collectors.toList());
    }

    public Integer getBucket(final String userId) {
        return users2buckets.get(userId);
    }

    @Override
    public void update(final Collection<Event> events) {

        LOGGER.info("Ingesting training data into LSH-CF model...");
        events.parallelStream().forEach(super::update);
        LOGGER.info("Assigning users to buckets (this may take a while)...");
        events.parallelStream().forEach(this::updateBuckets);
    }

    @Override
    public void update(final Event event) {

        super.update(event);
        this.updateBuckets(event);
    }

    private void updateBuckets(final Event event) {

        final String userId = event.getUserId();
        final User user = this.getUser(userId).get();
        if (users2buckets.containsKey(userId)) {
            buckets.get(users2buckets.get(userId)).remove(user);
        }
        final int newBucket = lsh.hash(MovieLensUtil.toDenseVector(user.getRatings(), numOfDimensions))[lastStageIndex];
        users2buckets.put(userId, newBucket);
        buckets.get(newBucket).add(user);
    }

    @Override
    protected Iterable<User> getCandidateNeighbors(final User user) {

        return IterateUtil.filter(buckets.get(users2buckets.get(user.getId())),
                candidateNeighbor -> !user.getRatings().keySet().containsAll(candidateNeighbor.getRatings().keySet()));
    }

}
