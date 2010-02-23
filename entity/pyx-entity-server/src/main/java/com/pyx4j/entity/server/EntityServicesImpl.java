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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.security.shared.SecurityController;

public class EntityServicesImpl {

    public static class SaveImpl implements EntityServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            if (request.getPrimaryKey() == null) {
                SecurityController.assertPermission(EntityPermission.permissionCreate(request.getObjectClass()));
            } else {
                SecurityController.assertPermission(EntityPermission.permissionUpdate(request.getObjectClass()));
            }
            PersistenceServicesFactory.getPersistenceService().persist(request);
            return request;
        }
    }

    public static class QueryImpl implements EntityServices.Query {

        @SuppressWarnings("unchecked")
        @Override
        public Vector execute(EntityCriteria request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            List<IEntity> rc = PersistenceServicesFactory.getPersistenceService().query(request);
            Vector<IEntity> v = new Vector<IEntity>();
            for (IEntity ent : rc) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
                v.add(ent);
            }
            return v;
        }
    }

    public static class RetrieveImpl implements EntityServices.Retrieve {

        @SuppressWarnings("unchecked")
        @Override
        public IEntity execute(EntityCriteria request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            IEntity ent = PersistenceServicesFactory.getPersistenceService().retrieve(request);
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
            }
            return ent;
        }
    }

    public static class RetrieveByPKImpl implements EntityServices.RetrieveByPK {

        @SuppressWarnings("unchecked")
        @Override
        public IEntity execute(EntityCriteriaByPK request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            IEntity ent = PersistenceServicesFactory.getPersistenceService().retrieve(ServerEntityFactory.entityClass(request.getDomainName()),
                    request.getPrimaryKey());
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
            }
            return ent;
        }
    }

}
