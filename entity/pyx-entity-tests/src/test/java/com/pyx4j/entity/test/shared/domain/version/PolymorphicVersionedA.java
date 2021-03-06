/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-09-21
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.version;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.test.shared.domain.version.PolymorphicVersionedA.PolymorphicVersionDataA;

@DiscriminatorValue("A")
public interface PolymorphicVersionedA extends PolymorphicVersionedSuper<PolymorphicVersionDataA> {

    IPrimitive<String> dataA();

    @DiscriminatorValue("A")
    public interface PolymorphicVersionDataA extends PolymorphicVersionedSuper.PolymorphicVersionDataSuper<PolymorphicVersionedA> {

        IPrimitive<String> dataAv();
    }
}
