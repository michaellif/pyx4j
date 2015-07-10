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
 * Created on May 13, 2015
 * @author vlads
 */
package com.pyx4j.entity.shared.utils;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;

import com.pyx4j.entity.core.IEntity;

/**
 * Used to ensure that each Entity in TO converted to the same single entity in BO and back
 *
 */
public class BindingContext {

    public static enum BindingType {

        List,

        View,

        Edit,

        Save;

    }

    private final BindingType bindingType;

    private final Map<Map<String, Serializable>, IEntity> mapped = new IdentityHashMap<>();

    public BindingContext(BindingType bindingType) {
        this.bindingType = bindingType;
    }

    void put(IEntity from, IEntity to) {
        if (!from.isNull()) {
            mapped.put(from.getValue(), to);
        }
    }

    public IEntity get(IEntity from) {
        if (!from.isNull()) {
            return mapped.get(from.getValue());
        } else {
            return null;
        }
    }

    public BindingType getBindingType() {
        return bindingType;
    }

}
