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

public interface IObject<VALUE_TYPE> {

    public boolean isNull();

    public void setValue(VALUE_TYPE value) throws ClassCastException;

    public VALUE_TYPE getValue();

    public Path getPath();

    public Class<?> getValueClass();

    public Class<? extends IObject> getObjectClass();

    //Owned by parent
    public IEntity getOwner();

    /**
     * This may be ICollection or IEntity
     */
    public IObject<?> getParent();

    //In parent's map
    public String getFieldName();

    public MemberMeta getMeta();

    public boolean metaEquals(IObject<?> other);

    /**
     * Business toString() presentation.
     * 
     * @return String value of member formated using annotation
     *         com.pyx4j.entity.annotations.Format or String value of members annotated
     *         with ToString for IEntity
     * 
     * @see com.pyx4j.entity.annotations.Format
     * @see com.pyx4j.entity.annotations.ToString
     */
    public String getStringView();

}
