/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 14, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.textsearch.TextSearchFacade;
import com.pyx4j.entity.shared.adapters.UpdateTextSearchIndexPersistenceAdapter;

public class UpdateTextSearchIndexPersistenceAdapterImpl implements UpdateTextSearchIndexPersistenceAdapter {

    private static final Logger log = LoggerFactory.getLogger(UpdateTextSearchIndexPersistenceAdapterImpl.class);

    private static int todo_remove = 100;

    @Override
    public void onBeforePersist(IEntity origEntity, IEntity newEntity) {
    }

    @Override
    public void onAfterPersist(IEntity entity) {
        if (entity.isValueDetached()) {
            // TODO This should not happen investigate later. VISTA-6711
            todo_remove++;
            if (todo_remove >= 100) {
                log.warn("TODO This should not happen investigate", new RuntimeException());
                todo_remove = 0;
            }
            return;
        }
        ServerSideFactory.create(TextSearchFacade.class).queueIndexUpdate(entity);
    }

}
