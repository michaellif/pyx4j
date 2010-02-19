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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.validator.Validator;

public interface IEntity extends IObject<Map<String, Object>>, Serializable {

    public static String PRIMARY_KEY = "id";

    public static String SERIALIZABLE_IMPL_CLASS_SUFIX = "_Impl";

    public Long getPrimaryKey();

    public void setPrimaryKey(Long pk);

    public IObject<?> getMember(String memberName);

    public IObject<?> getMember(Path path);

    public Object getMemberValue(String memberName);

    public Object getMemberValue(Path path);

    public void setMemberValue(String memberName, Object value);

    /**
     * Unsafe set entity value object.
     * 
     * @param entity
     */
    public void set(IEntity entity);

    /**
     * Safer way to assign members value
     * 
     * @param <T>
     * @param member
     *            meta
     * @param value
     */
    public <T extends IObject<?>> void set(T member, T value);

    public Object removeMemberValue(String memberName);

    /**
     * A single instance of MemeberMeta is shared between all instances of the IEntity
     * inside EntityMeta.
     */
    public EntityMeta getEntityMeta();

    public List<Validator> getValidators(Path memberPath);

    /**
     * Business toString() presentation. @see com.pyx4j.entity.annotations.ToString
     * 
     * @return String value of members annotated with ToString
     */
    public String getStringView();

    public IEntity cloneEntity();
}
