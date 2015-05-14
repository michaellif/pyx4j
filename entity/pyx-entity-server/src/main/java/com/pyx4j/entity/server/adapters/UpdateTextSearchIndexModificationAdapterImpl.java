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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.textsearch.TextSearchFacade;
import com.pyx4j.entity.shared.adapters.UpdateTextSearchIndexModificationAdapter;

public class UpdateTextSearchIndexModificationAdapterImpl implements UpdateTextSearchIndexModificationAdapter {

    @Override
    public void onBeforeUpdate(IEntity origEntity, IEntity newEntity) {
        ServerSideFactory.create(TextSearchFacade.class).queueIndexUpdate(newEntity);
    }

}
