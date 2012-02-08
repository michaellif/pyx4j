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
 * Created on Feb 8, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.meta.PublicPlace;
import com.pyx4j.site.shared.meta.SigningOutPlace;

public abstract class AbstractAppPlaceDispatcher implements AppPlaceDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AbstractAppPlaceDispatcher.class);

    private AppPlace urlEntryTargetPlace = AppPlace.NOWHERE;

    /**
     * Called when application is not authenticated and url points NOWHERE of hidden(authenticated only) place
     */
    protected abstract void obtainDefaulPublicPlacePlace(AsyncCallback<AppPlace> callback);

    protected abstract boolean isApplicationAuthenticated();

    /**
     * Define security for places. Called before each navigation. If it returns FALSE we will go to DefaultAuthenticatedPlace
     */
    protected abstract void isPlaceNavigable(AppPlace targetPlace, AsyncCallback<Boolean> callback);

    /**
     * Fallback place for authenticated application
     */
    protected abstract void obtainDefaultAuthenticatedPlace(AsyncCallback<AppPlace> callback);

    /**
     * This the only customization point, used for places like PasswordChangeRequired e.g. user can only go to one single place.
     * Only called when isApplicationAuthenticated() returns FALSE
     */
    protected abstract AppPlace specialForward(AppPlace newPlace);

    @Override
    public final void forwardTo(AppPlace newPlace, final AsyncCallback<AppPlace> callback) {
        if ((newPlace instanceof PublicPlace) || (newPlace instanceof SigningOutPlace)) {
            callback.onSuccess(newPlace);
        } else if (isApplicationAuthenticated()) {
            final AppPlace targetPlace = selectTargetPlace(newPlace);
            isPlaceNavigable(targetPlace, new DefaultAsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if ((result) && (targetPlace != AppPlace.NOWHERE)) {
                        callback.onSuccess(targetPlace);
                    } else {
                        obtainDefaultAuthenticatedPlace(callback);
                    }
                }
            });
            urlEntryTargetPlace = AppPlace.NOWHERE;
        } else {
            AppPlace special = specialForward(newPlace);
            if (special != null) {
                callback.onSuccess(special);
            } else {
                urlEntryTargetPlace = newPlace;
                obtainDefaulPublicPlacePlace(callback);
            }
        }
    }

    @Override
    public void confirm(String message, Command onConfirmed) {
        log.debug("We show JS confirm {}", message);
        if (Window.confirm(message)) {
            onConfirmed.execute();
        }
    }

    private AppPlace selectTargetPlace(AppPlace newPlace) {
        if ((newPlace == AppPlace.NOWHERE) && (urlEntryTargetPlace != AppPlace.NOWHERE)) {
            return urlEntryTargetPlace;
        } else {
            return newPlace;
        }
    }

}
