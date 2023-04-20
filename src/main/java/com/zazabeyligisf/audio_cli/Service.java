package com.zazabeyligisf.audio_cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Log
public class Service {
    private final Gson gson;
    private static final int BUFFER_SIZE = 4096;

    public Service() {
        this.gson = new Gson();
    }

    void pushAudio(String name, Optional<String> owner, Optional<String> additions, Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        String fileb64 = Base64.getEncoder().encodeToString(bytes);

        TitleLegal titleLegal = TitleLegal.builder()
                .name(name)
                .owner(owner.get())
                .additions(additions.get())
                .build();

        JsonObject payload = new JsonObject();
        payload.addProperty("name", titleLegal.name());
        payload.addProperty("owner", titleLegal.owner());
        payload.addProperty("additions", titleLegal.additions());
        payload.addProperty("mp3file", fileb64);

        CloseableHttpResponse response;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(Main.url+"upload");

            StringEntity jsonO = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(jsonO);

            response = client.execute(post);
        }
        HttpEntity responseEntity  = response.getEntity();
        System.out.println(EntityUtils.toString(responseEntity));
    }

    Map<String, String> list(String s) throws URISyntaxException, IOException {
        CloseableHttpResponse response;
        URI uri = new URIBuilder(Main.url+"querify").addParameter("s", s).build();
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(uri);
            response = client.execute(get);
        }

        String responseString = EntityUtils.toString(response.getEntity());
        return Map.of(responseString.split(",")[0], responseString.split(",")[1]);
    }

    void playMusic(String s) throws URISyntaxException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        CloseableHttpResponse response;
        URI uri = new URIBuilder(Main.url+"play").addParameter("s", s).build();
        String responseStr;
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(uri);
            response = client.execute(get);
            responseStr= EntityUtils.toString(response.getEntity());
        }

        byte[] audioBytes = Base64.getDecoder().decode(responseStr);
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bais);

        AudioFormat af = ais.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,af);
        SourceDataLine sdline = (SourceDataLine) AudioSystem.getLine(info);
        sdline.open(af);

        sdline.start();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while((bytesRead = ais.read(buffer, 0, buffer.length)) != -1) {
            sdline.write(buffer,0,bytesRead);
        }

        sdline.drain();
        sdline.close();
    }
}
