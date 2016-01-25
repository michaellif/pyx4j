/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Feb 2, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.docs.sheet;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Executables;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.server.cursor.CursorSource;
import com.pyx4j.entity.shared.utils.BindingContext;
import com.pyx4j.entity.shared.utils.BindingContext.BindingType;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

public final class SheetCreationDeferredProcess<TO extends IEntity, MODEL extends IEntity> extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final EntityQueryCriteria<TO> criteria;

    private final CursorSource<TO> cursorSource;

    private final ReportTableFormatter formatter;

    private final EntityBinder<TO, MODEL> modelBinder;

    private final ReportModelFormatter<MODEL> modelFormatter;

    private final String fileName;

    /**
     * Better uSe SheetCreationProcessBuilder
     *
     * @param criteria
     *            TO criteria
     * @param cursorSource
     *            CrudService
     * @param formatter
     *            XLSX or HML table formatter.
     * @param binder
     *            Optional converted from TO to MODLE if they are of a different type.
     * @param modelFormatter
     * @param fileName
     */
    public SheetCreationDeferredProcess(EntityQueryCriteria<TO> criteria, CursorSource<TO> cursorSource, //
            ReportTableFormatter formatter, EntityBinder<TO, MODEL> modelBinder, ReportModelFormatter<MODEL> modelFormatter, //
            String fileName) {
        super();
        this.criteria = criteria;
        this.cursorSource = cursorSource;
        this.formatter = formatter;
        this.modelBinder = modelBinder;
        this.modelFormatter = modelFormatter;
        this.fileName = fileName;
    }

    @Override
    public void execute() {
        modelFormatter.createHeader(formatter);
        Executable<Void, RuntimeException> task = new Executable<Void, RuntimeException>() {

            @SuppressWarnings("unchecked")
            @Override
            public Void execute() throws RuntimeException {
                // TODO maximum = Persistence.service().count(criteria);

                ICursorIterator<TO> cursor = null;
                try {
                    cursor = cursorSource.getCursor(null, criteria, AttachLevel.Attached);
                    while (cursor.hasNext()) {
                        TO to = cursor.next();
                        MODEL model;
                        if (modelBinder == null) {
                            model = (MODEL) to;
                        } else {
                            model = modelBinder.createTO(to, new BindingContext(BindingType.List));
                        }
                        modelFormatter.reportEntity(formatter, model);

                        progress.progress.addAndGet(1);

                        if (canceled) {
                            break;
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(cursor);
                }
                return null;
            }
        };

        task = Executables.wrapInEntityNamespace(criteria.getEntityClass(), task);

        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(task);

        modelFormatter.createFooter(formatter);

        if (!canceled) {
            Downloadable d = new Downloadable(formatter.getBinaryData(), formatter.getContentType());
            d.save(fileName);
            completed = true;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        if (completed) {
            DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            return r;
        } else {
            return super.status();
        }
    }
}
