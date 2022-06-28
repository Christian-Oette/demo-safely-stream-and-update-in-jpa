package com.christianoette.streamjpa.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * {HOST}/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket myApi() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Test Api")
                .description("Api to interact with the demo")
                .version("1.0")
                .build();

        return new Docket(DocumentationType.OAS_30)
                .groupName("Test Api")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .apiInfo(apiInfo)
                .forCodeGeneration(true);
    }

}
