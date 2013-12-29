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
 * Created on Jan 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;

@Table(prefix = "test")
//@Cached
public interface Task extends IEntity {

    @Override
    @Caption(name = "Task Id")
    IPrimitive<Key> id();

    IPrimitive<Boolean> finished();

    @ToString
    @BusinessEqualValue
    @Indexed
    IPrimitive<String> description();

    @ToString(index = 1)
    IPrimitive<Status> status();

    @BusinessEqualValue
    IPrimitive<Date> deadLine();

    @Indexed
    IPrimitiveSet<String> notes();

    IPrimitiveSet<Status> oldStatus();
}
