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

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.ReportTableCSVFormater;
import com.pyx4j.essentials.server.report.ReportTableFormater;
import com.pyx4j.essentials.server.report.SearchReportDeferredProcess;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.domain.VistaNamespace;

public class DBIntegrityCheckDeferredProcess extends SearchReportDeferredProcess<Pmc> {

    private static final long serialVersionUID = 1L;

    protected ReportTableFormater formater;

    public DBIntegrityCheckDeferredProcess(ReportRequest request) {
        super(request);
        this.formater = new ReportTableCSVFormater();
    }

    @Override
    protected String getFileName() {
        return "DB_integrity_check.csv";
    }

    @Override
    public void cancel() {
        canceled = true;
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
            commonNamespaceIntegrityCheck();
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
        }
    }

    private void commonNamespaceIntegrityCheck() {
        EntityPersistenceServiceRDB srv = (EntityPersistenceServiceRDB) Persistence.service();
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
            if (!EntityPersistenceServiceRDB.allowNamespaceUse(entityClass)) {
                continue;
            }
            if (srv.isTableExists(meta.getEntityClass())) {
                int keys = srv.count(EntityQueryCriteria.create(entityClass));
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

    private void specificNamespaceIntegrityCheck(String namespace) {
        try {
            NamespaceManager.setNamespace(namespace);
            //TODO
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
        }
    }

    @Override
    protected void createDownloadable() {
        Downloadable d = new Downloadable(formater.getBinaryData(), formater.getContentType());
        d.save(getFileName());
    }
}
