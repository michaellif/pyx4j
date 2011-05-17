/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-05-17
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.util.List;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;

class EntityMemberEmbeddedAccess implements EntityMemberAccess {

    private final List<String> path;

    private final String memberName;

    public EntityMemberEmbeddedAccess(List<String> path, String memberName) {
        this.memberName = memberName;
        this.path = path;
    }

    @Override
    public String getMemberName() {
        return memberName;
    }

    private IEntity actualEntity(IEntity entity) {
        for (String memberName : path) {
            entity = (IEntity) entity.getMember(memberName);
        }
        return entity;
    }

    @Override
    public Object getMemberValue(IEntity entity) {
        return actualEntity(entity).getMemberValue(memberName);
    }

    @Override
    public boolean containsMemberValue(IEntity entity) {
        return actualEntity(entity).containsMemberValue(memberName);
    }

    @Override
    public void setMemberValue(IEntity entity, Object value) {
        actualEntity(entity).setMemberValue(memberName, value);
    }

    @Override
    public IObject<?> getMember(IEntity entity) {
        return actualEntity(entity).getMember(memberName);
    }

}
