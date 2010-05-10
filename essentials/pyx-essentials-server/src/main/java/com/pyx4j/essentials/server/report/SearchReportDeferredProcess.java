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
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.search.IndexedEntitySearch;
import com.pyx4j.entity.server.search.SearchResultIterator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.ReportColumn;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.security.shared.SecurityController;

public class SearchReportDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -7944873735643401186L;

    private final static Logger log = LoggerFactory.getLogger(SearchReportDeferredProcess.class);

    protected String dataStored;

    protected transient StringBuilder dataBuilder;

    private final EntitySearchCriteria<?> request;

    private String encodedCursorRefference;

    private final Class<? extends IEntity> entityClass;

    private List<String> selectedMemberNames;

    private volatile boolean canceled;

    private int fetchCount = 0;

    private boolean fetchCompleate;

    private boolean formatCompleate;

    public SearchReportDeferredProcess(EntitySearchCriteria<?> request) {
        SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
        this.request = request;
        this.request.setPageSize(0);
        this.entityClass = ServerEntityFactory.entityClass(request.getDomainName());
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
            try {
                dataBuilder = new StringBuilder();
                if (dataStored != null) {
                    dataBuilder.append(dataStored);
                }
                if (selectedMemberNames == null) {
                    formatHeader();
                }
                IndexedEntitySearch search = new IndexedEntitySearch(request);
                search.buildQueryCriteria();
                SearchResultIterator<IEntity> it = search.getResult(encodedCursorRefference);
                while (it.hasNext()) {
                    IEntity ent = it.next();
                    SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
                    formatEntity(ent);
                    fetchCount++;
                    if (System.currentTimeMillis() - start > Consts.SEC2MSEC * 15) {
                        log.warn("Executions time quota exceeded {}", System.currentTimeMillis() - start);
                        log.debug("fetch will continue {}; data size {}", fetchCount, dataBuilder.length());
                        encodedCursorRefference = it.encodedCursorRefference();
                        return;
                    }
                    if (canceled) {
                        log.debug("fetch canceled");
                        break;
                    }
                }
                log.debug("fetch compleate {}; data size {}", fetchCount, dataBuilder.length());
                fetchCompleate = true;
            } finally {
                if (canceled) {
                    dataStored = null;
                } else {
                    dataStored = dataBuilder.toString();
                }
            }
        }
    }

    protected String getFileName() {
        return EntityFactory.getEntityMeta(entityClass).getCaption() + ".csv";
    }

    protected boolean acceptMemeber(String memberName, MemberMeta memberMeta) {
        return true;
    }

    protected void formatHeader() {
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
            if (!acceptMemeber(memberName, memberMeta)) {
                continue;
            }
            if (memberMeta.isEntity()) {
                selectedMemberNames.add(memberName);
                dataBuilder.append(memberMeta.getCaption());
                dataBuilder.append(",");
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                selectedMemberNames.add(memberName);
                dataBuilder.append(memberMeta.getCaption());
                dataBuilder.append(",");
            }
        }
        dataBuilder.append("\n");
    }

    protected void formatEntity(IEntity entity) {
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : selectedMemberNames) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isEntity()) {
                dataBuilder.append(((IEntity) entity.getMember(memberName)).getStringView());
                dataBuilder.append(",");
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                dataBuilder.append(entity.getMember(memberName).getValue());
                dataBuilder.append(",");
            }
        }
        dataBuilder.append("\n");
    }

    protected void createDownloadable() {
        Downloadable d = new Downloadable(dataStored.getBytes(), "text/csv");
        d.save(getFileName());
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (formatCompleate) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink("/download/" + System.currentTimeMillis() + "/" + getFileName());
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

}
