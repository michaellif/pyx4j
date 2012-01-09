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

    /**
     * @return true if Value object is Null
     */
    public boolean isNull();

    public void setValue(VALUE_TYPE value) throws ClassCastException;

    public VALUE_TYPE getValue();

    /**
     * TODO return AttachLevel
     * 
     * @return true is only PrimaryKey and type information is present, other data was not retrieved, TODO getStringView()
     */
    //public AttachLevel getAttachLevel();

    /**
     * TODO set AttachLevel
     * Internally used by persistence layer when loading all values for entity
     */
    //public void setAttachLevel(AttachLevel level);

    public Path getPath();

    /**
     * @return Actual primitive class String, Double ..., or for entity class that extends IEntity (e.g. the same as getObjectClass())
     */
    public Class<?> getValueClass();

    /**
     * @return IList, ISet, IPrimitive, or extends IEntity
     */
    public Class<? extends IObject<?>> getObjectClass();

    /**
     * This is IEntity in the Entity Graph that holds the value of this Entity. This is not business parent @Owner!
     */
    public IEntity getOwner();

    /**
     * This may be ICollection or IEntity in the Entity Graph. This is not business parent @Owner!
     */
    public IObject<?> getParent();

    /**
     * Name of this Object in parent's object map
     * 
     * @return null for root Entity in Graph
     */
    public String getFieldName();

    /**
     * Meta of this Entity in parent's object map
     * 
     * @return null for root Entity in Graph
     */
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
