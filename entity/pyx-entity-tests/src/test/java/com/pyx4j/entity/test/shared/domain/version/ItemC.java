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
 * Created on Feb 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.version;

import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.test.shared.domain.version.ItemC.ItemCVersion;

//TODO Not supported!
@Deprecated
@ToStringFormat("{0} - {1}")
@Table(prefix = "test")
public interface ItemC extends IVersionedEntity<ItemCVersion> {

    IPrimitive<String> testId();

    @ToString(index = 0)
    IPrimitive<String> name();

    @Owner
    ItemD itemD();

    public interface ItemCVersion extends IVersionData<ItemC> {

        IPrimitive<String> testId();

        @ToString(index = 1)
        IPrimitive<String> name();

    }

}
