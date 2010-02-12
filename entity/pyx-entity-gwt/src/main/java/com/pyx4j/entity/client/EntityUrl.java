/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Feb 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pyx4j.entity.shared.IEntity;

/**
 * Allows passing parameters between forms and widgets using history tokens.
 * 
 * TODO use name value pairs.
 */
public class EntityUrl {

    private static Map<String, IEntity<?>> local = new HashMap<String, IEntity<?>>();

    private static int localCount = 1;

    public static String getHistoryParmeters(IEntity<?> entity) {
        //TODO convert actual members to name value pairs.
        String historyParmeters = "l" + (localCount++);
        local.put(historyParmeters, entity);
        return historyParmeters;
    }

    public static <T extends IEntity<T>> void obtainEntityFromHistoryParmeters(Class<T> entityClass, String historyParmeters, AsyncCallback<T> handlingCallback) {
        IEntity<?> ent = local.get(historyParmeters);
        if (ent == null) {
            //TODO pars parameters and create entity, Use ReferenceDataManager to obtain reference data by values names.
            handlingCallback.onFailure(new RuntimeException("Not Found"));
        } else {
            handlingCallback.onSuccess((T) ent);
        }
    }

}
