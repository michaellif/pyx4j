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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.report.ReportTableCSVFormater;
import com.pyx4j.essentials.server.report.ReportTableFormater;
import com.pyx4j.essentials.server.report.SearchReportDeferredProcess;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.VistaNamespace;

public class DBIntegrityCheckDeferredProcess<E extends IEntity> implements IDeferredProcess {

    private static final long serialVersionUID = 1L;

    private static String filename = "DB_integrity_check.csv";

    private final static Logger log = LoggerFactory.getLogger(SearchReportDeferredProcess.class);

    protected ReportTableFormater formater;

    private final ReportRequest request;

    private String encodedCursorReference;

    protected final Class<? extends IEntity> entityClass;

    private List<String> selectedMemberNames;

    protected volatile boolean canceled;

    private int maximum = 0;

    private int fetchCount = 0;

    private boolean fetchCompleate;

    private boolean formatCompleate;

    public DBIntegrityCheckDeferredProcess(ReportRequest request) {
        SecurityController.assertPermission(new EntityPermission(request.getCriteria().getEntityClass(), EntityPermission.READ));
        this.request = request;
        this.entityClass = request.getCriteria().getEntityClass();
        this.formater = new ReportTableCSVFormater();
        ((ReportTableCSVFormater) this.formater).setTimezoneOffset(request.getTimezoneOffset());
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public void execute() {
        if (canceled) {
            return;
        }
        if (fetchCompleate) {
            createDownloadable();
            formatCompleate = true;
        } else {
            long start = System.currentTimeMillis();
            Persistence.service().startTransaction();
            try {
                if (selectedMemberNames == null) {
                    maximum = Persistence.service().count(request.getCriteria());
                    createHeader();

                }
                @SuppressWarnings("unchecked")
                ICursorIterator<Pmc> it = (ICursorIterator<Pmc>) Persistence.service().query(encodedCursorReference, request.getCriteria(),
                        AttachLevel.Attached);
                try {
                    int currentFetchCount = 0;
                    while (it.hasNext()) {
                        Pmc ent = it.next();
                        SecurityController.assertPermission(EntityPermission.permissionRead(ent.getValueClass()));
                        reportEntity(ent);
                        fetchCount++;
                        currentFetchCount++;
                        if (ServerSideConfiguration.instance().getEnvironmentType() != ServerSideConfiguration.EnvironmentType.LocalJVM) {
                            if ((System.currentTimeMillis() - start > Consts.SEC2MSEC * 15) || (currentFetchCount > 200)) {
                                log.warn("Executions time quota exceeded {}; rows {}", currentFetchCount, System.currentTimeMillis() - start);
                                log.debug("fetch will continue rows {}; characters {}", fetchCount, formater.getBinaryDataSize());
                                encodedCursorReference = it.encodedCursorReference();
                                return;
                            }
                        }
                        if (canceled) {
                            log.debug("fetch canceled");
                            break;
                        }
                    }
                } finally {
                    it.completeRetrieval();
                }
                log.debug("fetch complete rows {}; characters {}", fetchCount, formater.getBinaryDataSize());
                fetchCompleate = true;
            } finally {
                Persistence.service().endTransaction();
                if (canceled) {
                    formater = null;
                }
            }
        }
    }

    protected void createHeader() {
        formater.header("pmc");
        formater.header("table");
        formater.header("count");
        formater.newRow();
    }

    protected void reportEntity(Pmc entity) {
        try {
            if (entity.status().getValue().equals(PmcStatus.Active)) {
                NamespaceManager.setNamespace(entity.namespace().getValue());
                dbIntegrityCheck();
            }
        } finally {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
        }
    }

    private void dbIntegrityCheck() {
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

    protected void createDownloadable() {
        Downloadable d = new Downloadable(formater.getBinaryData(), formater.getContentType());
        d.save(filename);
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (formatCompleate) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + filename);
            return r;
        } else {
            DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
            r.setProgress(fetchCount);
            r.setProgressMaximum(maximum);
            if (canceled) {
                r.setCanceled();
            }
            return r;
        }
    }
}
