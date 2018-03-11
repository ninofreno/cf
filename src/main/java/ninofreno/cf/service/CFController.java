package ninofreno.cf.service;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import ninofreno.cf.core.CFModel;
import ninofreno.cf.core.Event;
import ninofreno.cf.core.LSHCFModel;
import ninofreno.cf.core.User;

@RestController
public class CFController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CFController.class);

    @Autowired
    private CFModel cfModel;

    @Autowired
    private ObjectWriter JACKSON_WRITER;

    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public ResponseEntity<Event> event(@RequestBody final Event event) {

        LOGGER.debug("Received request at /event");
        try {
            cfModel.update(event);
            LOGGER.debug("Ingested event {}", JACKSON_WRITER.writeValueAsString(event));
        } catch (IllegalArgumentException | JsonProcessingException e) {
            LOGGER.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<User> user(@RequestParam("id") final String userId) {

        LOGGER.debug("Received request at /user");
        try {
            return new ResponseEntity<>(cfModel.getUser(userId).orNull(), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Exception {} thrown!", e.getClass().getSimpleName(), e);
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @RequestMapping("/top-k")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<List<String>> topK(@RequestParam("userId") final String userId,
            @RequestParam("k") final Integer k) {

        LOGGER.debug("Received request at /top-k");
        try {
            final List<String> recommendations = cfModel.getRecommendations(userId, k);
            if (recommendations.isEmpty()) {
                LOGGER.warn("No recommendations available for user {}", userId);
            }
            return new ResponseEntity<>(recommendations, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Exception {} thrown!", e.getClass().getSimpleName(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @RequestMapping("/bucket")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Integer> bucket(@RequestParam("userId") final String userId) {

        LOGGER.debug("Received request at /bucket");
        if (cfModel instanceof LSHCFModel) {
            return new ResponseEntity<>(((LSHCFModel) cfModel).getBucket(userId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/")
    @ResponseBody
    public ResponseEntity<String> root() {

        LOGGER.debug("Received request at /");
        return new ResponseEntity<>("ninofreno rulez!", HttpStatus.OK);
    }

}
