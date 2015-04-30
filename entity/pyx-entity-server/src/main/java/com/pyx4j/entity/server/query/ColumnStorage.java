/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 30, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.core.query.AbstractQueryFilterColumnStorage;
import com.pyx4j.entity.core.query.IQueryFilter;
import com.pyx4j.entity.core.query.IQueryFilterList;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;

public class ColumnStorage {

    private static Logger log = LoggerFactory.getLogger(ColumnStorage.class);

    private static class SingletonHolder {
        public static final ColumnStorage INSTANCE = new ColumnStorage();
    }

    public static ColumnStorage instance() {
        return SingletonHolder.INSTANCE;
    }

    private Class<? extends AbstractQueryFilterColumnStorage> persistableEntityClass;

    private ColumnStorage() {

    }

    @SuppressWarnings("unchecked")
    public void initialize(Class<? extends AbstractQueryFilterColumnStorage> persistableEntityClass) {
        this.persistableEntityClass = persistableEntityClass;
        for (Class<? extends IEntity> ec : ServerEntityFactory.getAllAssignableFrom(IQueryFilterList.class)) {
            if ((ec.getAnnotation(AbstractEntity.class) == null) && (ec.getAnnotation(EmbeddedEntity.class) == null)) {
                createOrUpdateCriteriaColumnStorage((Class<? extends IQueryFilterList<?>>) ec);
            }
        }
    }

    private void createOrUpdateCriteriaColumnStorage(Class<? extends IQueryFilterList<?>> criteriaClass) {
        BidiMap<Key, Path> map = getCriteriaColumns(criteriaClass);
        IQueryFilterList<?> proto = EntityFactory.getEntityPrototype(criteriaClass);
        EntityMeta cm = proto.getEntityMeta();
        for (String memberName : cm.getMemberNames()) {
            MemberMeta memberMeta = cm.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            IObject<?> criteriaMember = proto.getMember(memberName);
            if (criteriaMember instanceof IQueryFilter) {
                if (!map.containsValue(criteriaMember.getPath())) {
                    log.debug("adding new query column {}", criteriaMember.getPath());

                    AbstractQueryFilterColumnStorage storage = EntityFactory.create(persistableEntityClass);
                    storage.queryClass().setValue(criteriaClass.getName());
                    storage.columnPath().setValue(memberName);
                    Persistence.service().persist(storage);
                }
            }
        }
    }

    //TODO add memory cash
    public <C extends IQueryFilterList<?>> BidiMap<Key, Path> getCriteriaColumns(Class<C> criteriaClass) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<AbstractQueryFilterColumnStorage> criteria = (EntityQueryCriteria<AbstractQueryFilterColumnStorage>) EntityQueryCriteria
                .create(persistableEntityClass);
        criteria.eq(criteria.proto().queryClass(), criteriaClass.getName());

        BidiMap<Key, Path> map = new DualHashBidiMap<>();
        for (AbstractQueryFilterColumnStorage column : Persistence.service().query(criteria)) {
            map.put(column.getPrimaryKey(), new Path(criteriaClass, column.columnPath().getValue()));
        }

        return map;

    }
}
