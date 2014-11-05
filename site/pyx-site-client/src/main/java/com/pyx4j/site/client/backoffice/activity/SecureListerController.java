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
 * Created on Jun 23, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client.backoffice.activity;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.backoffice.ui.prime.lister.IPrimeLister;

public class SecureListerController<E extends IEntity> extends ListerController<E> {

    public SecureListerController(Class<E> entityClass, IPrimeLister<E> view, AbstractListCrudService<E> service) {
        super(entityClass, view, service);
    }

    @Override
    public boolean canCreateNewItem() {
        return SecurityController.check(DataModelPermission.permissionCreate(getEntityClass()));
    }

    // Ignore Populate Request on Hidden/ No Access Lister
    @Override
    public void populate() {
        if (SecurityController.check(DataModelPermission.permissionRead(getEntityClass()))) {
            super.populate();
        }
    }
}
