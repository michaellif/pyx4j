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
 * Created on Jul 3, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.lister;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.security.shared.SecurityController;

public class EntityLister {

    public static <T extends IEntity> EntitySearchResult<T> secureQuery(EntityListCriteria<T> criteria) {
        SecurityController.assertPermission(new EntityPermission(criteria.getEntityClass(), EntityPermission.READ));
        EntitySearchResult<T> r = new EntitySearchResult<T>();
        final ICursorIterator<T> unfiltered = PersistenceServicesFactory.getPersistenceService().query(null, criteria);
        try {
            while (unfiltered.hasNext()) {
                T ent = unfiltered.next();
                SecurityController.assertPermission(EntityPermission.permissionRead(ent));
                r.add(ent);
                if ((criteria.getPageSize() > 0) && r.getData().size() >= criteria.getPageSize()) {
                    break;
                }
            }
            // The position is important, hasNext may retrieve one more row. 
            r.setEncodedCursorReference(unfiltered.encodedCursorReference());
            r.hasMoreData(unfiltered.hasNext());
        } finally {
            unfiltered.completeRetrieval();
        }

        return r;
    }
}
