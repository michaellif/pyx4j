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
 * Created on Aug 22, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.search;

import java.util.Map;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class EntityInMemoryFilter extends InMemoryFilter {

    final String searchValue;

    public EntityInMemoryFilter(Path propertyPath, String object) {
        super(propertyPath);
        searchValue = object;
    }

    @Override
    protected boolean accept(IEntity entity) {
        Map<String, Object> childIEntity = (Map<String, Object>) entity.getValue(propertyPath);
        if (childIEntity == null) {
            return false;
        }
        return EqualsHelper.equals(searchValue, childIEntity.get(IEntity.PRIMARY_KEY));
    }

}
