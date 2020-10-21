package ng.min.authserve;

import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class AuthServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServeApplication.class, args);
    }


    @Bean
    public RestTemplate getResTemplate(){
        return new RestTemplate(clientHttpRequestFactory());
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectionRequestTimeout(50000);
        clientHttpRequestFactory.setReadTimeout(180000);
        clientHttpRequestFactory.setConnectTimeout(40000);
        clientHttpRequestFactory.setHttpClient(HttpClientBuilder.create().build());
        return clientHttpRequestFactory;
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/swagger"), req ->
                ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build());
    }
}
