/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 16, 2014
 * @author vlads
 */
package com.pyx4j.entity.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.GWTSerializable;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;

/**
 * Use this as example of GWT serializable condition.
 */
public class ForTypesAccessRule implements InstanceAccess {

    private static final long serialVersionUID = 1L;

    //Not final because of GWT
    @GWTSerializable
    private Vector<IEntity> prototypes = new Vector<>();

    // This cant be serialized with GWT RPC
    private transient List<Class<? extends IEntity>> entityClasses = new ArrayList<>();

    @GWTSerializable
    protected ForTypesAccessRule() {
    }

    public ForTypesAccessRule(Class<? extends IEntity> entityClass) {
        prototypes.add(EntityFactory.getEntityPrototype(entityClass));
    }

    public ForTypesAccessRule(Class<? extends IEntity> entityClass1, Class<? extends IEntity> entityClass2) {
        prototypes.add(EntityFactory.getEntityPrototype(entityClass1));
        prototypes.add(EntityFactory.getEntityPrototype(entityClass2));
    }

    public ForTypesAccessRule(@SuppressWarnings("unchecked") Class<? extends IEntity>... entityClasses) {
        for (Class<? extends IEntity> entityClass : entityClasses) {
            prototypes.add(EntityFactory.getEntityPrototype(entityClass));
        }
    }

    @GWTSerializable
    @Deprecated
    private void setPrototypes(Vector<IEntity> prototypes) {
        this.prototypes = prototypes;
    }

    @Override
    public boolean implies(IEntity contextEntity) {
        if (entityClasses.isEmpty()) {
            initAfterSerialization();
        }
        return entityClasses.contains(contextEntity.getValueClass());
    }

    private void initAfterSerialization() {
        entityClasses = new ArrayList<>(prototypes.size());
        for (IEntity entityPrototype : prototypes) {
            entityClasses.add(entityPrototype.getValueClass());
        }
    }

}
