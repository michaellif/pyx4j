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
 * Created on May 4, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.core.query.IEntityCondition;
import com.pyx4j.entity.server.Persistence;

public class ConditionTranslationEntity<E extends IEntity> extends AbstractConditionTranslation<IEntityCondition<E>> {

    @Override
    public <T extends IEntity> void enhanceCriteria(EntityQueryCriteria<T> criteria, Path entityMemeberPath, IEntityCondition<E> condition) {
        if (!condition.refs().isNull()) {
            criteria.add(new PropertyCriterion(entityMemeberPath, Restriction.IN, condition.refs()));
        }
    }

    @Override
    public void onBeforePersist(IEntityCondition<E> condition) {
        if (!condition.references().isValueDetached()) {
            condition.refs().clear();
            for (E entity : condition.references()) {
                condition.refs().add(entity.getPrimaryKey());
            }
        }
    }

    @Override
    public void onAfterRetrive(IEntityCondition<E> condition) {
        for (Key pk : condition.refs()) {
            E entity = condition.references().$();
            entity.setPrimaryKey(pk);
            if (!Persistence.service().retrieve(entity)) {
                throw new Error("Entity  " + entity.getDebugExceptionInfoString() + " refferenced in sotrage not found");
            }
            condition.references().add(entity);
        }
    }
}
