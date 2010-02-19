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
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import com.pyx4j.entity.shared.meta.MemberMeta;

@SuppressWarnings("unchecked")
public interface IObject<OBJECT_TYPE extends IObject, VALUE_TYPE> {

    public boolean isNull();

    //public void set(OBJECT_TYPE entity);

    public void setValue(VALUE_TYPE value);

    public VALUE_TYPE getValue();

    public Path getPath();

    public Class<? extends IObject> getObjectClass();

    //Owned by parent
    public IEntity<?> getParent();

    //In parent's map
    public String getFieldName();

    public MemberMeta getMeta();

}
