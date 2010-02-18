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
 * Created on Feb 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.entity.shared.IEntity;

public class MemberDateColumnDescriptor<E extends IEntity<IEntity<?>>> extends ColumnDescriptor<E> {

    private String dateTimeFormatPattern;

    public MemberDateColumnDescriptor(String columnName, String columnTitle, String dateTimeFormatPattern) {
        super(columnName, columnTitle);
        if (dateTimeFormatPattern == null) {
            dateTimeFormatPattern = "EEE, MMM d, yyyy - h:mm a";
        }
    }

    public String getDateTimeFormatPattern() {
        return dateTimeFormatPattern;
    }

    public void setDateTimeFormatPattern(String dateTimeFormatPattern) {
        this.dateTimeFormatPattern = dateTimeFormatPattern;
    }

    @Override
    public String convert(E entity) {
        Object value = entity.getMemberValue(getColumnName());
        if (value == null) {
            return "";
        }
        Date date = null;
        if (value instanceof Date) {
            date = (Date) value;
        } else if (value instanceof Long) {
            date = new Date(((Long) value).longValue());
        } else {
            return value.toString();
        }
        return DateTimeFormat.getFormat(getDateTimeFormatPattern()).format(date);
    }

}
