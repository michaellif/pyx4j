/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 1, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

public class TransientListHelpers {

    public interface WorkflowAdapter<E> {
        boolean doBefore(E item);

        boolean doAfter(E item);
    }

    public static class DefaultWorkflowAdapter<E> implements WorkflowAdapter<E> {

        @Override
        public boolean doBefore(E item) {
            return true;
        }

        @Override
        public boolean doAfter(E item) {
            return true;
        }

    }

    public static <E extends IEntity> void save(IList<E> list, Class<E> entityClass) {
        save(list, entityClass, EntityQueryCriteria.create(entityClass), null);
    }

    public static <E extends IEntity> void save(IList<E> list, Class<E> entityClass, WorkflowAdapter<E> onDeleteItem) {
        save(list, entityClass, EntityQueryCriteria.create(entityClass), onDeleteItem);
    }

    public static <E extends IEntity> void save(IList<E> list, Class<E> entityClass, EntityQueryCriteria<E> criteria, WorkflowAdapter<E> onDeleteItem) {

        // load current:
        List<E> current = Persistence.service().query(criteria);

        // save and remove from current if exist:
        for (E item : list) {
            Persistence.service().merge(item);

            if (current.contains(item)) {
                current.remove(item);
            }
        }

        // remove orphaned ones:
        for (E item : current) {
            if (onDeleteItem == null || onDeleteItem.doBefore(item)) {
                Persistence.service().delete(item);
            }
        }
    }
}
