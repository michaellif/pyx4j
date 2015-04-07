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
 */
package com.pyx4j.entity.test.shared.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@Caption(name = "Laborer")
@Table(prefix = "test")
public interface Employee extends IEntity {

    public static int DECLARED_MEMBERS = 18;

    public static String[] MEMBERS_ORDER = new String[] { "firstName", "from", "reliable", "holidays", "rating", "flagByte", "flagShort", "flagDouble",
            "salary", "employmentStatus", "accessStatus", "tasks", "tasksSorted", "department", "manager", "employees", "homeAddress", "workAddress" };

    @I18n
    public static enum EmploymentStatus {

        DISMISSED, FULL_TIME, PART_TIME, CONTRACT;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    @Indexed
    @ToString
    @Length(100)
    IPrimitive<String> firstName();

    @Indexed
    @Caption(name = "Hire Date")
    @MemberColumn(name = "hiredate")
    IPrimitive<Date> from();

    IPrimitive<Boolean> reliable();

    @Indexed
    IPrimitive<Long> holidays();

    @Indexed
    IPrimitive<Integer> rating();

    IPrimitive<Byte> flagByte();

    IPrimitive<Short> flagShort();

    @Indexed
    IPrimitive<Double> flagDouble();

    @Indexed
    IPrimitive<BigDecimal> salary();

    IPrimitive<EmploymentStatus> employmentStatus();

    @RpcTransient
    IPrimitive<Status> accessStatus();

    @Owned
    ISet<Task> tasks();

    @Owned
    IList<Task> tasksSorted();

    // TODO fix stack overflow
    @Detached(level = AttachLevel.IdOnly)
    Department department();

    interface ManagerColumnId extends ColumnId {
    }

    @Indexed
    @Caption(description = "Boss")
    @JoinColumn(ManagerColumnId.class)
    Employee manager();

    @JoinTable(value = Employee.class, mappedBy = ManagerColumnId.class)
    // TODO fix stack overflow
    @Detached(level = AttachLevel.Detached)
    ISet<Employee> employees();

    @Owned
    @Caption(name = "Mail address")
    Address homeAddress();

    @Owned
    @EmbeddedEntity
    Address workAddress();

}
