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
import java.util.Set;

import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.validator.Validator;

public interface IEntity<E extends IEntity<?>> extends IObject<E, Map<String, Object>>, Serializable {

    public static String PRIMARY_KEY = "id";

    public static String SERIALIZABLE_IMPL_CLASS_SUFIX = "_Impl";

    public String getPrimaryKey();

    public void setPrimaryKey(String pk);

    public Set<String> getMemberNames();

    public IObject<?, ?> getMember(String memberName);

    public IObject<?, ?> getMember(Path path);

    public Object getMemberValue(String memberName);

    public Object getMemberValue(Path path);

    public void setMemberValue(String memberName, Object value);

    /**
     * A single instance of MemeberMeta is shared between all instances of the IEntity
     * inside EntityMeta.
     */
    public MemberMeta getMemberMeta(String memberName);

    public List<Validator> getValidators(Path memberPath);

    public E cloneEntity();
}
