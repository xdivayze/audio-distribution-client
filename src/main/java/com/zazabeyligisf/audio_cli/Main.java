package com.zazabeyligisf.audio_cli;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Main {
    static String url = "http://localhost:8080/api/v1/";
    static Service service = new Service();

    public static void main(String[] args) throws IOException, URISyntaxException, UnsupportedAudioFileException, LineUnavailableException {
        LinkedList<String> argsList = new LinkedList<>(List.of(args));
        switch (args[0]) {
            case "--upload" -> {

                if (!argsList.contains("--name") || !argsList.contains("--owner") || !argsList.contains("--path")) {
                    throw new RuntimeException("required entries not included");
                } else if (argsList.get(argsList.indexOf("--name") + 1).isBlank() || argsList.get(argsList.indexOf("--path") + 1).isBlank()) {
                    throw new RuntimeException("name not included");
                } else if (!argsList.contains("--additions")) {
                    argsList.addLast("--additions");
                    argsList.addLast("");
                }
                service.pushAudio(argsList.get(argsList.indexOf("--name") + 1),
                        Optional.ofNullable(argsList.get(argsList.indexOf("--owner") + 1)),
                        Optional.ofNullable(argsList.get(argsList.indexOf("--additions") + 1)),
                        Path.of(argsList.get(argsList.indexOf("--path") + 1)));
                System.out.println(argsList);
                System.out.println(argsList.get(argsList.indexOf("--name") + 1) +
                        Optional.ofNullable(argsList.get(argsList.indexOf("--owner") + 1)) +
                        Optional.ofNullable(argsList.get(argsList.indexOf("--additions") + 1)) +
                        Path.of(argsList.get(argsList.indexOf("--path") + 1)));
            }
            case "--list" -> System.out.println(service.list(argsList.get(argsList.indexOf("--list") + 1)).toString());

            case "--play" -> service.playMusic(argsList.get(argsList.indexOf("--play") + 1));
        }
    }
}
