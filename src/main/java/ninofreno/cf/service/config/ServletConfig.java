package ninofreno.cf.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Configuration
public class ServletConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletConfig.class);
    private static final ObjectMapper JACKSON_MAPPER = new ObjectMapper();
    public static final ObjectWriter JACKSON_WRITER = JACKSON_MAPPER.writer();

    @Value("${servlet.port}")
    private int servletPort;

    @Bean
    EmbeddedServletContainerFactory servletContainer() {

        final TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();

        factory.setPort(servletPort);
        LOGGER.info("Set servlet port to {}.", servletPort);

        return factory;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    ObjectMapper objectMapper() {

        return JACKSON_MAPPER;
    }

    @Bean
    ObjectWriter objectWriter() {

        return JACKSON_WRITER;
    }

}
