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
import com.pyx4j.entity.shared.Path;

public class MemberDateColumnDescriptor<E extends IEntity> extends MemberColumnDescriptor<E> {

    public static String DEFAULT_DATE_TIME_FORMAT = "EEE, MMM d, yyyy - h:mm a";

    public static String DEFAULT_DATE_FORMAT = "MMM d, yyyy";

    public MemberDateColumnDescriptor(Path columnPath, String columnTitle, String dateTimeFormatPattern) {
        super(columnPath, columnTitle, (dateTimeFormatPattern == null) ? DEFAULT_DATE_TIME_FORMAT : dateTimeFormatPattern);
    }

    public MemberDateColumnDescriptor(Path columnPath, String columnTitle, String dateTimeFormatPattern, String defaultDateTimeFormatPattern) {
        super(columnPath, columnTitle, (dateTimeFormatPattern == null) ? defaultDateTimeFormatPattern : dateTimeFormatPattern);
    }

    @Override
    public String convert(E entity) {
        Object value = entity.getMember(getColumnPath()).getValue();
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
        return DateTimeFormat.getFormat(getFormatPattern()).format(date);
    }

}
