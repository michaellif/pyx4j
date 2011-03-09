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
 * Created on 2011-03-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityClassFinder;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

public class XMLEntityFactoryDefault implements XMLEntityFactory {

    private static final Logger log = LoggerFactory.getLogger(XMLEntityFactoryDefault.class);

    private final Map<String, String> names = new HashMap<String, String>();

    public XMLEntityFactoryDefault() {
        List<String> allClasses = EntityClassFinder.findEntityClasses();
        log.debug("has {} entity classes", allClasses.size());
        for (String className : allClasses) {
            // strip the package name
            String simpleName = className.substring(className.lastIndexOf(".") + 1);
            names.put(XMLEntityNameDefault.deCapitalize(simpleName), className);
        }
    }

    @Override
    public <T extends IEntity> T createInstance(String xmlName, Class<T> objectClass) {
        Class<T> entityClass;
        if (xmlName != null) {
            String entityClassName = names.get(xmlName);
            if (entityClassName == null) {
                entityClassName = xmlName;
            }
            entityClass = ServerEntityFactory.entityClass(entityClassName);
        } else {
            entityClass = objectClass;
        }
        return EntityFactory.create(entityClass);
    }

}
