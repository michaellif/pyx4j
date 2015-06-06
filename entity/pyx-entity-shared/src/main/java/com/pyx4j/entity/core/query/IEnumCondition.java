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
 * Created on June 5, 2015
 * @author michaellif
 */
package com.pyx4j.entity.core.query;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitiveSet;

@DiscriminatorValue("Enum")
@AbstractEntity(generateMetadata = false)
public interface IEnumCondition<E extends Enum<E>> extends ICondition {

    IPrimitiveSet<String> keys();

    @Transient
    IPrimitiveSet<E> values();
}
