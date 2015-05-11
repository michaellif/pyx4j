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
 * Created on Jun 6, 2014
 * @author vlads
 */
package com.pyx4j.security.server;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Behavior;

public class UIAclBuilder extends ServletContainerAclBuilder {

    protected void grant(Class<? extends ActionId> actionClass) {
        grant(new ActionPermission(actionClass));
    }

    protected void grant(Behavior behavior, Class<? extends ActionId> actionClass) {
        grant(behavior, new ActionPermission(actionClass));
    }

    protected void grant(Behavior behavior1, Behavior behavior2, Class<? extends ActionId> actionClass) {
        grant(behavior1, new ActionPermission(actionClass));
        grant(behavior2, new ActionPermission(actionClass));
    }

    protected void grant(Behavior behavior1, Behavior behavior2, Behavior behavior3, Class<? extends ActionId> actionClass) {
        grant(behavior1, new ActionPermission(actionClass));
        grant(behavior2, new ActionPermission(actionClass));
        grant(behavior3, new ActionPermission(actionClass));
    }

    protected void grant(Behavior behavior, Class<? extends ActionId> actionClass, InstanceAccess protectionDomain) {
        grant(behavior, new ActionPermission(actionClass, protectionDomain));
    }

    protected void grant(Behavior behavior, Class<? extends IEntity> entityClass, int actions) {
        grant(behavior, DataModelPermission.create(entityClass, actions));
    }

    protected void grant(Behavior behavior, Class<? extends IEntity> entityClass, InstanceAccess instanceAccess, int actions) {
        grant(behavior, DataModelPermission.create(entityClass, instanceAccess, actions));
    }

    protected void grant(Behavior behavior, Class<? extends IEntity> entityClass1, Class<? extends IEntity> entityClass2, int actions) {
        grant(behavior, DataModelPermission.create(entityClass1, actions));
        grant(behavior, DataModelPermission.create(entityClass2, actions));
    }

    protected void grant(Behavior behavior, List<Class<? extends IEntity>> entities, int actions) {
        for (Class<? extends IEntity> entityClass : entities) {
            grant(behavior, DataModelPermission.create(entityClass, actions));
        }
    }

    @SafeVarargs
    protected final List<Class<? extends IEntity>> entities(Class<? extends IEntity>... classes) {
        return Arrays.asList(classes);
    }
}
