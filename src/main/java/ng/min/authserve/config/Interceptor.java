package ng.min.authserve.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;


@Configuration
@EnableWebMvc
@Slf4j
@RequiredArgsConstructor
public class Interceptor implements WebMvcConfigurer, HandlerInterceptor {

//    @Autowired
//    private final RestTemplate restTemplate;
    private final Environment environment;

//    public boolean validateToken(String token, String publicKey, String clientId) throws MinServiceException {
//
//        String oauth2Url = environment.getProperty("app.oauth2.server.url");
//        log.info("::::::::::  URL {}", oauth2Url);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", token);
//        headers.add("ClientId", clientId);
//        headers.add("PublicKey", publicKey);
//
//
//        try {
//            HttpEntity<String> entity = new HttpEntity(headers);
//            log.info(":::::::::: REQUEST Headers {}", entity);
//
//            ResponseEntity<Response> forEntity = restTemplate.exchange(oauth2Url, HttpMethod.GET, entity, Response.class);
//            log.info(":::::::::: RESPONSE   {}", forEntity);
//            if (forEntity == null) {
//                log.info("No Response from the OAUTH2 server");
//                return false;
//            }
//            Response response = forEntity.getBody();
//            if (response == null) {
//                log.info("Response body is null");
//                return false;
//            }
//            if (!"00".equals(response.getCode())) {
//                log.info("Token validation error {}", response);
//                throw new MinServiceException(Integer.valueOf(response.getCode()), response.getMsg(),"06");
//            }
//
//        } catch (Exception ex) {
//            log.error(":::: ERROR while making call to OAUTH2 server {}", oauth2Url, ex);
//            throw new MinServiceException(ResponseCode.UNAVAILABLE1.getCode(), ex.getMessage(), ResponseCode.UNAVAILABLE1.getStatusCode());
//        }
//        return true;
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Requested URL {} from IP Address {} At {}", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        log.info("The incoming header cl {}",request.getHeader("ClientId"));

        if ("OPTIONS".equals(request.getMethod())) return false;

        if (request.getRequestURI().equals("/swagger-ui.html") || request.getRequestURI().equals("/favicon.ico"))
            return true;
        if (request.getRequestURI().contains("/error")) return true;
        if (request.getRequestURI().contains("/paystack/callback")) return true;
        if (request.getRequestURI().contains("/confirm")) return true;

//        try {
//
//            if (!Validation.validData(channelCode)) {
//                log.info("No channel code was passed");
//                response.setStatus(400);
//                response.getWriter().print(new Gson().toJson(new Response(ResponseCode.BAD_REQUEST.getStatusCode(), "Channel code is required", null)));
//
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                response.setHeader(" Access-Control-Allow-Origin", request.getHeader("Origin"));
//                return false;
//            }
//            return true;
//        } catch (IOException e) {
//            log.error("Error ", e);
//            return false;
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("Interceptor postHandle process execution to URL {}  from IP Address {} was completed At {}", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null)
            log.info("Interceptor.afterCompletion after executing request from  {} from IP Address {} was completed At {} Error {}", request.getRequestURI(),request.getRemoteAddr(), LocalDateTime.now(), ex);
        else
            log.info("Interceptor.afterCompletion  after executing request from  {} from IP Address {} was completed At {}", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {
        // default implementation ignored
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {
        // default implementation ignored
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {
        // default implementation ignored
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {
        // default implementation ignored
    }

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        // default implementation ignored
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String[] excludeUrls = environment.getProperty("app.security.exclude-url", String[].class, new String[]{});
        registry.addInterceptor(this)
                .excludePathPatterns(excludeUrls)
                .addPathPatterns("/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/v2/api-docs", "/v2/api-docs");
        registry.addRedirectViewController("/swagger-resources/configuration/ui", "/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/swagger-resources/configuration/security", "/swagger-resources/configuration/security");
        registry.addRedirectViewController("/swagger-resources", "/swagger-resources");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {
        // default implementation ignored
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {
        // default implementation ignored

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {
        // default implementation ignored

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> list) {

        // default implementation ignored
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> list) {

        // default implementation ignored
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

        // default implementation ignored
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

        // default implementation ignored
    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/img/**",
                "/css/**",
                "/js/**")
                .addResourceLocations(
                        "classpath:/META-INF/resources/webjars/",
                        "classpath:/META-INF/resources/",
                        "classpath:/static/img/",
                        "classpath:/static/css/",
                        "classpath:/static/js/");

        registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        // default implementation ignored
    }
}
