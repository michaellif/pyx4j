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
 * Created on May 8, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import java.util.Collection;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.domain.Entity;
import com.pyx4j.shared.dataObjects.ReportColumnDescriptor;
import com.pyx4j.shared.dataObjects.ReportColumnDescriptorNonSortable;
import com.pyx4j.shared.dataObjects.ReportDateColumnDescriptor;
import com.pyx4j.shared.reports.EntityConverterUtils;

/**
 * Show reports Column in client Application
 */
public class ColumnDescriptorReportAdapter<E> extends ColumnDescriptor<E> {

    private final ReportColumnDescriptor<? super E> reportColumnDescriptor;

    private static DateTimeFormat dateFormatter = DateTimeFormat.getFormat("EEE, MMM d, yyyy - h:mm a");

    public ColumnDescriptorReportAdapter(ReportColumnDescriptor<? super E> reportColumnDescriptor) {
        super(reportColumnDescriptor.getColumnName(), reportColumnDescriptor.getColumnTitle());
        this.reportColumnDescriptor = reportColumnDescriptor;
        if (reportColumnDescriptor instanceof ReportColumnDescriptorNonSortable) {
            this.setSortable(false);
        }
    }

    public ReportColumnDescriptor<? super E> getReportColumnDescriptor() {
        return this.reportColumnDescriptor;
    }

    @Override
    public String convert(E entity) {
        Object value = reportColumnDescriptor.getColumnValue(entity);
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Entity) {
            return ((Entity) value).getShortEntityDescription();
        } else if (reportColumnDescriptor instanceof ReportDateColumnDescriptor<?>) {
            Date date = null;
            if (value instanceof Date) {
                date = (Date) value;
            } else if (value instanceof Long) {
                date = new Date(((Long) value).longValue());
            }
            return DateTimeFormat.getFormat(((ReportDateColumnDescriptor<?>) reportColumnDescriptor).getDateTimeFormatPattern()).format(date);
        } else if (value instanceof Date) {
            return dateFormatter.format((Date) value);
        } else if (value instanceof Collection<?>) {
            Object item = ConverterUtils.collectionFirstElement((Collection<?>) value);
            if (item instanceof Entity) {
                return "[" + EntityConverterUtils.convertEntityCollection((Collection<Entity>) value) + "]";
            } else if (item instanceof String) {
                return "[" + ConverterUtils.convertStringCollection((Collection<String>) value) + "]";
            } else {
                return "[]";
            }
        } else {
            return value.toString();
        }
    }
}
