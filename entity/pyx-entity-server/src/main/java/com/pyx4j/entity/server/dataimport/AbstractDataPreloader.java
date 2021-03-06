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
 */
package com.pyx4j.entity.server.dataimport;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.server.PersistenceServicesFactory;

public abstract class AbstractDataPreloader implements DataPreloader {

    protected Map<String, Serializable> parameters;

    private final Map<String, IEntity> namesCache = new HashMap<String, IEntity>();

    List<Class<? extends IEntity>> deleteList = null;

    protected AbstractDataPreloader() {

    }

    @Override
    public void prepare() {
    }

    public static boolean isGAEDevelopment() {
        return (ServerSideConfiguration.instance().getEnvironmentType() == ServerSideConfiguration.EnvironmentType.GAEDevelopment);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<Class<? extends IEntity>> getEntityToDelete() {
        deleteList = new Vector();
        delete();
        try {
            return deleteList;
        } finally {
            deleteList = null;
        }
    }

    protected <T extends IEntity> String deleteAll(Class<T> entityClass) {
        if (deleteList != null) {
            deleteList.add(entityClass);
            return null;
        }
        EntityQueryCriteria<T> criteria = new EntityQueryCriteria<T>(entityClass);
        int count = PersistenceServicesFactory.getPersistenceService().delete(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (count > 0) {
            return "Removed " + count + " " + entityMeta.getCaption() + "(s)";
        } else {
            return null;
        }
    }

    protected String deleteAll(Class<? extends IEntity>... entityClass) {
        if (deleteList != null) {
            deleteList.addAll(Arrays.asList(entityClass));
            return null;
        }
        StringBuilder b = new StringBuilder();
        for (Class<? extends IEntity> ec : entityClass) {
            String msg = deleteAll(ec);
            if (CommonsStringUtils.isStringSet(msg)) {
                b.append(msg).append('\n');
            }
        }
        return b.toString();
    }

    public static <T extends IEntity> T createNamed(Class<T> clazz, String name) {
        T ent = EntityFactory.create(clazz);
        ent.setMemberValue("name", name);
        PersistenceServicesFactory.getPersistenceService().persist(ent);
        return ent;
    }

    public static <T extends IEntity> void createNamed(Class<T> clazz, String... names) {
        List<T> entToSave = new Vector<T>();
        for (String name : names) {
            T ent = EntityFactory.create(clazz);
            ent.setMemberValue("name", name);
            entToSave.add(ent);
        }
        PersistenceServicesFactory.getPersistenceService().persist(entToSave);
    }

    @SuppressWarnings("unchecked")
    protected <T extends IEntity> T retrieveNamed(Class<T> clazz, String name) {
        String key = clazz.getName() + "-" + name;
        if (namesCache.containsKey(key)) {
            return (T) namesCache.get(key);
        }
        EntityQueryCriteria<T> criteria = EntityQueryCriteria.create(clazz);
        criteria.add(PropertyCriterion.eq(criteria.proto().getMember("name"), name));
        T ent = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        namesCache.put(key, ent);
        return ent;
    }

    @SuppressWarnings("unchecked")
    protected <T extends IEntity> T retrieveByMember(Class<T> clazz, IObject<?> member, String value) {
        String key = clazz.getName() + "-" + member.getFieldName() + "=" + value;
        if (namesCache.containsKey(key)) {
            return (T) namesCache.get(key);
        }
        EntityQueryCriteria<T> criteria = EntityQueryCriteria.create(clazz);
        criteria.add(PropertyCriterion.eq(member, value));
        T ent = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        namesCache.put(key, ent);
        return ent;
    }

    @Override
    public Set<String> getParameters() {
        return new HashSet<String>();
    }

    public void setParameterValue(String key, Serializable value) {
        if (parameters == null) {
            parameters = new HashMap<String, Serializable>();
        }
        parameters.put(key, value);
    }

    @Override
    public void setParametersValues(Map<String, Serializable> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Serializable> getParametersValues() {
        return parameters;
    }

    protected Serializable getParameter(String name) {
        if (parameters == null) {
            return null;
        } else {
            return parameters.get(name);
        }
    }
}
