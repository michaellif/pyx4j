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
 * Created on 2012-03-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.version;

import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.test.shared.domain.version.OwnedByVerOneToManyParent.OwnedByVerOneToManyParentV;

@Table(prefix = "test")
public interface OwnedByVerOneToManyParent extends IVersionedEntity<OwnedByVerOneToManyParentV> {

    IPrimitive<String> testId();

    @ToString(index = 0)
    IPrimitive<String> name();

    @Table(prefix = "test")
    public interface OwnedByVerOneToManyParentV extends IVersionData<OwnedByVerOneToManyParent> {

        IPrimitive<String> testId();

        @ToString(index = 1)
        IPrimitive<String> name();

        @Owned
        IList<OwnedByVerOneToManyUChild> childrenU();

        @Owned
        IList<OwnedByVerOneToManyMChild> childrenM();

        @EmbeddedEntity
        OwnedByVerOneToManyEChild childE();
    }

}
