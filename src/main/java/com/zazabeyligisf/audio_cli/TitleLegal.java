package com.zazabeyligisf.audio_cli;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@Builder
public record TitleLegal(String name, String additions, String owner) {
}
