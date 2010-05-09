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
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.security.shared.SecurityController;

public class SearchReportDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -7944873735643401186L;

    private final static Logger log = LoggerFactory.getLogger(SearchReportDeferredProcess.class);

    private final StringBuilder data = new StringBuilder();

    private final EntitySearchCriteria<?> request;

    private String encodedCursorRefference;

    private final Class<? extends IEntity> entityClass;

    private int fetchCount = 0;

    private boolean fetchCompleate;

    private boolean formatCompleate;

    public SearchReportDeferredProcess(EntitySearchCriteria<?> request) {
        SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
        this.request = request;
        this.request.setPageSize(0);
        this.entityClass = ServerEntityFactory.entityClass(request.getDomainName());
        formatHeader();
    }

    @Override
    public void cancel() {
        // TODO Auto-generated method stub
    }

    @Override
    public void execute() {
        if (fetchCompleate) {
            createDownloadable();
            formatCompleate = true;
        } else {
            long start = System.currentTimeMillis();
            IndexedEntitySearch search = new IndexedEntitySearch(request);
            search.buildQueryCriteria();
            SearchResultIterator<IEntity> it = search.getResult(encodedCursorRefference);
            while (it.hasNext()) {
                IEntity ent = it.next();
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
                formatEntity(ent);
                fetchCount++;
                if (System.currentTimeMillis() - start > Consts.SEC2MSEC * 20) {
                    log.warn("Executions time quota exceeded {}", System.currentTimeMillis() - start);
                    log.debug("fetch will continue {}; data size {}", fetchCount, data.length());
                    encodedCursorRefference = it.encodedCursorRefference();
                    return;
                }
            }
            log.debug("fetch compleate {}; data size {}", fetchCount, data.length());
            fetchCompleate = true;
        }
    }

    protected String getFileName() {
        return EntityFactory.getEntityMeta(entityClass).getCaption() + ".csv";
    }

    protected void formatHeader() {
        EntityMeta em = EntityFactory.getEntityMeta(entityClass);
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (em.getMemberMeta(memberName).isRpcTransient()) {
                continue;
            } else if (memberMeta.isEntity()) {
                data.append(memberMeta.getCaption());
                data.append(",");
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                data.append(memberMeta.getCaption());
                data.append(",");
            }
        }
        data.append("\n");
    }

    protected void formatEntity(IEntity entity) {
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (em.getMemberMeta(memberName).isRpcTransient()) {
                continue;
            } else if (memberMeta.isEntity()) {
                data.append(((IEntity) entity.getMember(memberName)).getStringView());
                data.append(",");
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                data.append(entity.getMember(memberName).getValue());
                data.append(",");
            }
        }
        data.append("\n");
    }

    protected void createDownloadable() {
        Downloadable d = new Downloadable(data.toString().getBytes(), "text/csv");
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
            return r;
        }
    }

}
