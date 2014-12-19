/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 27, 2013
 * @author vlads
 */
package com.pyx4j.essentials.server.docs.sheet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractDownloadableCrudServiceDtoImpl<E extends IEntity, DTO extends IEntity> extends AbstractCrudServiceDtoImpl<E, DTO> implements
        ReportService<DTO> {

    protected AbstractDownloadableCrudServiceDtoImpl(Class<E> entityClass, Class<DTO> dtoClass) {
        super(entityClass, dtoClass);
    }

    @Override
    public void createDownload(AsyncCallback<String> callback, ReportRequest reportRequest) {
        EntityListCriteria<E> criteria = EntityListCriteria.create(boClass);
        criteria.setVersionedCriteria(reportRequest.getCriteria().getVersionedCriteria());
        enhanceListCriteria(criteria, (EntityListCriteria<DTO>) reportRequest.getCriteria());
        reportRequest.setCriteria(criteria);

        if (ApplicationBackend.getBackendType() == ApplicationBackendType.GAE) {
            callback.onSuccess(DeferredProcessRegistry.register(new SearchReportDeferredProcess<E>(reportRequest)));
        } else {
            callback.onSuccess(DeferredProcessRegistry.fork(new SearchReportDeferredProcess<E>(reportRequest), DeferredProcessRegistry.THREAD_POOL_DOWNLOADS));
        }
    }

    @Override
    public void cancelDownload(AsyncCallback<VoidSerializable> callback, String downloadUrl) {
        String fileName = Downloadable.getDownloadableFileName(downloadUrl);
        if (fileName != null) {
            Downloadable.cancel(fileName);
        }
        callback.onSuccess(null);
    }

}
