package com.pyx4j.client.demo.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class LandingPlace extends Place {

    public LandingPlace() {
    }

    public static class Tokenizer implements PlaceTokenizer<LandingPlace> {

        @Override
        public LandingPlace getPlace(String token) {
            return new LandingPlace();
        }

        @Override
        public String getToken(LandingPlace place) {
            return null;
        }
    }

}
