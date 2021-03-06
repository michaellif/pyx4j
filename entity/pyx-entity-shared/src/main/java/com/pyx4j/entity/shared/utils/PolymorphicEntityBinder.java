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
 * Created on Jul 8, 2014
 * @author vlads
 */
package com.pyx4j.entity.shared.utils;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;

public abstract class PolymorphicEntityBinder<BO extends IEntity, TO extends IEntity> extends SimpleEntityBinder<BO, TO> {

    private final Map<Class<? extends BO>, EntityBinder<BO, TO>> bindingByBO = new HashMap<>();

    private final Map<Class<? extends TO>, EntityBinder<BO, TO>> bindingByTO = new HashMap<>();

    protected PolymorphicEntityBinder(Class<BO> boClass, Class<TO> toClass) {
        super(boClass, toClass);
    }

    @Override
    protected abstract void bind();

    //TODO Change order of parameters or order of generics
    @SuppressWarnings("unchecked")
    protected final <TBO extends BO, TTO extends TO> void bind(Class<TTO> toClass, Class<TBO> boClass, EntityBinder<TBO, TTO> binder) {
        bindingByBO.put(boClass, (EntityBinder<BO, TO>) binder);
        bindingByTO.put(toClass, (EntityBinder<BO, TO>) binder);
    }

    private void init() {
        if (bindingByBO.isEmpty()) {
            synchronized (bindingByBO) {
                if (bindingByBO.isEmpty()) {
                    bind();
                }
            }
        }
    }

    private EntityBinder<BO, TO> getBinderByBO(BO bo) {
        init();
        return bindingByBO.get(bo.getInstanceValueClass());
    }

    private EntityBinder<BO, TO> getBinderByTO(Class<? extends TO> toClass) {
        init();
        return bindingByTO.get(toClass);
    }

    @SuppressWarnings("unchecked")
    private EntityBinder<BO, TO> getBinderByTO(TO to) {
        return getBinderByTO((Class<TO>) to.getInstanceValueClass());
    }

    @Override
    public TO createTO(BO bo, BindingContext context) {
        EntityBinder<BO, TO> subBinder = getBinderByBO(bo);
        assert subBinder != null : "Binder not found for " + bo.getDebugExceptionInfoString();
        return subBinder.createTO(bo.<BO> cast(), context);
    }

    @Override
    public void copyBOtoTO(BO bo, TO to, BindingContext context) {
        EntityBinder<BO, TO> subBinder = getBinderByBO(bo);
        assert subBinder != null : "Binder not found for " + bo.getDebugExceptionInfoString();
        subBinder.copyBOtoTO(bo, to, context);
        super.copyBOtoTO(bo, to, context);
    }

    @Override
    public BO createBO(TO to, BindingContext context) {
        EntityBinder<BO, TO> subBinder = getBinderByTO(to);
        assert subBinder != null : "Binder not found for " + to.getDebugExceptionInfoString() + " in binder " + this.getClass().getName();
        return subBinder.createBO(to.<TO> cast(), context);
    }

    @Override
    public void copyTOtoBO(TO to, BO bo, BindingContext context) {
        EntityBinder<BO, TO> subBinder = getBinderByTO(to);
        assert subBinder != null : "Binder not found for " + to.getDebugExceptionInfoString() + " in binder " + this.getClass().getName();
        subBinder.copyTOtoBO(to, bo, context);
        super.copyTOtoBO(to, bo, context);
    }

    @Override
    public Path getBoundBOMemberPath(Path toMemberPath) {
        init();
        // Find by members of super type
        if (toClass.getSimpleName().equals(toMemberPath.getRootObjectClassName())) {
            for (Class<? extends TO> toClass : bindingByTO.keySet()) {
                Path boMemberPath = getBinderByTO(toClass).getBoundBOMemberPath(new Path(toClass, toMemberPath.getPathMembers()));
                if (boMemberPath != null) {
                    return new Path(boClass, boMemberPath.getPathMembers());
                }
            }
        }
        return null;
    }
}
