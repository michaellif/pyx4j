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
 * Created on Feb 8, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain.ownership.cascade;

import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Table(prefix = "test")
public interface BidirectionalOneToManyNCPChild extends IEntity {

    @Owner
    @JoinColumn
    BidirectionalOneToManyNCPParent parent();

    @OrderColumn
    IPrimitive<Integer> orderInParent();

    IPrimitive<String> testId();

    IPrimitive<String> name();

}