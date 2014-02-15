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
 * Created on Apr 6, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.rpc.CrudAppPlace;

public class AppPlaceEntityMapper {

    private static final Logger log = LoggerFactory.getLogger(AppPlaceEntityMapper.class);

    private static HashMap<Class<? extends IEntity>, AppPlaceData> map = new HashMap<>();

    public static void register(Class<? extends IEntity> type, Class<? extends CrudAppPlace> placeClass, ImageResource image) {
        AppPlaceData data = new AppPlaceData(placeClass, image);
        if (map.containsKey(type)) {
            log.error("type is already registered {} to {}", type, map.get(type), new Throwable());
        }
        map.put(type, data);
    }

    public static CrudAppPlace resolvePlace(Class<? extends IEntity> type, Key id) {
        AppPlaceData data = retrievePlaceData(type);
        if (data != null) {
            CrudAppPlace place = AppSite.getHistoryMapper().createPlace(data.placeClass);
            if (id != null) {
                return place.formViewerPlace(id);
            } else {
                return place;
            }
        } else {
            return null;
        }
    }

    public static CrudAppPlace resolvePlace(Class<? extends IEntity> type) {
        return resolvePlace(type, null);
    }

    public static Class<? extends CrudAppPlace> resolvePlaceClass(Class<? extends IEntity> type) {
        AppPlaceData data = retrievePlaceData(type);
        if (data != null) {
            return data.placeClass;
        } else {
            return null;
        }
    }

    public static ImageResource resolveImageResource(Class<? extends IEntity> type) {
        AppPlaceData data = retrievePlaceData(type);
        if (data != null) {
            return data.image;
        } else {
            return null;
        }
    }

    static class AppPlaceData {
        Class<? extends CrudAppPlace> placeClass;

        ImageResource image;

        public AppPlaceData(Class<? extends CrudAppPlace> placeClass, ImageResource image) {
            this.placeClass = placeClass;
            this.image = image;
        }
    }

    // internals:
    private static AppPlaceData retrievePlaceData(Class<? extends IEntity> type) {
        AppPlaceData data = map.get(type);
        if (data == null) {
            data = map.get(EntityFactory.getEntityMeta(type).getBOClass());
        }
        return data;
    }
}
