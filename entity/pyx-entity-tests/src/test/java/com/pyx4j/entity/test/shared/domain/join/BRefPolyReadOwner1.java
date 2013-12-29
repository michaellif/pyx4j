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
 * Created on Jan 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.join;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

@DiscriminatorValue("O1")
@Table(prefix = "test")
public interface BRefPolyReadOwner1 extends IEntity, BRefPolyReadAbstractOwner {

    IPrimitive<String> testId();

    IPrimitive<String> name();

    @JoinTable(value = BRefPolyReadChild.class)
    @OrderBy(BRefPolyReadChild.SortColumnId.class)
    IList<BRefPolyReadChild> children();
}
