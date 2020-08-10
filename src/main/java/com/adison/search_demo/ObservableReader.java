package com.adison.search_demo;

import io.reactivex.Observable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//to observe the console and emit strings when input is given
public class ObservableReader {

    static Observable<String> from(InputStream inputStream) {
        return Observable.create(emitter -> {
            var reader = new BufferedReader(new InputStreamReader(inputStream));
            try (reader) {
                String text;
                while ((text = reader.readLine()) != null) {
                    emitter.onNext(text);
                }
                emitter.onComplete();
            }
        });
    }
}