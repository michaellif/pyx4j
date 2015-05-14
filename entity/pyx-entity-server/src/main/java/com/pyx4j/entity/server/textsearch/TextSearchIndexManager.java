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
package com.pyx4j.entity.server.textsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.textsearch.TextSearchFacade.KeywordUpdateRule;
import com.pyx4j.entity.server.textsearch.TextSearchFacade.UpdateChain;
import com.pyx4j.entity.shared.ITextSearchIndex;

class TextSearchIndexManager {

    private static Logger log = LoggerFactory.getLogger(TextSearchIndexManager.class);

    private TextSearchIndexManager() {
    }

    private static class SingletonHolder {
        public static final TextSearchIndexManager INSTANCE = new TextSearchIndexManager();
    }

    static TextSearchIndexManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public <T extends IEntity, E extends IEntity> void registerUpdateChain(Class<T> entityClass, Class<? extends ITextSearchIndex<E>> indexClass,
            UpdateChain<T, E> updateChain) {
        // TODO Auto-generated method stub

    }

    public <E extends IEntity> void registerUpdateRule(Class<? extends ITextSearchIndex<E>> indexClass, Class<? extends KeywordUpdateRule<E>> ruleClass) {
        // TODO Auto-generated method stub

    }

    public void queueIndexUpdate(IEntity entity) {
        // TODO Auto-generated method stub

    }
}
