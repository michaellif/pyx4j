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
 * Created on 2010-08-03
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.server;

import java.util.Locale;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.examples.domain.User;
import com.pyx4j.examples.domain.UserCredential;
import com.pyx4j.examples.rpc.EditableUser;
import com.pyx4j.examples.rpc.ExamplesAdminServices;

public class ExamplesAdminServicesImpl implements ExamplesAdminServices {

    public static class SaveImpl extends EntityServicesImpl.SaveImpl implements ExamplesAdminServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            EditableUser requestEditableUser = (EditableUser) request;
            User user = requestEditableUser.user();
            user.email().setValue(user.email().getValue().toLowerCase(Locale.ENGLISH));
            user = (User) super.execute(user);

            EntityQueryCriteria<UserCredential> crCriteria = EntityQueryCriteria.create(UserCredential.class);
            crCriteria.add(PropertyCriterion.eq(crCriteria.meta().user(), user));
            UserCredential crs = PersistenceServicesFactory.getPersistenceService().retrieve(crCriteria);
            if (crs == null) {
                crs = EntityFactory.create(UserCredential.class);
                crs.user().set(user);
                crs.credential().set(user.email());
            }
            crs.enabled().set(requestEditableUser.enabled());
            crs.behavior().set(requestEditableUser.behavior());

            PersistenceServicesFactory.getPersistenceService().persist(crs);

            EditableUser editableUser = EntityFactory.create(EditableUser.class);
            editableUser.setPrimaryKey(user.getPrimaryKey());
            editableUser.user().set(user);

            editableUser.enabled().set(crs.enabled());
            editableUser.behavior().set(crs.behavior());

            return editableUser;
        }

    }

    public static class RetrieveImpl extends EntityServicesImpl.RetrieveImpl implements ExamplesAdminServices.Retrieve {

        @Override
        public IEntity execute(EntityQueryCriteria request) {
            EntityQueryCriteria<User> criteria;
            if (request instanceof EntityCriteriaByPK) {
                criteria = EntityCriteriaByPK.create(User.class, ((EntityCriteriaByPK<?>) request).getPrimaryKey());
            } else {
                criteria = new EntityQueryCriteria<User>(User.class);
                for (Criterion c : ((EntityQueryCriteria<?>) request).getFilters()) {
                    criteria.add(c);
                }
            }
            User user = (User) super.execute(criteria);

            EditableUser editableUser = EntityFactory.create(EditableUser.class);
            editableUser.setPrimaryKey(user.getPrimaryKey());
            editableUser.user().set(user);

            // -- copy Authentication data but not credentials.
            EntityQueryCriteria<UserCredential> crCriteria = EntityQueryCriteria.create(UserCredential.class);
            crCriteria.add(PropertyCriterion.eq(crCriteria.meta().user(), user));
            UserCredential crs = PersistenceServicesFactory.getPersistenceService().retrieve(crCriteria);
            editableUser.enabled().set(crs.enabled());
            editableUser.behavior().set(crs.behavior());

            return editableUser;
        }
    }
}
