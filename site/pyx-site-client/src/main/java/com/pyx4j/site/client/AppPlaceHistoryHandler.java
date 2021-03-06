/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Apr 5, 2012
 * @author michaellif
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import com.pyx4j.site.rpc.AppPlace;

public class AppPlaceHistoryHandler {

    private static final Logger log = LoggerFactory.getLogger(AppPlaceHistoryHandler.class.getName());

    /**
     * Default implementation of {@link Historian}, based on {@link History}.
     */
    public static class DefaultHistorian implements Historian {
        @Override
        public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
            return History.addValueChangeHandler(valueChangeHandler);
        }

        @Override
        public String getToken() {
            return History.getToken();
        }

        @Override
        public void newItem(String token, boolean issueEvent) {
            History.newItem(token, issueEvent);
        }
    }

    /**
     * Optional delegate in charge of History related events. Provides nice
     * isolation for unit testing, and allows pre- or post-processing of tokens.
     * Methods correspond to the like named methods on {@link History}.
     */
    public interface Historian {
        /**
         * Adds a {@link com.google.gwt.event.logical.shared.ValueChangeEvent} handler to be informed of changes to the browser's history stack.
         *
         * @param valueChangeHandler
         *            the handler
         * @return the registration used to remove this value change handler
         */
        com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler);

        /**
         * @return the current history token.
         */
        String getToken();

        /**
         * Adds a new browser history entry. Calling this method will cause
         * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)} to be called as well.
         */
        void newItem(String token, boolean issueEvent);
    }

    private final Historian historian;

    private final PlaceHistoryMapper mapper;

    private AppPlaceContorller placeController;

    private String lastStablePlaceToken = "";

    /**
     * Create a new PlaceHistoryHandler with a {@link DefaultHistorian}. The
     * DefaultHistorian is created via a call to GWT.create(), so an alternative
     * default implementation can be provided through &lt;replace-with&gt; rules
     * in a {@code gwt.xml} file.
     *
     * @param mapper
     *            a {@link PlaceHistoryMapper} instance
     */
    public AppPlaceHistoryHandler(PlaceHistoryMapper mapper) {
        this(mapper, (Historian) GWT.create(DefaultHistorian.class));
    }

    /**
     * Create a new PlaceHistoryHandler.
     *
     * @param mapper
     *            a {@link PlaceHistoryMapper} instance
     * @param historian
     *            a {@link Historian} instance
     */
    public AppPlaceHistoryHandler(PlaceHistoryMapper mapper, Historian historian) {
        this.mapper = mapper;
        this.historian = historian;
    }

    /**
     * Handle the current history token. Typically called at application start, to
     * ensure bookmark launches work.
     */
    public void handleCurrentHistory() {
        handleHistoryToken(historian.getToken());
    }

    /**
     * Initialize this place history handler.
     *
     * @return a registration object to de-register the handler
     */
    public HandlerRegistration register(AppPlaceContorller placeController, EventBus eventBus) {
        this.placeController = placeController;

        final HandlerRegistration placeReg = eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                AppPlace newPlace = (AppPlace) event.getNewPlace();
                if (newPlace.isStable()) {
                    historian.newItem(tokenForPlace(newPlace), false);
                }
                lastStablePlaceToken = historian.getToken();
            }
        });

        final HandlerRegistration historyReg = historian.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String token = event.getValue();
                handleHistoryToken(token);
            }
        });

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                AppPlaceHistoryHandler.this.placeController = null;
                placeReg.removeHandler();
                historyReg.removeHandler();
            }
        };
    }

    /*
     * Internal function to restore history state of the last recorded App place.
     */
    void restoreStableHistoryToken() {
        historian.newItem(lastStablePlaceToken, false);
    }

    private void handleHistoryToken(String token) {

        Place newPlace = null;

        if ("".equals(token)) {
            newPlace = AppPlace.NOWHERE;
        }

        if (newPlace == null) {
            newPlace = mapper.getPlace(token);
        }

        if (newPlace == null) {
            log.warn("Unrecognized history token: " + token);
            newPlace = AppPlace.NOWHERE;
        }

        placeController.goTo(newPlace);
    }

    private String tokenForPlace(AppPlace newPlace) {

        String token = mapper.getToken(newPlace);
        if (token != null) {
            return token;
        }

        log.warn("Place not mapped to a token: " + newPlace);
        return "";
    }
}