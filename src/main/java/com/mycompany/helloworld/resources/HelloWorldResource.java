package com.mycompany.helloworld.resources;

import com.mycompany.helloworld.api.HelloWorld;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
    private final String defaultMessage;

    public HelloWorldResource(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @GET
    @Path("/hey")
    public HelloWorld sayHello() {
        return new HelloWorld(defaultMessage);
    }

    @POST
    @Path("/hey")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HelloWorld replyMessage(HelloWorld content) {
        return content;
    }

    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("my-dropwizard-app");

    @GET
    @Path("/otel")
    public String sayHelloWithOtel() {
        Span parentSpan = tracer.spanBuilder("sayHelloSpan").startSpan();
        try (Scope parentScope = parentSpan.makeCurrent()) {
            // Operation 1
            Span span1 = tracer.spanBuilder("operation-1").setParent(Context.current().with(parentSpan)).startSpan();
            try (Scope scope1 = span1.makeCurrent()) {
                Thread.sleep(1000);

                // Operation 2
                Span span2 = tracer.spanBuilder("operation-2").setParent(Context.current().with(span1)).startSpan();
                try (Scope scope2 = span2.makeCurrent()) {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    span2.end();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                span1.end();
            }

            // Operation 3 (Direct child of parentSpan)
            Span span3 = tracer.spanBuilder("operation-3").setParent(Context.current().with(parentSpan)).startSpan();
            try (Scope scope3 = span3.makeCurrent()) {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                span3.end();
            }

            return "Hello, World!";
        } finally {
            parentSpan.end();
        }
    }
}
