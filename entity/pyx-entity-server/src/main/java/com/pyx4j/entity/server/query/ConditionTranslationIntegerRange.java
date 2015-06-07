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
 * Created on Jun 6, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.core.query.IIntegerRangeCondition;

public class ConditionTranslationIntegerRange extends AbstractConditionTranslation<IIntegerRangeCondition> {

    @Override
    public <E extends IEntity> void enhanceCriteria(EntityQueryCriteria<E> criteria, Path entityMemeberPath, IIntegerRangeCondition condition) {
        if (!condition.integerValueFrom().isNull()) {
            criteria.add(new PropertyCriterion(entityMemeberPath, Restriction.GREATER_THAN_OR_EQUAL, condition.integerValueFrom().getValue()));
        }
        if (!condition.integerValueTo().isNull()) {
            criteria.add(new PropertyCriterion(entityMemeberPath, Restriction.LESS_THAN_OR_EQUAL, condition.integerValueTo().getValue()));
        }
    }

}
