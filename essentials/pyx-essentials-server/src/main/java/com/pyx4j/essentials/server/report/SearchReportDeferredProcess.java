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
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.search.IndexedEntitySearch;
import com.pyx4j.entity.server.search.SearchResultIterator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.ReportColumn;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;

public class SearchReportDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -7944873735643401186L;

    private final static Logger log = LoggerFactory.getLogger(SearchReportDeferredProcess.class);

    protected ReportTableFormater formater;

    private final ReportRequest request;

    private String encodedCursorRefference;

    protected final Class<? extends IEntity> entityClass;

    private List<String> selectedMemberNames;

    protected volatile boolean canceled;

    private int fetchCount = 0;

    private boolean fetchCompleate;

    private boolean formatCompleate;

    public SearchReportDeferredProcess(ReportRequest request) {
        SecurityController.assertPermission(new EntityPermission(request.getCriteria().getEntityClass(), EntityPermission.READ));
        this.request = request;
        this.request.getCriteria().setPageSize(0);
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
            formatCompleate();
            createDownloadable();
            formatCompleate = true;
        } else {
            long start = System.currentTimeMillis();
            try {
                if (selectedMemberNames == null) {
                    createHeader();
                }
                @SuppressWarnings({ "rawtypes", "unchecked" })
                IndexedEntitySearch<IEntity> search = new IndexedEntitySearch(request.getCriteria());
                search.buildQueryCriteria();
                SearchResultIterator<IEntity> it = search.getResult(encodedCursorRefference);
                try {
                    int currentFetchCount = 0;
                    while (it.hasNext()) {
                        IEntity ent = it.next();
                        SecurityController.assertPermission(EntityPermission.permissionRead(ent.getValueClass()));
                        reportEntity(ent);
                        fetchCount++;
                        currentFetchCount++;
                        if (ServerSideConfiguration.instance().getEnvironmentType() != ServerSideConfiguration.EnvironmentType.LocalJVM) {
                            if ((System.currentTimeMillis() - start > Consts.SEC2MSEC * 15) || (currentFetchCount > 200)) {
                                log.warn("Executions time quota exceeded {}; rows {}", currentFetchCount, System.currentTimeMillis() - start);
                                log.debug("fetch will continue rows {}; characters {}", fetchCount, formater.getBinaryDataSize());
                                encodedCursorRefference = it.encodedCursorReference();
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
                if (canceled) {
                    formater = null;
                }
            }
        }
    }

    protected String getFileName() {
        return EntityFactory.getEntityMeta(entityClass).getCaption() + ".csv";
    }

    protected boolean acceptMember(String memberName, MemberMeta memberMeta) {
        return true;
    }

    protected void createHeaderEnds() {
        formater.newRow();
    }

    protected void createHeader() {
        selectedMemberNames = new Vector<String>();
        EntityMeta em = EntityFactory.getEntityMeta(entityClass);
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
                formater.header(memberMeta.getCaption());
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                selectedMemberNames.add(memberName);
                formater.header(memberMeta.getCaption());
            }
        }
        createHeaderEnds();
    }

    protected boolean reportMember(IEntity entity, String memberName, MemberMeta memberMeta) {
        return true;
    }

    protected void reportEntityEnds(IEntity entity) {
        formater.newRow();
    }

    protected void reportEntity(IEntity entity) {
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : selectedMemberNames) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (!reportMember(entity, memberName, memberMeta)) {
                continue;
            }
            if (memberMeta.isEntity()) {
                formater.cell(((IEntity) entity.getMember(memberName)).getStringView());
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                formater.cell(entity.getMember(memberName).getValue());
            }
        }
        reportEntityEnds(entity);
    }

    protected void formatCompleate() {

    }

    protected void createDownloadable() {
        Downloadable d = new Downloadable(formater.getBinaryData(), formater.getContentType());
        d.save(getFileName());
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (formatCompleate) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(Context.getRequest().getContextPath() + "/download/" + System.currentTimeMillis() + "/" + getFileName());
            return r;
        } else {
            DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
            r.setProgress(fetchCount);
            if (canceled) {
                r.setCanceled();
            }
            return r;
        }
    }

    protected int getCount() {
        return fetchCount;
    }

}
