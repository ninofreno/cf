# CF

RESTful implementation of collaborative filtering using lazy nearest neighbor computation (exact search) or locality sensitive hashing (fast, approximate nearest neighbor search).

## Build

To build the project:

    mvn clean install

## Run

To run the application (locally):

    java -Xmx4G -jar <full path of cf-0.0.1-SNAPSHOT.jar> --cf.trainingFile=<full path of ratings.dat> [--servlet.port=10080] [--cf.numOfNeighbors=1] [--cf.useLSH=false] [--lsh.numOfStages=4] [--lsh.numOfBuckets=10] [--lsh.numOfDimensions=100]
    
`<ratings.dat>` is the movie rating file, in the format provided by the MovieLens 1M dataset: this argument is required, because the model is initialized with the MovieLens data. If you don't want to download the full MovieLens 1M data, a small sample from them is provided under `src/main/resources/examples`. The following parameters are optional: `servlet.port` is the http port the service will listen to; `cf.numOfNeighbors` is the number of nearest neighbors used to compute recommendations for a given user; `cf.useLSH` determines whether to use plain CF or LSH (default is `false`); the remaining arguments are parameters used to estimate the LSH model (default values are probably ideal for a small-scale demonstration).

## Examples:

To play with the app, you can try to (edit and) run the example commands contained in the bash scripts in `src/main/resources/examples`. In a Unix bash, you can e.g. do the following to retrieve the ratings of user "1" (assuming you are running the service locally on port 10080):

    curl -s 'http://localhost:10080/user?id=1' | jq -S .

`jq` will simply render JSON-formatted console output in a more friendly way. Equivalently, you can access the URL from a browser (a JSON-formatting extension is highly recomended to enhance readability). To request *k* recommendations for user "2", visit the following URL:

    http://localhost:10080/top-k?userId=2&k=10

You can also update the ratings for an existing user and add ratings for new users. The following POST request will push data for a new user (named "john"):

    curl -sH "Content-Type: application/json" -X POST -d '{"userId":"john","movieId":"the blues brothers","rating":5}' 'http://localhost:10080/event'

If you are using LSH for nearest-neighbor search (i.e. running the app with option `--cf.useLSH=true`), then you can also query the following endpoint to retrieve the bucket assignment for any known user, e.g. for the freshly added john:

    http://localhost:10080/bucket?userId=john

To make sense of LSH, check e.g. how bucket assignments change when user ratings are updated, and how users in the same buckets tend to have similar rating histories.

