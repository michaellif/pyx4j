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

import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.entity.core.query.IDateCondition;
import com.pyx4j.entity.core.query.IEntityCondition;
import com.pyx4j.entity.core.query.IStringCondition;

@SuppressWarnings("rawtypes")
class ConditionTranslationRegistry {

    private Map<Class<?>, ConditionTranslation<?>> registry = new HashMap<>();

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
        registerCondition(IEntityCondition.class, new ConditionTranslationEntity());
    }

    <C extends ICondition> void registerCondition(Class<C> conditionClass, ConditionTranslation<C> conditionTranslation) {
        registry.put(conditionClass, conditionTranslation);
    }

    @SuppressWarnings("unchecked")
    ConditionTranslation<ICondition> getConditionTranslation(ICondition condition) {
        ConditionTranslation ct = registry.get(condition.getInstanceValueClass());
        //TODO See PYX-14.
        if (ct == null && condition instanceof IEntityCondition) {
            ct = registry.get(IEntityCondition.class);
        }
        if (ct == null) {
            throw new Error("Unknown criterion class " + condition.getInstanceValueClass().getName());
        }
        return ct;
    }
}
