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
 * Created on Apr 21, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.query.IBooleanCondition;
import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.entity.core.query.IDateCondition;
import com.pyx4j.entity.core.query.IDateOffsetCondition;
import com.pyx4j.entity.core.query.IDecimalRangeCondition;
import com.pyx4j.entity.core.query.IEntityCondition;
import com.pyx4j.entity.core.query.IEnumCondition;
import com.pyx4j.entity.core.query.IIntegerRangeCondition;
import com.pyx4j.entity.core.query.IStringCondition;
import com.pyx4j.entity.core.query.IValuePresentCondition;

@SuppressWarnings("rawtypes")
class ConditionTranslationRegistry {

    private Map<Class<?>, ConditionTranslation<?>> classRegistry = new HashMap<>();

    private Map<Path, ConditionTranslation<?>> pathRegistry = new HashMap<>();

    private static class SingletonHolder {
        public static final ConditionTranslationRegistry INSTANCE = new ConditionTranslationRegistry();
    }

    static ConditionTranslationRegistry instance() {
        return SingletonHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private ConditionTranslationRegistry() {
        registerCondition(IStringCondition.class, new ConditionTranslationString());
        registerCondition(IDateCondition.class, new ConditionTranslationDate());
        registerCondition(IDateOffsetCondition.class, new ConditionTranslationDateOffset());
        registerCondition(IEntityCondition.class, new ConditionTranslationEntity());
        registerCondition(IEnumCondition.class, new ConditionTranslationEnum());
        registerCondition(IBooleanCondition.class, new ConditionTranslationBoolean());
        registerCondition(IValuePresentCondition.class, new ConditionTranslationValuePresent());
        registerCondition(IIntegerRangeCondition.class, new ConditionTranslationIntegerRange());
        registerCondition(IDecimalRangeCondition.class, new ConditionTranslationDecimalRange());
    }

    <C extends ICondition> void registerCondition(Class<C> conditionClass, ConditionTranslation<C> conditionTranslation) {
        classRegistry.put(conditionClass, conditionTranslation);
    }

    <C extends ICondition> void registerCondition(C queryMember, ConditionTranslation<C> conditionTranslation) {
        pathRegistry.put(queryMember.getPath(), conditionTranslation);
    }

    @SuppressWarnings("unchecked")
    ConditionTranslation<ICondition> getConditionTranslation(ICondition condition) {
        ConditionTranslation ct;
        ct = pathRegistry.get(condition.getPath());
        if (ct == null) {
            ct = classRegistry.get(condition.getInstanceValueClass());
        }
        //TODO See PYX-14.
        if (ct == null && condition instanceof IEntityCondition) {
            ct = classRegistry.get(IEntityCondition.class);
        } else if (ct == null && condition instanceof IEnumCondition) {
            ct = classRegistry.get(IEnumCondition.class);
        }

        if (ct == null) {
            throw new Error("Unknown criterion class " + condition.getInstanceValueClass().getName());
        }
        return ct;
    }
}
