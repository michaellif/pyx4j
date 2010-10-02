/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Oct 1, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.adapters.index;

import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.shared.meta.MemberMeta;

public abstract class AbstractIndexAdapter<E> implements IndexAdapter<E> {

    @Override
    public String getIndexedColumnName(MemberMeta memberMeta) {
        Indexed index = memberMeta.getAnnotation(Indexed.class);
        if (index.global() != 0) {
            return ENTITY_KEYWORD_PRROPERTY;
        } else {
            return memberMeta.getFieldName() + SECONDARY_PRROPERTY_SUFIX;
        }
    }

}
