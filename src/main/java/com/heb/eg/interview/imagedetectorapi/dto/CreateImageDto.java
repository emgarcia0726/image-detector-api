package com.heb.eg.interview.imagedetectorapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateImageDto {
    private String location;
    private char enableDetection;
    private String label;
}
