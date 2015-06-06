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
 * Created on Jun 5, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.query.IEnumCondition;

public class ConditionTranslationEnum<E extends Enum<E>> extends AbstractConditionTranslation<IEnumCondition<E>> {

    @Override
    public <T extends IEntity> void enhanceCriteria(EntityQueryCriteria<T> criteria, Path entityMemeberPath, IEnumCondition<E> condition) {

    }

    @Override
    public void onBeforePersist(IEnumCondition<E> condition) {
        if (!condition.values().isValueDetached()) {
            condition.enums().clear();
            for (E enm : condition.values()) {
                condition.enums().add(enm.name());
            }
        }
    }

    @Override
    public void onAfterRetrive(IEnumCondition<E> condition) {
        @SuppressWarnings("unchecked")
        Class<E> enumValueClass = (Class<E>) condition.getValueClass();
        for (String valueName : condition.enums()) {
            condition.values().add(Enum.valueOf(enumValueClass, valueName));
        }
    }

}
