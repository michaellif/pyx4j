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
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.join.org1;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;

@Table(prefix = "test")
public interface Employee1 extends IEntity {

    IPrimitive<String> testId();

    @Indexed
    IPrimitive<String> name();

    interface Department1ColumnId extends ColumnId {
    }

    @JoinColumn(Department1ColumnId.class)
    Department1 department();

    interface Manager1ColumnId extends ColumnId {
    }

    @Indexed
    @Caption(description = "Boss")
    @JoinColumn(Manager1ColumnId.class)
    Employee1 manager();

    @JoinTable(value = Employee1.class, mappedBy = Manager1ColumnId.class)
    @Detached(level = AttachLevel.Detached)
    ISet<Employee1> employees();

}
