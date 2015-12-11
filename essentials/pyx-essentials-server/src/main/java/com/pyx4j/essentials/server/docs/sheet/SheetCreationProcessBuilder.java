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
 * Created on Dec 7, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.docs.sheet;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityColumnDescriptor;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.SheetCreationRequest;
import com.pyx4j.entity.server.cursor.CursorSource;
import com.pyx4j.entity.server.cursor.SimpleCursorSource;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.essentials.server.download.MimeMap;

/**
 * Helper class for common operations required in Sheet creation on back-end.
 */
public class SheetCreationProcessBuilder<TO extends IEntity, MODEL extends IEntity> {

    private final Class<TO> entityClass;

    private final Class<MODEL> modleClass;

    private EntityQueryCriteria<TO> criteria;

    private CursorSource<TO> cursorSource;

    private ReportTableFormatter formatter;

    private EntityBinder<TO, MODEL> modelBinder = null;

    private ReportModelFormatter<MODEL> modelFormatter;

    private String fileName;

    public static <TO extends IEntity> SheetCreationProcessBuilder<TO, TO> create(Class<TO> entityClass) {
        return new SheetCreationProcessBuilder<TO, TO>(entityClass, entityClass);
    }

    public static <TO extends IEntity, MODEL extends IEntity> SheetCreationProcessBuilder<TO, MODEL> create(Class<TO> entityClass, Class<MODEL> modleClass) {
        return new SheetCreationProcessBuilder<TO, MODEL>(entityClass, modleClass);
    }

    private SheetCreationProcessBuilder(Class<TO> entityClass, Class<MODEL> modleClass) {
        this.entityClass = entityClass;
        this.modleClass = modleClass;
    }

    public SheetCreationProcessBuilder<TO, MODEL> withCriteria(EntityQueryCriteria<TO> criteria) {
        this.criteria = criteria;
        return this;
    }

    public SheetCreationProcessBuilder<TO, MODEL> withCursorSource(CursorSource<TO> cursorSource) {
        this.cursorSource = cursorSource;
        return this;
    }

    public SheetCreationProcessBuilder<TO, MODEL> withTableFormatter(ReportTableFormatter reportTableFormatter) {
        this.formatter = reportTableFormatter;
        return this;
    }

    public SheetCreationProcessBuilder<TO, MODEL> withModelBinder(EntityBinder<TO, MODEL> modelBinder) {
        this.modelBinder = modelBinder;
        return this;
    }

    public SheetCreationProcessBuilder<TO, MODEL> withModelFormatter(ReportModelFormatter<MODEL> modelFormatter) {
        this.modelFormatter = modelFormatter;
        return this;
    }

    public static <TO extends IEntity> void selectListerMemebers(EntityReportFormatter<TO> entityFormatter, SheetCreationRequest<TO> sheetCreationRequest) {
        for (EntityColumnDescriptor cd : sheetCreationRequest.getColumnDescriptors()) {
            entityFormatter.selectMemeber(cd.getPath(), cd.getTitle());
        }
    }

    @SuppressWarnings("unchecked")
    public SheetCreationProcessBuilder<TO, MODEL> withSelectedColumns(SheetCreationRequest<TO> sheetCreationRequest) {
        selectListerMemebers((EntityReportFormatter<TO>) modelFormatter(), sheetCreationRequest);
        return this;
    }

    public SheetCreationProcessBuilder<TO, MODEL> withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ReportModelFormatter<MODEL> modelFormatter() {
        if (modelFormatter == null) {
            modelFormatter = new EntityReportFormatter<MODEL>(modleClass);
        }
        return modelFormatter;
    }

    public SheetCreationDeferredProcess<TO, MODEL> build() {
        if (cursorSource == null) {
            cursorSource = new SimpleCursorSource<TO>();
        }
        if (formatter == null) {
            formatter = new ReportTableXLSXFormatter();
        }
        if (fileName == null) {
            String extension = MimeMap.getExtension(formatter.getContentType());
            fileName = EntityFactory.getEntityMeta(entityClass).getCaption() + "." + extension;
        }
        return new SheetCreationDeferredProcess<>(criteria, cursorSource, formatter, modelBinder, modelFormatter(), fileName);
    }

}
