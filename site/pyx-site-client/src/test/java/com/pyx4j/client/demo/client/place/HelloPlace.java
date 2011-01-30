package com.pyx4j.client.demo.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class HelloPlace extends Place {
    private final String helloName;

    public HelloPlace(String token) {
        this.helloName = token;
    }

    public String getHelloName() {
        return helloName;
    }

    public static class Tokenizer implements PlaceTokenizer<HelloPlace> {

        @Override
        public String getToken(HelloPlace place) {
            return place.getHelloName();
        }

        @Override
        public HelloPlace getPlace(String token) {
            return new HelloPlace(token);
        }

    }

}
