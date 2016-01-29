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
 * Created on Jan 29, 2016
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.join.org6where;

import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.JoinWhere;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.test.shared.domain.join.org5where.Employee5.Employee5TypeColumnId;

@Table(prefix = "test")
public interface Department6 extends IEntity {

    IPrimitive<String> testId();

    IPrimitive<String> name();

    @JoinTable(value = Employee6.class, where = { @JoinWhere(column = Employee5TypeColumnId.class, value = "manager") })
    Employee6 manager();

    @JoinTable(value = Employee6.class, where = { @JoinWhere(column = Employee5TypeColumnId.class, value = "managerFormer") })
    Employee6 managerFormer();

    @JoinTable(value = Employee6.class, where = { @JoinWhere(column = Employee5TypeColumnId.class, value = "employee") })
    ISet<Employee6> employees();
}
