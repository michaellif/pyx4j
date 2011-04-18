/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.rdb;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.server.config.VistaServerSideConfiguration;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.quartz.SchedulerHelper;

public class VistaDBReset {

    private static final Logger log = LoggerFactory.getLogger(VistaDBReset.class);

    public static void main(String[] args) {
        VistaServerSideConfiguration conf = new VistaServerSideConfiguration();
        ServerSideConfiguration.setInstance(conf);
        EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) PersistenceServicesFactory.getPersistenceService();
        List<String> allClasses = EntityClassFinder.findEntityClasses();
        for (String className : allClasses) {
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            if (meta.isTransient()) {
                continue;
            }
            if (srv.isTableExists(meta.getEntityClass())) {
                log.info("drop table {}", meta.getEntityClass().getName());
                srv.dropTable(meta.getEntityClass());
            }
        }
        SchedulerHelper.dbReset();
        long start = System.currentTimeMillis();
        System.out.println(conf.getDataPreloaders().preloadAll());
        System.out.println("Preload time: " + TimeUtils.secSince(start));
    }
}
