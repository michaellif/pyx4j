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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class MemberColumnDescriptor<E extends IEntity> extends ColumnDescriptor<E> {

    private final Path columnPath;

    private String formatPattern;

    public MemberColumnDescriptor(Path columnPath, String columnTitle, String formatPattern) {
        super(columnPath.toString(), columnTitle);
        this.columnPath = columnPath;
        this.formatPattern = formatPattern;
    }

    public Path getColumnPath() {
        return columnPath;
    }

    public String getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    @Override
    public String convert(E entity) {
        Object value = entity.getMember(columnPath).getValue();
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

}
