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
 * Created on Feb 12, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.dataimport;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;

public abstract class AbstractDataPreloader implements DataPreloader {

    protected Map<String, Serializable> parameters;

    protected AbstractDataPreloader() {

    }

    public static boolean isGAEDevelopment() {
        SecurityManager sm = System.getSecurityManager();
        return (sm != null) && (sm.getClass().getName().startsWith("com.google.appengine.tools.development"));
    }

    protected static <T extends IEntity> String deleteAll(Class<T> entityClass) {
        EntityQueryCriteria<T> criteria = new EntityQueryCriteria<T>(entityClass);
        int count = PersistenceServicesFactory.getPersistenceService().delete(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        return "Removed " + count + " " + entityMeta.getCaption() + "(s)";
    }

    protected static String deleteAll(Class<? extends IEntity>... entityClass) {
        StringBuilder b = new StringBuilder();
        for (Class<? extends IEntity> ec : entityClass) {
            b.append(deleteAll(ec)).append('\n');
        }
        return b.toString();
    }

    public static <T extends IEntity> T createNamed(Class<T> clazz, String name) {
        T ent = EntityFactory.create(clazz);
        ent.setMemberValue("name", name);
        PersistenceServicesFactory.getPersistenceService().persist(ent);
        return ent;
    }

    public static <T extends IEntity> T retrieveNamed(Class<T> clazz, String name) {
        EntityQueryCriteria<T> criteria = EntityQueryCriteria.create(clazz);
        criteria.add(PropertyCriterion.eq("name", name));
        return PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
    }

    @Override
    public Set<String> getParameters() {
        return new HashSet<String>();
    }

    @Override
    public void setParametersValues(Map<String, Serializable> parameters) {
        this.parameters = parameters;
    }
}
