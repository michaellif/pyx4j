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
 */
package com.pyx4j.entity.test.shared.domain.detached;

import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

@Table(prefix = "test")
public interface MainHolderEnity extends IEntity {

    IPrimitive<String> name();

    @Owned
    MainEnity ownedEntity();

    @Owned
    IList<MainEnity> ownedEntities();

    @Owned
    @MemberColumn(name = "obr")
    OwnedWithBackReference ownedWithBackReference();

    @Owned
    @MemberColumn(name = "obrl")
    IList<OwnedInListWithBackReference> ownedWithBackReferenceList();
}
