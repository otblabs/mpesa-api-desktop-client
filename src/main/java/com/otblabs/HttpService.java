package com.otblabs;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpService {


    public static  <T> T sendSingleResponseRequest(Class<T> responseType, HttpClient httpClient, HttpRequest httpRequest) throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), responseType);
    }

    public <T> List<T> sendListResponseRequest(String url, Class<T> responseType, HttpClient httpClient, HttpRequest httpRequest) throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, responseType));
    }



}
