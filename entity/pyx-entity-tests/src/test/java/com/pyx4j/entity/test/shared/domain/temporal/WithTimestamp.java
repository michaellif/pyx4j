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
 * Created on 2011-04-28
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.temporal;

import java.util.Date;

import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Table(prefix = "test")
public interface WithTimestamp extends IEntity {

    @ToString
    @Indexed
    IPrimitive<String> name();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    @Timestamp
    IPrimitive<Date> updated();

}
