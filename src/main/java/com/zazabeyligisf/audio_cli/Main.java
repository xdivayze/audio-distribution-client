package com.zazabeyligisf.audio_cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Main {
    static String url = "http://localhost:8080/api/v1/upload";
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();

        byte[] bytes = Files.readAllBytes(Paths.get(args[3]));
        String fileb64 = Base64.getEncoder().encodeToString(bytes);

        TitleLegal titleLegal = TitleLegal.builder()
                .name(args[0])
                .owner(args[1])
                .additions(args[2])
                .build();

        JsonObject payload = new JsonObject();
        payload.addProperty("name", titleLegal.name());
        payload.addProperty("owner", titleLegal.owner());
        payload.addProperty("additions", titleLegal.additions());
        payload.addProperty("mp3file", fileb64);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        StringEntity jsonO = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
        post.setEntity(jsonO);

        CloseableHttpResponse response = client.execute(post);
        HttpEntity responseEntity  = response.getEntity();
        System.out.println(EntityUtils.toString(responseEntity));

    }
}