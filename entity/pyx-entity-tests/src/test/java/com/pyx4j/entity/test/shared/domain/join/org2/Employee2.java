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
package com.pyx4j.entity.test.shared.domain.join.org2;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

@Table(prefix = "test")
public interface Employee2 extends IEntity {

    IPrimitive<String> testId();

    @Indexed
    IPrimitive<String> name();

    interface Department2ColumnId extends ColumnId {
    }

    @JoinColumn(Department2ColumnId.class)
    Department2 department();

    interface Manager2ColumnId extends ColumnId {
    }

    @Indexed
    @Caption(description = "Boss")
    @JoinColumn(Manager2ColumnId.class)
    Employee2 manager();

    @JoinTable(value = Employee2.class, mappedBy = Manager2ColumnId.class)
    @Detached(level = AttachLevel.Detached)
    ISet<Employee2> employees();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<Association2> associations();

}
