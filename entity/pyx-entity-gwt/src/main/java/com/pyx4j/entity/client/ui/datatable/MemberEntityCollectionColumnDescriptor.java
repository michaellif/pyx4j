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

import java.util.Collection;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.entity.shared.IEntity;

public class MemberEntityCollectionColumnDescriptor<E extends IEntity> extends ColumnDescriptor<E> {

    private static class StringConverter implements ToStringConverter<IEntity> {

        public String toString(IEntity value) {
            return value.getStringView();
        }
    }

    public MemberEntityCollectionColumnDescriptor(String columnName, String columnTitle) {
        super(columnName, columnTitle);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String convert(E entity) {
        Object value = entity.getMemberValue(getColumnName());
        if (value == null) {
            return "";
        } else if (value instanceof Collection<?>) {
            return ConverterUtils.convertCollection((Collection<IEntity>) value, new StringConverter());
        } else {
            return value.toString();
        }
    }

}
