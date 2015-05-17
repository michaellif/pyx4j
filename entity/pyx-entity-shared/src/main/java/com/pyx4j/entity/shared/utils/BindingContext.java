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
//TODO pass this as argument in EntityBinder functions
public class BindingContext {

    public static enum BindingType {

        List,

        View,

        Edit;

    }

    //TODO remove default
    private BindingType bindingType = BindingType.List;

    private final Map<Map<String, Serializable>, IEntity> mapped = new IdentityHashMap<>();

    void put(IEntity from, IEntity to) {
        mapped.put(from.getValue(), to);
    }

    public IEntity get(IEntity from) {
        return mapped.get(from.getValue());
    }

    public BindingType getBindingType() {
        return bindingType;
    }

    public void setBindingType(BindingType bindingType) {
        this.bindingType = bindingType;
    }
}
