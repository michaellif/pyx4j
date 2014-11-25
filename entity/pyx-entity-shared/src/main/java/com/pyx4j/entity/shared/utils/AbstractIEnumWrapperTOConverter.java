/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Nov 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.IEnumWrapperTO;

public class AbstractIEnumWrapperTOConverter<E extends Enum<E>, T extends IEnumWrapperTO<E>> {

    protected final Class<T> entityClass;

    public AbstractIEnumWrapperTOConverter(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> toTO(Collection<E> c) {
        List<T> r = new Vector<>(); // Vector to make it serializable
        for (E b : c) {
            T dto = EntityFactory.create(entityClass);
            dto.setPrimaryKey(new Key(b.ordinal() + 1));
            dto.value().setValue(b);
            r.add(dto);
        }
        return r;
    }

    public Collection<E> toBO(Collection<T> src) {
        Collection<E> dst = new Vector<>(); // Vector to make it serializable
        for (T dto : src) {
            dst.add(dto.value().getValue());
        }
        return dst;
    }

}
