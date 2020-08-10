package com.adison.search_demo;

import com.adison.search_demo.github.GithubService;
import com.adison.search_demo.wikipedia.WikipediaService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SearchApplication {

    private final GithubService githubService = new GithubService(retrofitBuilder("https://api.github.com/"));
    private final WikipediaService wikipediaService = new WikipediaService(retrofitBuilder("https://en.wikipedia.org/w/"));

    private Retrofit retrofitBuilder(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BASIC))
                        .build())
                .build();
    }

    public static void main(String[] args) {


    }
}
