/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

public abstract class XMLEntityFactoryStrict implements XMLEntityFactory {

    private final Map<String, Class<?>> binding = new HashMap<String, Class<?>>();

    private final XMLEntityName entityName;

    public XMLEntityFactoryStrict(XMLEntityName entityName) {
        this.entityName = entityName;
        bind();
    }

    protected abstract void bind();

    public void bind(Class<? extends IEntity> entityClass) {
        binding.put(entityName.getXMLName(entityClass), entityClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> T createInstance(String xmlName, Class<T> objectClass) {
        Class<T> entityClass;
        if (xmlName != null) {
            entityClass = (Class<T>) binding.get(xmlName);
            if (entityClass == null) {
                throw new Error("Unbound XML entity name '" + xmlName + "'");
            }
        } else {
            entityClass = objectClass;
        }
        return EntityFactory.create(entityClass);
    }

}
