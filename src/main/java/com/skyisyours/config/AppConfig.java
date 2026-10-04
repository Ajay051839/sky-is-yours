package com.skyisyours.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Configuration
    public class ModelMapperConfig {

        @Bean
        @Primary // Default bean injected whenever @Qualifier is omitted
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }

        @Bean("ModelMapperWithSkipNull")
        public ModelMapper modelMapperWithSkipNull() {
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setSkipNullEnabled(true);
            return mapper;
        }
    }

}
