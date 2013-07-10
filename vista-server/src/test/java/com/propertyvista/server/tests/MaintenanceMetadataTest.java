/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.tests;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.maintenance.MaintenanceMetadataAbstractManager.CategoryTree;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.portal.server.preloader.RefferenceDataPreloader;

@Ignore
public class MaintenanceMetadataTest extends VistaDBTestBase {
    private final static Logger log = LoggerFactory.getLogger(MaintenanceMetadataTest.class);

    private MaintenanceRequestCategory root;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (((EntityPersistenceServiceRDB) Persistence.service()).getDatabaseType() == DatabaseType.HSQLDB) {
            log.error("This test is intended for benchmarking on a real RDBMS - see VistaTestDBSetup#init()");
            // preload categories if needed
            new RefferenceDataPreloader().createInternalMaintenancePreload();
        }
        EntityQueryCriteria<MaintenanceRequestCategory> crit = EntityQueryCriteria.create(MaintenanceRequestCategory.class);
        crit.add(PropertyCriterion.eq(crit.proto().name(), "ROOT"));
        root = Persistence.service().retrieve(crit);
    }

    public void testRetrieveRecursive() {
        log.info("Starting RECURSIVE loading...");
        long initTime = System.currentTimeMillis();
        CategoryTree tree = new CategoryTree(root);
        tree.retrieveRecursive();
        initTime = System.currentTimeMillis() - initTime;
        log.info("INIT TIME: {}", initTime);
        log.info("CATEGORIES: {}", tree.getNodeCount());
    }

    public void testRetrieveAll() {
        log.info("Starting NON-RECURSIVE loading...");
        long initTime = System.currentTimeMillis();
        CategoryTree tree = new CategoryTree(root);
        tree.retrieveAll();
        initTime = System.currentTimeMillis() - initTime;
        log.info("INIT TIME: {}", initTime);
        log.info("CATEGORIES: {}", tree.getNodeCount());
    }
}
