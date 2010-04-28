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
 * Created on 2010-04-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.search;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.Path;

public class CollectionInMemoryFilter extends InMemoryFilter {

    final List<String> valuePath = new Vector<String>();

    final Serializable searchValue;

    public CollectionInMemoryFilter(Path propertyPath, int pathItem, Serializable Object) {
        super(propertyPath);
        searchValue = Object;
        int count = 0;
        for (String memberName : propertyPath.getPathMembers()) {
            count++;
            if (count > pathItem + 1) {
                valuePath.add(memberName);
            }
        }
    }

    @Override
    protected boolean accept(IEntity entity) {
        IEntity ent = entity;
        for (String memberName : propertyPath.getPathMembers()) {
            IObject<?> member = ent.getMember(memberName);
            if (member instanceof ICollection) {
                if (((ICollection) member).isEmpty()) {
                    return false;
                }
                for (Object item : (ICollection) member) {
                    if (acceptValue((IEntity) item)) {
                        return true;
                    }
                }
                return false;
            } else {
                ent = (IEntity) member;
                if (ent.isNull()) {
                    return false;
                }
            }
        }
        return false;
    }

    protected boolean acceptValue(IEntity entity) {
        IEntity ent = entity;
        for (String memberName : valuePath) {
            IObject<?> member = ent.getMember(memberName);
            if (member instanceof IPrimitive<?>) {
                return searchValue.equals(member.getValue());
            } else if (member instanceof IEntity) {
                ent = (IEntity) member;
                if (ent.isNull()) {
                    return false;
                }
            }
        }
        return false;
    }
}
