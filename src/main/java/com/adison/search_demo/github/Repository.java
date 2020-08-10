package com.adison.search_demo.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Repository {

    private String name;
    private String description;
}
