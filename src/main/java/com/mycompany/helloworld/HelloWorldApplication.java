package com.mycompany.helloworld;

import com.mycompany.helloworld.health.ConfigHealthCheck;
import com.mycompany.helloworld.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.api.common.Attributes;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return "helloworld";
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) {

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getDefaultMessage()
        );

        final ConfigHealthCheck healthCheck;
        healthCheck = new ConfigHealthCheck(configuration.getDefaultMessage());

        environment.healthChecks().register("defaultMessage", healthCheck);
        environment.jersey().register(resource);
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        // Initialize OpenTelemetry
        initOpenTelemetry();
    }

    private void initOpenTelemetry() {
        // Create a Resource with the service name attribute
        Resource serviceNameResource = Resource.create(Attributes.builder().put("service.name", "dropwizard-otel").build());

        // Configure the Jaeger exporter
        JaegerGrpcSpanExporter jaegerExporter = configureJaegerExporter();

        // Configure the SDK Tracer Provider
        SdkTracerProvider tracerProvider = configureSdkTracerProvider(jaegerExporter, serviceNameResource);

        // Register the OpenTelemetry SDK as the global instance
        OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).buildAndRegisterGlobal();

        // Optionally, set a global tracer for use in your application code
        GlobalOpenTelemetry.getTracer("my-application");
    }

    private JaegerGrpcSpanExporter configureJaegerExporter() {
        return JaegerGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:14250") // Default Jaeger port
            .build();
    }

    private SdkTracerProvider configureSdkTracerProvider(JaegerGrpcSpanExporter jaegerExporter, Resource serviceNameResource) {
        return SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(jaegerExporter))
            .setResource(Resource.getDefault().merge(serviceNameResource))
            .build();
    }
}
