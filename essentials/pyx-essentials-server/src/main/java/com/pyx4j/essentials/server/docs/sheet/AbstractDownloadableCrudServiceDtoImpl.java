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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.DocCreationRequest;
import com.pyx4j.entity.rpc.DocCreationService;
import com.pyx4j.entity.rpc.SheetCreationRequest;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.essentials.rpc.download.DownloadableService;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.rpc.shared.DeferredCorrelationId;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractDownloadableCrudServiceDtoImpl<BO extends IEntity, TO extends IEntity> extends AbstractCrudServiceDtoImpl<BO, TO>
        implements DocCreationService, DownloadableService {

    protected AbstractDownloadableCrudServiceDtoImpl(Class<BO> entityClass, Class<TO> dtoClass) {
        super(entityClass, dtoClass);
    }

    protected AbstractDownloadableCrudServiceDtoImpl(EntityBinder<BO, TO> binder) {
        super(binder);
    }

    @Override
    public final void startDocCreation(AsyncCallback<DeferredCorrelationId> callback, DocCreationRequest docCreationRequest) {
        callback.onSuccess(startDocCreation(docCreationRequest));
    }

    @Override
    public void cancelDownload(AsyncCallback<VoidSerializable> callback, String downloadUrl) {
        String fileName = Downloadable.getDownloadableFileName(downloadUrl);
        if (fileName != null) {
            Downloadable.cancel(fileName);
        }
        callback.onSuccess(null);
    }

    public final DeferredCorrelationId startDocCreation(DocCreationRequest docCreationRequest) {
        if (docCreationRequest instanceof SheetCreationRequest) {
            @SuppressWarnings("unchecked")
            SheetCreationRequest<TO> sheetCreationRequest = (SheetCreationRequest<TO>) docCreationRequest;

            SheetCreationProcessBuilder<TO, TO> builder = SheetCreationProcessBuilder.create(toClass);
            builder.withCriteria(sheetCreationRequest.getQeueryCriteria());
            builder.withCursorSource(this);
            builder.withSelectedColumns(sheetCreationRequest);
            builder.withFileName(EntityFactory.getEntityMeta(toClass).getCaption() + "."
                    + DownloadFormat.fromSheetFormat(sheetCreationRequest.getSheetFormat()).getExtension());

            return new DeferredCorrelationId(DeferredProcessRegistry.fork(builder.build(), DeferredProcessRegistry.THREAD_POOL_DOWNLOADS));
        } else {
            throw new IllegalArgumentException();
        }
    }

}
