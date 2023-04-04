package com.heb.eg.interview.imagedetectorapi.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CreateImageDto {

    private String location;

    private char enableDetection;

    private String label;

}
