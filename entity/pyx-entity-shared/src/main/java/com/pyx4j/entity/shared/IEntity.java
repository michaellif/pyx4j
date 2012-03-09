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

import com.pyx4j.commons.Key;
import com.pyx4j.commons.Printable;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.validator.Validator;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;

@I18n(strategy = I18nStrategy.DerivedOnly)
public interface IEntity extends IObject<Map<String, Object>>, Serializable, Printable {

    public static String PRIMARY_KEY = "id";

    public static String SERIALIZABLE_IMPL_CLASS_SUFIX = "_Impl";

    public static String ATTR_PREFIX = "$";

    public static String CONCRETE_TYPE_DATA_ATTR = ATTR_PREFIX + "concrete";

    public static String DETACHED_ATTR = ATTR_PREFIX + "detached";

    public static String TO_STRING_ATTR = ATTR_PREFIX + "string";

    public Key getPrimaryKey();

    public void setPrimaryKey(Key pk);

    @Indexed
    public IPrimitive<Key> id();

    /**
     * @return true if only PrimaryKey present and other properties are empty or null
     */
    public boolean isEmpty();

    /**
     * TODO return AttachLevel
     * 
     * @return true is only PrimaryKey and type information is present, other data was not retrieved, TODO getStringView()
     */
    public boolean isValueDetached();

    /**
     * TODO set AttachLevel
     * Internally used by persistence layer when loading all values for entity
     */
    public void setValuePopulated();

    /**
     * Internally used by persistence layer when creating new Entity reference
     */
    public void setValueDetached();

    public IObject<?> getMember(String memberName);

    public IObject<?> getMember(Path path) throws IllegalArgumentException;

    public Object getMemberValue(String memberName);

    public void setMemberValue(String memberName, Object value);

    /**
     * Remove all values from this entity, preserve ownership relationships
     */
    public void clearValues();

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

    /**
     * For IEntity ObjectClass and ValueClass are the same.
     * Represents the java interface class that is used to create this entity.
     * In case of inheritance IEntity may be of different type, see getInstanceValueClass()
     */
    @Override
    public Class<? extends IEntity> getValueClass();

    /**
     * For IEntity ObjectClass and ValueClass are the same.
     * Represents the java interface class that is used to create this entity.
     * In case of inheritance IEntity may be of different type, see getInstanceValueClass()
     */
    @Override
    public Class<? extends IEntity> getObjectClass();

    /**
     * Returns ValueClass or another IEntity class that is inherited from ValueClass if abstract Entity had been set with another value.
     */
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

    /**
     * Clone the entity to new entity of the same type
     * 
     * @return clone of the entity
     */
    public <T extends IEntity> T duplicate();

    /**
     * Copy/clone all existing members to new Entity of specific type that has a common ancestor.
     * This function is run-time type safe!
     * Should be used for Downcasting and upcasting IEntity.
     */
    public <T extends IEntity> T duplicate(Class<T> entityClass);

    /**
     * Detach IEntity from its parent but keep the same shared value object.
     * TODO rename. Subject related convert to root entity.
     */
    public <T extends IEntity> T detach();

    /**
     * Create shell entity that has only class information and primary key.
     * isValueDetached() will return true for such entity.
     */
    public <T extends IEntity> T createIdentityStub();

    /**
     * Cast the entity to actual type if entity is Abstract Entity. Return the same entity if Concrete type is the same as current type.
     * Use case when you need access to Concrete entity metadata(filed names).
     * 
     * @return Entity that reference the same data as original entity.
     */
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

    public String getDebugExceptionInfoString();
}
