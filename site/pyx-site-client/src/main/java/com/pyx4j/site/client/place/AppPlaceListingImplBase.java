/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 4, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client.place;

import java.util.HashMap;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.site.shared.meta.SiteMap;

public abstract class AppPlaceListingImplBase implements AppPlaceListing {

    protected final HashMap<String, AppPlaceFactory<?>> mappedPlaces = new HashMap<String, AppPlaceFactory<?>>();

    protected final HashMap<Class<? extends AppPlace>, AppPlaceFactory<?>> placesFactories = new HashMap<Class<? extends AppPlace>, AppPlaceFactory<?>>();

    protected final HashMap<Class<? extends AppPlace>, AppPlaceInfo> placesInfo = new HashMap<Class<? extends AppPlace>, AppPlaceInfo>();

    protected static final I18n i18n = I18n.get(AppPlace.class);

    AppPlaceListingImplBase() {
    }

    protected final void map(Class<? extends SiteMap> siteMapClass, Class<? extends AppPlace> placeClass, AppPlaceFactory<?> factory) {
        String token = AppPlaceInfo.getPlaceId(placeClass);
        mappedPlaces.put(siteMapClass.getName() + "#" + token, factory);
        placesFactories.put(placeClass, factory);
    }

    protected final void map(Class<? extends AppPlace> placeClass, AppPlaceInfo placeInfo) {
        placesInfo.put(placeClass, placeInfo);
    }

    @Override
    public final AppPlace getPlace(Class<? extends SiteMap> siteMapClass, String token) {
        AppPlaceFactory<?> factory = mappedPlaces.get(siteMapClass.getName() + "#" + token);
        // Support single inheritance site map
        if ((factory == null) && (siteMapClass.getSuperclass() != null)) {
            factory = mappedPlaces.get(siteMapClass.getSuperclass().getName() + "#" + token);
        }
        if (factory != null) {
            return factory.create();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E extends AppPlace> E createPlace(Class<E> placeClass) {
        AppPlaceFactory<?> factory = placesFactories.get(placeClass);
        assert factory != null : "Place class " + placeClass.getName() + " not found";
        return (E) factory.create();
    }

    @Override
    public final AppPlaceInfo getPlaceInfo(Class<? extends AppPlace> placeClass) {
        return placesInfo.get(placeClass);
    }
}
