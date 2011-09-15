/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.tests;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

import com.propertyvista.config.tests.VistaDBTestBase;

public class VistaDBResetTest extends VistaDBTestBase {

    public void testObjectsStructure() {
        List<String> allClasses = EntityClassFinder.findEntityClasses();
        EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) Persistence.service();

        Map<String, Class<? extends IEntity>> allTables = new Hashtable<String, Class<? extends IEntity>>();

        for (String className : allClasses) {
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            if (meta.isTransient()) {
                continue;
            }
            if (className.endsWith("DTO")) {
                System.err.println("DTO Object " + className + "  should be @Transient");
                Assert.fail("DTO Object " + className + "  should be @Transient");
            }

            if (allTables.containsKey(meta.getPersistenceName())) {
                System.err.println("IEntity " + className + " has the same table name as " + allTables.get(meta.getPersistenceName()));
                Assert.fail("IEntity " + className + " has the same table name as " + allTables.get(meta.getPersistenceName()));
            }

            if (srv.isTableExists(meta.getEntityClass())) {
                srv.dropTable(meta.getEntityClass());
            }
            allTables.put(meta.getPersistenceName(), entityClass);
        }
    }
}
