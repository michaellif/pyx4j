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
 * Created on 2011-01-04
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.inherit.single;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.core.IPrimitive;

@DiscriminatorValue("C2")
public interface SConcrete2 extends SBase {

    IPrimitive<String> nameC2();

    interface ReferenceMasterColumnId extends ColumnId {
    }

    @JoinColumn(ReferenceMasterColumnId.class)
    @MemberColumn(name = "mstr")
    SReferenceToSubType master();
}
