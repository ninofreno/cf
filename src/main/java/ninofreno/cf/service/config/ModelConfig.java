package ninofreno.cf.service.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ninofreno.cf.core.CFModel;
import ninofreno.cf.core.Event;
import ninofreno.cf.core.LSHCFModel;
import ninofreno.cf.util.FileUtil;
import ninofreno.cf.util.MovieLensUtil;

@Configuration
@Import(ServletConfig.class)
public class ModelConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelConfig.class);

    @Value("${cf.trainingFile}")
    private String trainingFile;

    @Value("${cf.numOfNeighbors}")
    private int numOfNeighbors;

    @Value("${cf.useLSH}")
    private boolean useLSH;

    @Value("${lsh.numOfStages}")
    private int numOfLSHStages;

    @Value("${lsh.numOfBuckets}")
    private int numOfLSHBuckets;

    @Value("${lsh.numOfDimensions}")
    private int numOfLSHDimensions;

    @Bean
    public CFModel cfModel() {

        final CFModel model;
        if (useLSH) {
            LOGGER.info("LSH is enabled");
            model = new LSHCFModel(numOfNeighbors, numOfLSHStages, numOfLSHBuckets, numOfLSHDimensions);
        } else {
            LOGGER.info("LSH is disabled (using lazy nearest neighbor computation)");
            model = new CFModel(numOfNeighbors);

        }

        try {
            LOGGER.info("Parsing training data from {}...", trainingFile);
            final List<Event> trainingData = FileUtil.asLines(trainingFile).parallelStream()
                    .filter(line -> !line.isEmpty()).map(MovieLensUtil::fromLine).collect(Collectors.toList());
            LOGGER.info("Training set size: {}", trainingData.size());
            model.update(trainingData);

        } catch (IOException e) {
            LOGGER.error("Exception {} thrown!", e.getClass().getSimpleName(), e);
        }

        return model;
    }

}
