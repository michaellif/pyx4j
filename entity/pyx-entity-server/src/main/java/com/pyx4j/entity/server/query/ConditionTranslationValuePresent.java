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
 * Created on Jul 15, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import java.io.Serializable;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.core.query.IValuePresentCondition;

public class ConditionTranslationValuePresent extends AbstractConditionTranslation<IValuePresentCondition> {

    @Override
    public <E extends IEntity> void enhanceCriteria(EntityQueryCriteria<E> criteria, Path entityMemeberPath, IValuePresentCondition condition) {
        if (!condition.booleanValue().isNull()) {
            if (condition.booleanValue().getValue()) {
                criteria.add(new PropertyCriterion(entityMemeberPath, Restriction.NOT_EQUAL, (Serializable) null));
            } else {
                criteria.add(new PropertyCriterion(entityMemeberPath, Restriction.EQUAL, (Serializable) null));
            }
        }
    }

}
