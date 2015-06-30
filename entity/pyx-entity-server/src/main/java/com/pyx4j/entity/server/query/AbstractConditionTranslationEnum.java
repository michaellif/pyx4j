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
 * Created on Jun 30, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import com.pyx4j.entity.core.query.IEnumCondition;

public abstract class AbstractConditionTranslationEnum<E extends Enum<E>, T extends IEnumCondition<E>> extends AbstractConditionTranslation<T> {

    @Override
    public void onBeforePersist(T condition) {
        if (!condition.values().isValueDetached()) {
            condition.enums().clear();
            for (E enm : condition.values()) {
                condition.enums().add(enm.name());
            }
        }
    }

    @Override
    public void onAfterRetrive(T condition) {
        Class<E> enumValueClass = condition.values().getValueClass();
        for (String valueName : condition.enums()) {
            condition.values().add(Enum.valueOf(enumValueClass, valueName));
        }
    }
}
