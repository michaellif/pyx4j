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
 * @version $Id$
 */
package com.pyx4j.entity.shared.impl;

import java.io.Serializable;

import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;

public abstract class ObjectHandler<VALUE_TYPE> implements IObject<VALUE_TYPE>, Serializable {

    private static final long serialVersionUID = 335416094543053866L;

    private transient final Class<? extends IObject<VALUE_TYPE>> clazz;

    private transient IEntity owner;

    private transient final IObject<?> parent;

    private transient final String fieldName;

    @SuppressWarnings("unchecked")
    public ObjectHandler(Class<? extends IObject> clazz, IObject<?> parent, String fieldName) {
        this.clazz = (Class<? extends IObject<VALUE_TYPE>>) clazz;
        this.parent = parent;
        if (parent instanceof ICollection<?, ?>) {
            this.owner = parent.getOwner();
        } else {
            this.owner = (IEntity) parent;
        }
        this.fieldName = fieldName;
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
    public Path getPath() {
        return new Path(this);
    }

    @Override
    public MemberMeta getMeta() {
        return getOwner().getEntityMeta().getMemberMeta(getFieldName());
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Class<? extends IObject<VALUE_TYPE>> getObjectClass() {
        assert (clazz != null) : this.getClass() + " objectClass is null";
        return clazz;
    }

}
