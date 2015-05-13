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
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.CursorSource;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

public class SheetCreationDeferredProcess<E extends IEntity> extends AbstractDeferredProcess {

    private static final long serialVersionUID = 1L;

    private final EntityListCriteria<E> criteria;

    private final CursorSource<E> cursorSource;

    private final ReportTableFormatter formatter;

    private final EntityReportFormatter<E> entityFormatter;

    private final String fileName;

    public SheetCreationDeferredProcess(EntityListCriteria<E> criteria, CursorSource<E> cursorSource, ReportTableFormatter formatter,
            EntityReportFormatter<E> entityFormatter, String fileName) {
        super();
        this.criteria = criteria;
        this.cursorSource = cursorSource;
        this.formatter = formatter;
        this.entityFormatter = entityFormatter;
        this.fileName = fileName;
    }

    @Override
    public void execute() {
        entityFormatter.createHeader(formatter);
        new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() throws RuntimeException {
                ICursorIterator<E> cursor = null;
                try {
                    cursor = cursorSource.getTOCursor(null, criteria, AttachLevel.Attached);
                    while (cursor.hasNext()) {
                        E model = cursor.next();
                        entityFormatter.reportEntity(formatter, model);

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
        });

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
