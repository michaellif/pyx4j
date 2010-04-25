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

public class MemberEntityColumnDescriptor<E extends IEntity> extends MemberColumnDescriptor<E> {

    public MemberEntityColumnDescriptor(Path columnPath, String columnTitle, String formatPattern) {
        super(columnPath, columnTitle, formatPattern);
    }

    @Override
    public String convert(E entity) {
        return ((IEntity) entity.getMember(getColumnPath())).getStringView();
    }

}
