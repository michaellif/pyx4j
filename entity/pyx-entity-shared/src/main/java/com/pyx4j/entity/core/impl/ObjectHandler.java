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
 * Created on Oct 29, 2009
 * @author michaellif
 */
package com.pyx4j.entity.core.impl;

import java.io.Serializable;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.MemberMeta;

public abstract class ObjectHandler<VALUE_TYPE> implements IObject<VALUE_TYPE>, Serializable {

    private static final long serialVersionUID = 335416094543053866L;

    private transient final Class<? extends IObject<VALUE_TYPE>> clazz;

    private transient IEntity owner;

    private transient IObject<?> parent;

    private transient String fieldName;

    public static final boolean PROPER_POINTERS = true;

    @SuppressWarnings("unchecked")
    public ObjectHandler(@SuppressWarnings("rawtypes") Class<? extends IObject> clazz, IObject<?> parent, String fieldName) {
        this.clazz = (Class<? extends IObject<VALUE_TYPE>>) clazz;
        attachToOwner(parent, fieldName);
    }

    void attachToOwner(IObject<?> parent, String fieldName) {
        this.parent = parent;
        if (parent instanceof ICollection<?, ?>) {
            this.owner = parent.getOwner();
        } else {
            this.owner = (IEntity) parent;
        }
        if (!".".equals(fieldName)) {
            this.fieldName = fieldName;
        }
    }

    @Override
    public IObject<?> getParent() {
        return parent;
    }

    @Override
    public IEntity getOwner() {
        return owner;
    }

    @Override
    public boolean isPrototype() {
        return getOwner().isPrototype();
    }

    @Override
    public boolean isValueDetached() {
        return getAttachLevel() != AttachLevel.Attached;
    }

    @Override
    public Path getPath() {
        return new Path(this);
    }

    @Override
    public MemberMeta getMeta() {
        assert getOwner() != null : "Accessing root entity";
        return getOwner().getEntityMeta().getMemberMeta(getFieldName());
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean metaEquals(IObject<?> other) {
        return getFieldName().equals(other.getFieldName()) && getMeta().equals(other.getMeta());
    }

    @Override
    public Class<? extends IObject<VALUE_TYPE>> getObjectClass() {
        assert (clazz != null) : this.getClass() + " objectClass is null";
        return clazz;
    }

}
