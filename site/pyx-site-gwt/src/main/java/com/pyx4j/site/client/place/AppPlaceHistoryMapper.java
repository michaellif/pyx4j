package com.pyx4j.site.client.place;

import static com.pyx4j.site.shared.meta.NavigNode.ARGS_GROUP_SEPARATOR;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.site.shared.meta.SiteMap;

public class AppPlaceHistoryMapper implements PlaceHistoryMapper {

    private final static AppPlaceListing listing = GWT.create(AppPlaceListing.class);

    private final Class<? extends SiteMap> siteMapClass;

    public AppPlaceHistoryMapper(Class<? extends SiteMap> siteMapClass) {
        this.siteMapClass = siteMapClass;
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

        AppPlace place = listing.getPlace(siteMapClass, placeId);

        if (queryString != null) {
            place.parseArgs(queryString);
        }
        return place;
    }

    @Override
    public String getToken(Place place) {
        if (place instanceof AppPlace) {
            return ((AppPlace) place).getToken();
        }
        return null;
    }

    public String getPlaceId(Place place) {
        if (place instanceof AppPlace) {
            return AppPlaceInfo.getPlaceId(place.getClass());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <E extends AppPlace> E createPlace(Class<E> placeClass) {
        return (E) getPlace(AppPlaceInfo.getPlaceId(placeClass));
    }

    public AppPlaceInfo getPlaceInfo(AppPlace place) {
        return listing.getPlaceInfo(place);
    }

}