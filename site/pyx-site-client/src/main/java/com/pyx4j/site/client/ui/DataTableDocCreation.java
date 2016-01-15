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
package com.pyx4j.site.client.ui;

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityColumnDescriptor;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.DocCreationService;
import com.pyx4j.entity.rpc.SheetCreationRequest;
import com.pyx4j.entity.rpc.SheetCreationRequest.SheetFormat;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ReportDialog;

/**
 * Default Export functionality. bridge for Old and new ColumnDescriptor (code changes are localized here)
 */
public class DataTableDocCreation {

    public static <E extends IEntity> void createExcelExport(DocCreationService service, EntityQueryCriteria<E> criteria,
            List<ColumnDescriptor> columnDescriptors) {
        // TODO use plural of Entity name
        ReportDialog reportDialog = new ReportDialog("Export", "Exporting...");

        SheetCreationRequest<E> request = new SheetCreationRequest<E>();
        request.setQeueryCriteria(criteria);
        request.setSheetFormat(SheetFormat.XLSX);
        request.setColumnDescriptors(new Vector<EntityColumnDescriptor>());

        for (ColumnDescriptor columnDescriptor : columnDescriptors) {
            if (!columnDescriptor.isSearchableOnly()) {
                EntityColumnDescriptor d = new EntityColumnDescriptor();
                d.setPath(columnDescriptor.getColumnPath());
                d.setTitle(columnDescriptor.getColumnTitle());
                request.getColumnDescriptors().add(d);
            }
        }

        reportDialog.start(service, request);
    }
}
