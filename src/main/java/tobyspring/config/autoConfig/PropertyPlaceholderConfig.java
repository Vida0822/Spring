package tobyspring.config.autoConfig;

import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import tobyspring.config.MyAutoConfiguration;

@MyAutoConfiguration
public class PropertyPlaceholderConfig {
    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}
