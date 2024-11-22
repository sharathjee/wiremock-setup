package com.discover;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import okhttp3.*;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.*;

public class WireMockSampleTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(9090));

    @Test
    public void wiremock_with_junit_test() throws Exception {
        // stub configuration
        configStub();

        // call request in WireMock through OkHttpClient
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // Request for "http://www.google.com/article/12345"
        Request request1 = new Request.Builder()
                .url("http://www.google.com/tweets/12345")
                .method("GET", null)
                .build();
        Response response1 = client.newCall(request1).execute();

        // Request for "http://localhost:9090/article/12345"
        Request request2 = new Request.Builder()
                .url("http://localhost:9090/tweets/12345")
                .method("GET", null)
                .build();
        Response response2 = client.newCall(request2).execute();

        // assert the response
        assertEquals(response1.body().string(), response2.body().string());

    }

    private void configStub() {
        configureFor("localhost", 9090);
        // create a stub
        stubFor(get(urlMatching("/tweets/.*"))
                .willReturn(aResponse().proxiedFrom("http://www.google.com/")));
    }

    private void configStubForPostMethod() {
        configureFor("localhost", 9090);
        stubFor(post(urlEqualTo("/testuser"))
                .willReturn(status(200)
                        .withBody("Welcome TestUser!")
                        .withHeader("content-type", "application/json")));
    }

    @Test
    public void testWiremockPostBody()throws IOException {
        configStubForPostMethod();
        RequestBody body = new FormBody.Builder()
                .add("username", "testuser")
                .build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:9090/testuser")
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        assertEquals("Welcome TestUser!", response.body().string());
    }

    private void configStubForGetMethod() {
        configureFor("localhost", 9090);
        stubFor(get(urlEqualTo("/api/v1/account/1"))
                .willReturn(status(200)
                        .withBody("{\"id\":1,\"name\":\"sharath\",\"location\":\"Hyderabad\"}")
                        .withHeader("content-type", "application/json")));
    }

    @Test
    public void testWiremockGetBody()throws IOException {
        configStubForGetMethod();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:9090/api/v1/account/1")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        assertEquals("{\"id\":1,\"name\":\"sharath\",\"location\":\"Hyderabad\"}", response.body().string());
    }

}
