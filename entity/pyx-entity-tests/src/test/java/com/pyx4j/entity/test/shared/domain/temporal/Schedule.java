/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-04-26
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.temporal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Table(prefix = "test")
public interface Schedule extends IEntity {

    @Indexed
    IPrimitive<String> name();

    @Format("HH:mm")
    @MemberColumn(name = "tm")
    @Indexed
    IPrimitive<java.sql.Time> time();

    @Format("MM/dd/yyyy")
    @Indexed
    IPrimitive<java.sql.Date> startsOn();

    @Format("MM/dd/yyyy")
    @Indexed
    IPrimitive<LogicalDate> endsOn();
}
