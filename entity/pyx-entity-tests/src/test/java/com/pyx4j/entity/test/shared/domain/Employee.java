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
package com.pyx4j.entity.test.shared.domain;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

@Caption(name = "Laborer")
public interface Employee extends IEntity {

    public static int DECLARED_MEMEBERS = 15;

    public static enum EmploymentStatus {
        DISMISSED, FULL_TIME, PART_TIME, CONTRACT
    }

    IPrimitive<String> firstName();

    IPrimitive<Date> hiredate();

    IPrimitive<Boolean> reliable();

    IPrimitive<Long> holidays();

    IPrimitive<Integer> rating();

    IPrimitive<Double> salary();

    IPrimitive<EmploymentStatus> employmentStatus();

    @RpcTransient
    IPrimitive<Status> accessStatus();

    @Owned
    ISet<Task> tasks();

    @Owned
    IList<Task> tasksSorted();

    Department department();

    Employee manager();

    @Owned
    @NotNull
    @Caption(name = "Home address")
    Address homeAddress();

    @Owned
    @EmbeddedEntity
    Address workAddress();

    IPrimitive<byte[]> image();

}
