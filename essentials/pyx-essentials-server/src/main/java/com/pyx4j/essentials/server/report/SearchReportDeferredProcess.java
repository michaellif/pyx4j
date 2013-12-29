/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-05-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.ReportColumn;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.IDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.security.shared.SecurityController;

public class SearchReportDeferredProcess<E extends IEntity> implements IDeferredProcess {

    private static final long serialVersionUID = -7944873735643401186L;

    private final static Logger log = LoggerFactory.getLogger(SearchReportDeferredProcess.class);

    protected ReportTableFormatter formatter;

    protected final ReportRequest request;

    private String encodedCursorReference;

    protected final Class<? extends IEntity> entityClass;

    private List<String> selectedMemberNames;

    protected volatile boolean canceled;

    private int maximum = 0;

    private int fetchCount = 0;

    private boolean fetchCompleate;

    private boolean formatCompleate;

    public SearchReportDeferredProcess(ReportRequest request) {
        SecurityController.assertPermission(new EntityPermission(request.getCriteria().getEntityClass(), EntityPermission.READ));
        this.request = request;
        this.entityClass = request.getCriteria().getEntityClass();

        if (request.getDownloadFormat() == null) {
            request.setDownloadFormat(DownloadFormat.CSV);
        }
        switch (request.getDownloadFormat()) {
        case XLS:
            this.formatter = new ReportTableXLSXFormatter(false);
            break;
        case XLSX:
            this.formatter = new ReportTableXLSXFormatter(true);
            break;
        case CSV:
            this.formatter = new ReportTableCSVFormatter();
            ((ReportTableCSVFormatter) this.formatter).setTimezoneOffset(request.getTimezoneOffset());
            break;
        default:
            break;
        }
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
            formatCompleate();
            createDownloadable();
            formatCompleate = true;
        } else {
            long start = System.currentTimeMillis();
            Persistence.service().startBackgroundProcessTransaction();
            try {
                if (selectedMemberNames == null) {
                    maximum = Persistence.service().count(request.getCriteria());
                    createHeader();

                }
                @SuppressWarnings("unchecked")
                ICursorIterator<E> it = (ICursorIterator<E>) Persistence.service().query(encodedCursorReference, request.getCriteria(), AttachLevel.Attached);
                try {
                    int currentFetchCount = 0;
                    while (it.hasNext()) {
                        E ent = it.next();
                        SecurityController.assertPermission(EntityPermission.permissionRead(ent.getValueClass()));
                        reportEntity(ent);
                        fetchCount++;
                        currentFetchCount++;
                        if (ServerSideConfiguration.instance().getEnvironmentType() != ServerSideConfiguration.EnvironmentType.LocalJVM) {
                            if ((System.currentTimeMillis() - start > Consts.SEC2MSEC * 15) || (currentFetchCount > 200)) {
                                log.warn("Executions time quota exceeded {}; rows {}", currentFetchCount, System.currentTimeMillis() - start);
                                log.debug("fetch will continue rows {}; characters {}", fetchCount, formatter.getBinaryDataSize());
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
                    it.close();
                }
                log.debug("fetch complete rows {}; characters {}", fetchCount, formatter.getBinaryDataSize());
                fetchCompleate = true;
            } finally {
                Persistence.service().endTransaction();
                if (canceled) {
                    formatter = null;
                }
            }
        }
    }

    protected String getFileName() {
        return EntityFactory.getEntityMeta(entityClass).getCaption() + "." + request.getDownloadFormat().getExtension();
    }

    protected boolean acceptMember(String memberName, MemberMeta memberMeta) {
        return true;
    }

    protected String getMemberCaption(String memberName, MemberMeta memberMeta) {
        return memberMeta.getCaption();
    }

    protected void createHeaderEnds() {
        formatter.newRow();
    }

    protected EntityMeta headerEntityMeta() {
        return EntityFactory.getEntityMeta(entityClass);
    }

    protected void createHeader() {
        selectedMemberNames = new Vector<String>();
        EntityMeta em = headerEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (em.getMemberMeta(memberName).isRpcTransient()) {
                continue;
            }
            ReportColumn reportColumn = memberMeta.getAnnotation(ReportColumn.class);
            if ((reportColumn != null) && reportColumn.ignore()) {
                continue;
            }
            if (!acceptMember(memberName, memberMeta)) {
                continue;
            }
            if (memberMeta.isEntity()) {
                selectedMemberNames.add(memberName);
                formatter.header(getMemberCaption(memberName, memberMeta));
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                selectedMemberNames.add(memberName);
                formatter.header(getMemberCaption(memberName, memberMeta));
            }
        }
        createHeaderEnds();
    }

    protected int getCount() {
        return fetchCount;
    }

    protected boolean reportMember(E entity, String memberName, MemberMeta memberMeta) {
        return true;
    }

    protected void reportEntityEnds(E entity) {
        formatter.newRow();
    }

    protected void reportEntity(E entity) {
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : selectedMemberNames) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (!reportMember(entity, memberName, memberMeta)) {
                continue;
            }
            if (memberMeta.isEntity()) {
                formatter.cell(((IEntity) entity.getMember(memberName)).getStringView());
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                formatter.cell(entity.getMember(memberName).getValue());
            }
        }
        reportEntityEnds(entity);
    }

    protected void formatCompleate() {

    }

    protected void createDownloadable() {
        Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
        d.save(getFileName());
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (formatCompleate) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + getFileName());
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
