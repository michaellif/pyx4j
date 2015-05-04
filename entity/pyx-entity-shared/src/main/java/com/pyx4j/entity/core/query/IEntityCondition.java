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
 * Created on May 4, 2015
 * @author vlads
 */
package com.pyx4j.entity.core.query;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ISet;

@DiscriminatorValue("Entity")
public interface IEntityCondition<E extends IEntity> extends ICondition {

    // has a set of PK of selected entities.

    IPrimitiveSet<Key> refs();

    @Transient
    ISet<E> references();
}
