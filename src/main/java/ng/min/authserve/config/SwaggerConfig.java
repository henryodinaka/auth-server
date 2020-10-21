package ng.min.authserve.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
//@Profile({"dev","test"})
@EnableSwagger2
public class SwaggerConfig {

    private static final String AUTHORIZATION = EncryptionHeader.AUTHORIZATION.getName();
    private static final String API_KEY = EncryptionHeader.API_KEY.getName();
    private static final String CLIENT_ID = EncryptionHeader.CLIENT_ID.getName();

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
//                .globalOperationParameters(globalParameterList())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Arrays.asList(/*channelCode(),*/ apiKeyAuthorization(), apiKey(), clientId()))
                /*.securitySchemes(Arrays.asList(apiKeyAuthorization()))*/
                .securityContexts(Arrays.asList(securityContext()));
    }

    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("Payment and Wallet API")
                .description("This is a micro service for wallet management ")
                .version("1.0.0")
                .build();
    }

    private ApiKey apiKeyAuthorization() {
        return new ApiKey(AUTHORIZATION, AUTHORIZATION, "header");
    }

    private ApiKey apiKey() {
        return new ApiKey(API_KEY, API_KEY, "header");
    }

    private ApiKey clientId() {
        return new ApiKey(CLIENT_ID, CLIENT_ID, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
                new SecurityReference(API_KEY, authorizationScopes),
                new SecurityReference(CLIENT_ID, authorizationScopes),
                new SecurityReference(AUTHORIZATION, authorizationScopes));
  }
//    private List<Parameter> globalParameterList() {
//
//        val authTokenHeader =
//                new ParameterBuilder()
//                        .name(AUTHORIZATION) // name of the header
//                        .modelRef(new ModelRef("string")) // data-type of the header
//                        .required(true) // required/optional
//                        .parameterType("header") // for query-param, this value can be 'query'
//                        .description("Access Auth Token")
//                        .build();
//
//        return Collections.singletonList(authTokenHeader);
//    }
}
