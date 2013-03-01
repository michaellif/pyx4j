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
package com.propertyvista.operations.server.qa;

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
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.report.SearchReportDeferredProcess;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.server.upgrade.VistaUpgrade;

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
        new UnitOfWork(TransactionScopeOption.Suppress, true).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                DBIntegrityCheckDeferredProcess.super.execute();
                return null;
            }

        });

    }

    @Override
    protected void createHeader() {
        formatter.header("pmc");
        formatter.header("table");
        formatter.header("count");
        formatter.newRow();

        specificNamespaceIntegrityCheck(VistaNamespace.operationsNamespace);
        specificNamespaceIntegrityCheck(VistaNamespace.expiringNamespace);
    }

    @Override
    protected void reportEntity(Pmc entity) {
        try {
            NamespaceManager.setNamespace(entity.namespace().getValue());
            new UnitOfWork(TransactionScopeOption.RequiresNew, true).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    RDBUtils.initAllEntityTables();
                    return null;
                }

            });
            commonNamespaceIntegrityCheck();
            VistaUpgrade.upgradePmcData(entity);
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
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

                new UnitOfWork(TransactionScopeOption.RequiresNew, true).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        RDBUtils.initNameSpaceSpecificEntityTables();
                        return null;
                    }

                });
            }

            exportTablesInfo(new Filter<Class<? extends IEntity>>() {
                @Override
                public boolean accept(Class<? extends IEntity> entityClass) {
                    Table table = entityClass.getAnnotation(Table.class);
                    return ((table != null) && NamespaceManager.getNamespace().equals(table.namespace()));
                }
            });
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
        }
    }

    private void exportTablesInfo(final Filter<Class<? extends IEntity>> filter) {
        new UnitOfWork(TransactionScopeOption.Suppress, true).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {

                ((EntityPersistenceServiceRDB) Persistence.service()).resetMapping();
                List<String> allClasses = EntityClassFinder.getEntityClassesNames();
                TreeMap<String, Integer> tablesMap = new TreeMap<String, Integer>();
                for (String className : allClasses) {
                    if (className.toLowerCase().contains(".gae")) {
                        continue;
                    }
                    Class<? extends IEntity> entityClass = ServerEntityFactory.entityClass(className);
                    EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
                    if (meta.isTransient() || entityClass.getAnnotation(AbstractEntity.class) != null
                            || entityClass.getAnnotation(EmbeddedEntity.class) != null) {
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
                    formatter.cell(NamespaceManager.getNamespace());
                    formatter.cell(entry.getKey());
                    formatter.cell(entry.getValue());
                    formatter.newRow();
                }

                return null;
            }

        });

    }
}
