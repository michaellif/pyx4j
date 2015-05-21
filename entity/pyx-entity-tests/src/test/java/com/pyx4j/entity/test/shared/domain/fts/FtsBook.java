/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 14, 2015
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.fts;

import com.pyx4j.entity.annotations.Adapters;
import com.pyx4j.entity.annotations.CascadeType;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.shared.adapters.UpdateTextSearchIndexModificationAdapter;

@Table(prefix = "test")
@Adapters(entityModificationAdapters = UpdateTextSearchIndexModificationAdapter.class)
public interface FtsBook extends IEntity {

    IPrimitive<String> testId();

    @ToString
    IPrimitive<String> title();

    @JoinTable(value = FtsAuthorBook.class, cascade = CascadeType.ALL)
    @Detached(level = AttachLevel.Detached)
    ISet<FtsAuthor> authors();

    @Detached(level = AttachLevel.Detached)
    @Owned(cascade = {})
    FtsBookIndex fts();
}
