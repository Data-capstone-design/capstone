package com.technote.client.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import java.util.List;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@EnableFeignClients
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    public Decoder feignDecoder() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Configure ObjectMapper if needed
        return new ResponseEntityDecoder(new SpringDecoder(() -> new HttpMessageConverters(
                new MappingJackson2HttpMessageConverter(objectMapper) {{
                    setSupportedMediaTypes(List.of(
                            MediaType.APPLICATION_JSON,
                            MediaType.valueOf("text/javascript;charset=utf-8")
                    ));
                }}
        )));
    }
}


//    @Bean
//    public Decoder feignDecoder() {
//        return new SpringDecoder(() -> new ObjectMapper());
//    }
//    https://noembed.com/embed?url=https://www.youtube.com/watch?v=h30k7YixrMo