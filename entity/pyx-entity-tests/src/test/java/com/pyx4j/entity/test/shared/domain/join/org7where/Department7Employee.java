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
 * Created on Jan 30, 2016
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.join.org7where;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Table(prefix = "test")
public interface Department7Employee extends IEntity {

    public enum Employee7Type {

        manager,

        director,

        employee

    }

    Employee7 employee();

    Department7 department();

    interface Employee7TypeColumnId extends ColumnId {
    }

    @JoinColumn(Employee7TypeColumnId.class)
    @MemberColumn(name = "tp")
    IPrimitive<Employee7Type> type();

}
