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
 * Created on Apr 21, 2015
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.query.IDateCondition;
import com.pyx4j.entity.core.query.IEntityCondition;
import com.pyx4j.entity.core.query.IEnumCondition;
import com.pyx4j.entity.core.query.IQuery;
import com.pyx4j.entity.core.query.IStringCondition;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;

@Transient
public interface EmployeeQuery extends IQuery<Employee> {

    IStringCondition firstName();

    IDateCondition hireDate();

    //TODO See PYX-14.
    @DiscriminatorValue("Entity.text.Department")
    public interface DepartmentEntityCondition extends IEntityCondition<Department> {

    }

    DepartmentEntityCondition department();

    @DiscriminatorValue("Enum.text.EmploymentStatus")
    public interface EmploymentStatusCondition extends IEnumCondition<EmploymentStatus> {

    }

    EmploymentStatusCondition employmentStatus();
}
