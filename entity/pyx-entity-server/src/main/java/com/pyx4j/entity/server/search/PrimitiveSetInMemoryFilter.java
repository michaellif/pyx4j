/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on May 12, 2010
 * @author vlads
 */
package com.pyx4j.entity.server.search;

import java.io.Serializable;
import java.util.Set;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;

public class PrimitiveSetInMemoryFilter extends InMemoryFilter {

    final Serializable searchValue;

    public PrimitiveSetInMemoryFilter(Path propertyPath, Serializable object) {
        super(propertyPath);
        searchValue = object;
    }

    @Override
    protected boolean accept(IEntity entity) {
        Set<?> value = (Set<?>) entity.getValue(propertyPath);
        if (value == null) {
            return false;
        } else {
            return value.contains(searchValue);
        }
    }

}
