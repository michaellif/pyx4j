/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.qa;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.pyx4j.commons.Filter;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.SearchReportDeferredProcess;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.server.upgrade.VistaUpgrade;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;

public class DBIntegrityCheckDeferredProcess extends SearchReportDeferredProcess<Pmc> {

    private static final long serialVersionUID = 1L;

    public DBIntegrityCheckDeferredProcess(ReportRequest request) {
        super(request);
    }

    @Override
    protected String getFileName() {
        return "DB_integrity_check.csv";
    }

    @Override
    public void execute() {
        boolean success = false;
        try {
            Persistence.service().startBackgroundProcessTransaction();
            super.execute();
            Persistence.service().commit();
            success = true;
        } finally {
            if (!success) {
                Persistence.service().rollback();
            }
            Persistence.service().endTransaction();
        }
    }

    @Override
    protected void createHeader() {
        formater.header("pmc");
        formater.header("table");
        formater.header("count");
        formater.newRow();

        specificNamespaceIntegrityCheck(VistaNamespace.adminNamespace);
        specificNamespaceIntegrityCheck(VistaNamespace.expiringNamespace);
    }

    @Override
    protected void reportEntity(Pmc entity) {
        try {
            NamespaceManager.setNamespace(entity.namespace().getValue());
            RDBUtils.initAllEntityTables();
            commonNamespaceIntegrityCheck();
            VistaUpgrade.upgradePmcData(entity);
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
        }
    }

    private void commonNamespaceIntegrityCheck() {

        exportTablesInfo(new Filter<Class<? extends IEntity>>() {

            @Override
            public boolean accept(Class<? extends IEntity> entityClass) {
                return EntityPersistenceServiceRDB.allowNamespaceUse(entityClass);
            }
        });
    }

    private void specificNamespaceIntegrityCheck(String namespace) {
        try {
            NamespaceManager.setNamespace(namespace);

            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                RDBUtils.initNameSpaceSpecificEntityTables();
            }

            exportTablesInfo(new Filter<Class<? extends IEntity>>() {
                @Override
                public boolean accept(Class<? extends IEntity> entityClass) {
                    Table table = entityClass.getAnnotation(Table.class);
                    return ((table != null) && NamespaceManager.getNamespace().equals(table.namespace()));
                }
            });
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
        }
    }

    private void exportTablesInfo(Filter<Class<? extends IEntity>> filter) {
        ((EntityPersistenceServiceRDB) Persistence.service()).resetMapping();
        List<String> allClasses = EntityClassFinder.getEntityClassesNames();
        TreeMap<String, Integer> tablesMap = new TreeMap<String, Integer>();
        for (String className : allClasses) {
            if (className.toLowerCase().contains(".gae")) {
                continue;
            }
            Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
            if (meta.isTransient() || entityClass.getAnnotation(AbstractEntity.class) != null || entityClass.getAnnotation(EmbeddedEntity.class) != null) {
                continue;
            }
            if (filter.accept(entityClass)) {
                if (!EntityPersistenceServiceRDB.allowNamespaceUse(entityClass)) {
                    continue;
                }
                int keys = Persistence.service().count(EntityQueryCriteria.create(entityClass));
                tablesMap.put(meta.getEntityClass().getSimpleName(), keys);
            }
        }
        for (Entry<String, Integer> entry : tablesMap.entrySet()) {
            formater.cell(NamespaceManager.getNamespace());
            formater.cell(entry.getKey());
            formater.cell(entry.getValue());
            formater.newRow();
        }
    }
}
