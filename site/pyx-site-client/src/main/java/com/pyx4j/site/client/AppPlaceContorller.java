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
 * Created on Aug 9, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.widgets.client.PopupPanel;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;

public final class AppPlaceContorller extends PlaceController {

    private static final Logger log = LoggerFactory.getLogger(AppPlaceContorller.class);

    private final EventBus eventBus;

    private AppPlace where = AppPlace.NOWHERE;

    private AppPlace forwardedFrom = AppPlace.NOWHERE;

    private final AppPlaceDispatcher dispatcher;

    public AppPlaceContorller(EventBus eventBus, AppPlaceDispatcher dispatcher) {
        super(eventBus);
        this.eventBus = eventBus;
        this.dispatcher = dispatcher;

        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                String warning = confirmGoTo(AppPlace.NOWHERE);
                if (warning != null) {
                    event.setMessage(warning);
                }
            }
        });
    }

    @Override
    public AppPlace getWhere() {
        return where;
    }

    public Place getForwardedFrom() {
        return forwardedFrom;
    }

    public void showNotification(Notification notification, AppPlace continuePlace) {
        NotificationAppPlace notificationPlace = dispatcher.getNotificationPlace(notification);
        notificationPlace.setContinuePlace(continuePlace);
        sureGoTo(notificationPlace);
    }

    public void showNotification(Notification notification) {
        showNotification(notification, null);
    }

    public void goTo(final AppPlace newPlace, final boolean withConfirm) {
        log.debug("requested to go to: " + newPlace);
        dispatcher.forwardTo(newPlace, new DefaultAsyncCallback<AppPlace>() {
            @Override
            public void onSuccess(final AppPlace result) {
                if (withConfirm) {
                    maybeGoTo(result);
                } else {
                    sureGoTo(result);
                }
            }
        });
    }

    public void goTo(final AppPlace newPlace) {
        goTo(newPlace, true);
    }

    /**
     * Shouldn't be called by application. Use goTo(AppPlace) instead
     */
    @Override
    public void goTo(final Place newPlace) {
        //TODO external request - handle unstable tokens. If stable counterpart exists - execute, otherwise go to NOWHERE
        goTo((AppPlace) newPlace);
    }

    private String confirmGoTo(Place newPlace) {
        PlaceChangeRequestEvent willChange = new PlaceChangeRequestEvent(newPlace);
        eventBus.fireEvent(willChange);
        String warning = willChange.getWarning();
        return warning;
    }

    private void maybeGoTo(final AppPlace newPlace) {
        String warning = confirmGoTo(newPlace);
        if (warning != null) {
            dispatcher.confirm(warning, new ConfirmDecline() {

                @Override
                public void onConfirmed() {
                    sureGoTo(newPlace);

                }

                @Override
                public void onDeclined() {
                    // In case we pressed a back or forward while navigating inside the application we need to restore the history token
                    // We should not fire event since application state change did not happened 
                    AppSite.getHistoryHandler().restoreStableHistoryToken();
                }
            });
        } else {
            sureGoTo(newPlace);
        }
    }

    private void sureGoTo(AppPlace newPlace) {
        PopupPanel.hideAll();
        forwardedFrom = where;
        where = newPlace;
        log.debug("forwarded to: {} from {}", where, forwardedFrom);

        eventBus.fireEvent(new PlaceChangeEvent(where));
    }

}
