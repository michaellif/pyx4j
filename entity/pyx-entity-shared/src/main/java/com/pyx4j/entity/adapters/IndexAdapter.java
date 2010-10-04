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
 * Created on 2010-10-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.adapters;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.MemberMeta;

public interface IndexAdapter<E> {

    public static final String SECONDARY_PRROPERTY_SUFIX = "-s";

    public static final String EMBEDDED_PRROPERTY_SUFIX = "-e";

    public static final String ENTITY_KEYWORD_PRROPERTY = "keys" + SECONDARY_PRROPERTY_SUFIX;

    public Object getIndexedValue(IEntity entity, MemberMeta memberMeta, E value);

    public String getIndexedColumnName(String embeddedPath, MemberMeta memberMeta);

}
