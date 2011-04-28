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

import com.pyx4j.commons.Printable;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.validator.Validator;

public interface IEntity extends IObject<Map<String, Object>>, Serializable, Printable {

    public static String PRIMARY_KEY = "id";

    public static String SERIALIZABLE_IMPL_CLASS_SUFIX = "_Impl";

    public static String CONCRETE_TYPE_DATA_ATTR = "$concrete";

    public Long getPrimaryKey();

    public void setPrimaryKey(Long pk);

    @Indexed
    public IPrimitive<Long> id();

    /**
     * @return true if only PrimaryKey present and other properties are empty or null
     */
    public boolean isEmpty();

    public IObject<?> getMember(String memberName);

    public IObject<?> getMember(Path path) throws IllegalArgumentException;

    public Object getMemberValue(String memberName);

    public void setMemberValue(String memberName, Object value);

    public Object getValue(Path path);

    public void setValue(Path path, Object value);

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

    public boolean containsMemberValue(String memberName);

    @Override
    public Class<? extends IEntity> getValueClass();

    public Class<? extends IEntity> getInstanceValueClass();

    /**
     * A single instance of MemberMeta is shared between all instances of the IEntity
     * inside EntityMeta.
     */
    public EntityMeta getEntityMeta();

    /**
     * @see BusinessEqualValue
     */
    public boolean businessEquals(IEntity other);

    public List<Validator> getValidators(Path memberPath);

    public IEntity cloneEntity();

    /**
     * Detach IEntity from its parent but keep the same shared value object
     */
    public <T extends IEntity> T detach();

    public <T extends IEntity> T cast();

    public boolean isObjectClassSameAsDef();

    /**
     * Determines if IEntity object is either the same class as, or is a superclass or
     * superinterface of, the class or interface represented by the specified Class
     * parameter.
     */
    public boolean isAssignableFrom(Class<? extends IEntity> targetType);

    /**
     * Determines if the specified class of IEntity is assignment-compatible with the
     * entity represented by this Entity Class.
     */
    public boolean isInstanceOf(Class<? extends IEntity> targetType);

    @Override
    public String getStringView();
}
