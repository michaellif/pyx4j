package com.pyx4j.client.demo.client.place;

import static com.pyx4j.site.shared.meta.NavigNode.ARGS_GROUP_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.ARGS_SEPARATOR;
import static com.pyx4j.site.shared.meta.NavigNode.NAME_VALUE_SEPARATOR;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

public class AppPlaceHistoryMapper implements PlaceHistoryMapper {

    private static final Logger log = LoggerFactory.getLogger(AppPlaceHistoryMapper.class);

    private final AppPlaceListing listing;

    public AppPlaceHistoryMapper() {
        listing = GWT.create(AppPlaceListing.class);
    }

    @Override
    public AppPlace getPlace(String token) {
        int splitIndex = token.indexOf(ARGS_GROUP_SEPARATOR);
        String placeId = null;
        String queryString = null;
        if (splitIndex == -1) {
            placeId = token;
        } else {
            placeId = token.substring(0, splitIndex);
            if (token.length() > splitIndex) {
                queryString = token.substring(splitIndex + 1);
            }
        }

        AppPlace place = listing.getPlace(placeId);

        if (queryString != null) {
            place.setArgs(parseQueryString(queryString));
        }
        return place;
    }

    @Override
    public String getToken(Place place) {
        if (place instanceof AppPlace) {
            return getPlaceId(place.getClass()) + createQueryString(((AppPlace) place).getArgs());
        }
        return null;
    }

    public static String getPlaceId(Class<? extends Place> clazz) {
        String simpleName = clazz.getName();
        // strip the package name
        simpleName = simpleName.substring(simpleName.indexOf("$") + 1).replace("$", "/");

        StringBuilder builder = new StringBuilder();
        char[] charArray = simpleName.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            if (i == 0) {
                builder.append(Character.toLowerCase(charArray[i]));
            } else if (Character.isUpperCase(charArray[i])) {
                builder.append('_').append(Character.toLowerCase(charArray[i]));
            } else {
                builder.append(charArray[i]);
            }
        }
        return builder.toString();
    }

    protected Map<String, String> parseQueryString(String queryString) {
        if (queryString.startsWith(ARGS_GROUP_SEPARATOR)) {
            queryString = queryString.substring(1);
        }
        Map<String, String> args = new HashMap<String, String>();
        if (queryString.length() == 0) {
            return args;
        }
        String[] nameValues = queryString.split(ARGS_SEPARATOR);
        if (nameValues.length > 0) {
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split(NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    args.put(nameAndValue[0], URL.decodeQueryString(nameAndValue[1]));
                } else {
                    log.warn("Can't pars argument {}", nameValues[i]);
                }
            }
        }
        return args;
    }

    protected String createQueryString(Map<String, String> args) {
        if (args == null) {
            return "";
        }
        StringBuilder queryString = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> me : args.entrySet()) {
            if (first) {
                queryString.append(ARGS_GROUP_SEPARATOR);
                first = false;
            } else {
                queryString.append(ARGS_SEPARATOR);
            }
            queryString.append(me.getKey());
            queryString.append(NAME_VALUE_SEPARATOR);
            queryString.append(URL.encodeQueryString(me.getValue()));
        }
        return queryString.toString();
    }

}