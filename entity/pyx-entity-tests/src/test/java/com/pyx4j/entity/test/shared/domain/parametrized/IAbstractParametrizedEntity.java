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
 * Created on Feb 16, 2012
 * @author vlads
 */
package com.pyx4j.entity.test.shared.domain.parametrized;

import java.io.Serializable;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;

@AbstractEntity(generateMetadata = false)
public interface IAbstractParametrizedEntity<E extends IEntity, T extends Serializable> extends IEntity {

    IPrimitive<String> name();

    IPrimitive<T> pvalue();

    IPrimitiveSet<T> pvalues();

    E entity();

    IList<E> entities();
}
