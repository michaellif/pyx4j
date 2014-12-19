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
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.meta.PublicPlace;

public abstract class AbstractAppPlaceDispatcher implements AppPlaceDispatcher {

    private final static Logger log = LoggerFactory.getLogger(AbstractAppPlaceDispatcher.class);

    private AppPlace targetPlace = AppPlace.NOWHERE;

    protected AbstractAppPlaceDispatcher() {

        AppSite.getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {

            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                if (AppSite.getViewFactory() instanceof SingletonViewFactory) {
                    ((SingletonViewFactory) AppSite.getViewFactory()).invalidate();
                }
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        AppPlace current = AppSite.getPlaceController().getWhere();
                        if ((!(current instanceof PublicPlace) && !ClientContext.isAuthenticated()) || // Not authenticated user on non-public place
                                !isPlaceNavigable(current) || // Place is not navigable for current user
                                !current.isStable()) { // Place is not stable, should leave
                            AppSite.getPlaceController().goTo(AppPlace.NOWHERE, false);
                        }
                    }
                });

            }

        });

    }

    protected abstract AppPlace obtainDefaultPlace();

    /**
     * Define security for places. Called before each navigation. If it returns FALSE we will go to DefaultAuthenticatedPlace
     */
    protected abstract boolean isPlaceNavigable(AppPlace targetPlace);

    /**
     * This the only customization point, used for places like PasswordChangeRequired e.g. user can only go to one single place.
     * Only called when isApplicationAuthenticated() returns FALSE
     */
    protected abstract AppPlace mandatoryActionForward(AppPlace newPlace);

    @Override
    public final AppPlace forwardTo(AppPlace place) {
        AppPlace forwardPlace = mandatoryActionForward(place);
        if (forwardPlace == AppPlace.NOWHERE) {
            if (targetPlace != AppPlace.NOWHERE) {
                forwardPlace = targetPlace;
                targetPlace = AppPlace.NOWHERE;
            } else {
                forwardPlace = obtainDefaultPlace();
            }
        } else {
            if (forwardPlace != place) {
                targetPlace = place;
            }
        }
        forwardPlace = isPlaceNavigable(forwardPlace) ? forwardPlace : obtainDefaultPlace();
        log.info("Forward from place [{}] to place[{}]", place.getPlaceId(), forwardPlace.getPlaceId());
        return forwardPlace;
    }
}
