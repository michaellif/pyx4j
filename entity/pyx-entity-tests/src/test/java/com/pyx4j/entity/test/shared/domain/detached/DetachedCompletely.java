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
 * Created on Sep 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.detached;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.Timestamp.Update;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

@Table(prefix = "test")
public interface DetachedCompletely extends IEntity {

    IPrimitive<String> testId();

    IPrimitive<String> name();

    @Timestamp(Update.Updated)
    IPrimitive<Date> updated();

    @Owned
    @Detached(level = AttachLevel.Detached)
    DetachedCompletelyChildS child();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<DetachedCompletelyChildM> children();

}
