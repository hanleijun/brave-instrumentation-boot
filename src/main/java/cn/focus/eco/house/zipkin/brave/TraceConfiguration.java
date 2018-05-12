package cn.focus.eco.house.zipkin.brave;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import brave.Tracing;
import brave.context.log4j12.MDCCurrentTraceContext;
import brave.http.HttpTracing;
import brave.httpasyncclient.TracingHttpAsyncClientBuilder;
import brave.httpclient.TracingHttpClientBuilder;
import brave.okhttp3.TracingInterceptor;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import brave.spring.webmvc.TracingHandlerInterceptor;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 *
 * @Author: leijunhan (leijunhan@sohu-inc.com)
 * @Date: 2017/6/21
 */
@Configuration
@Import({ TracingClientHttpRequestInterceptor.class, TracingHandlerInterceptor.class })
public class TraceConfiguration extends WebMvcConfigurerAdapter {

    @Value("${zipkin.traceHost}")
    private String traceHost;
    @Value("${zipkin.serviceName}")
    private String serviceName;

    /**
     * Configuration for how to send spans to Zipkin
     */
    @Bean
    public Sender sender() {
        return OkHttpSender.create(traceHost + "/api/v1/spans");
    }

    /**
     * Configuration for how to buffer spans into messages for Zipkin
     */
    @Bean
    public Reporter<Span> reporter() {
        return AsyncReporter.builder(sender()).build();
    }

    /**
     * Controls aspects of tracing such as the name that shows up in the UI
     */
    @Bean
    public Tracing tracing() {
        return Tracing.newBuilder()
                .localServiceName(serviceName) // change to your own project name
                .currentTraceContext(MDCCurrentTraceContext.create()) // puts trace IDs into logs
                .reporter(reporter()).build();
    }

    @Bean
    public HttpTracing httpTracing() {
        return HttpTracing.create(tracing());
    }

    @Autowired
    private TracingHandlerInterceptor serverInterceptor;

    @Autowired
    private TracingClientHttpRequestInterceptor clientInterceptor;

    @Autowired
    private HttpTracing httpTracing;

    @Autowired
    private Tracing tracing;

    @Autowired
    private ApplicationContext applicationContext;



    /**
     * adds tracing to the rest template
     */
    @PostConstruct
    public void init() {

        try {
            RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
            if (restTemplate != null) {
                List<ClientHttpRequestInterceptor> interceptors =
                        new ArrayList<>(restTemplate.getInterceptors());
                interceptors.add(clientInterceptor);
                restTemplate.setInterceptors(interceptors);
            }
        }catch (Exception e){

        }

    }

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .dispatcher(dispatcher())
                .addNetworkInterceptor(interceptor())
                .build();
    }

    @Bean
    public Interceptor interceptor(){
        return TracingInterceptor.create(httpTracing);
    }

    @Bean
    public Dispatcher dispatcher(){
        return new Dispatcher(
                httpTracing.tracing().currentTraceContext()
                        .executorService(
                                new Dispatcher().executorService()
                        )
        );
    }

    @Bean
    public HttpClient httpClient(){
        return TracingHttpClientBuilder.create(tracing).build();
    }

    @Bean
    public HttpClientBuilder httpClientBuilder(){
        return TracingHttpClientBuilder.create(tracing);
    }

    @Bean
    public HttpAsyncClient httpAsyncClient(){
        return TracingHttpAsyncClientBuilder.create(tracing).build();
    }

    @Bean
    public HttpAsyncClientBuilder httpAsyncClientBuilder(){
        return TracingHttpAsyncClientBuilder.create(tracing);
    }


    /**
     * adds tracing to the  web controller
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverInterceptor);
    }
}
