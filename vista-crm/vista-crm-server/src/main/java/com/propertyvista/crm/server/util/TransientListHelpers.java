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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

public class TransientListHelpers {

    public static <E extends IEntity> List<E> loadTransientList(Class<E> entityClass) {
        return load(entityClass, EntityQueryCriteria.create(entityClass));
    }

    public static <E extends IEntity> List<E> load(Class<E> entityClass, EntityQueryCriteria<E> criteria) {
        return Persistence.service().query(criteria);
    }

    public static <E extends IEntity> void save(IList<E> list, Class<E> entityClass) {
        save(list, entityClass, EntityQueryCriteria.create(entityClass));
    }

    public static <E extends IEntity> void save(IList<E> list, Class<E> entityClass, EntityQueryCriteria<E> criteria) {

        // load current:
        List<E> current = load(entityClass, criteria);

        // save and remove from current if exist:
        for (E item : list) {
            Persistence.service().merge(item);

            if (current.contains(item)) {
                current.remove(item);
            }
        }

        // remove orphaned ones:
        for (E item : current) {
            Persistence.service().delete(item);
        }
    }
}
