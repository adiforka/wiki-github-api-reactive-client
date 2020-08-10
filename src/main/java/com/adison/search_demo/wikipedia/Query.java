package com.adison.search_demo.wikipedia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Query {

    private List<Article> search;
}
