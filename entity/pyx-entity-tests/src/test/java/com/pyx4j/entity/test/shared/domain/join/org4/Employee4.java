/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 27, 2014
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.join.org4;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;

@Table(prefix = "test")
public interface Employee4 extends IEntity {

    IPrimitive<String> testId();

    IPrimitive<String> name();

    @JoinTable(value = Department4.class, mappedBy = Department4.Employee3ColumnId.class)
    @Detached(level = AttachLevel.Detached)
    ISet<Department4> departments();

}
