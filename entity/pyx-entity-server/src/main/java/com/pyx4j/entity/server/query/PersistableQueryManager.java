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
 * Created on Apr 21, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.query;

import org.apache.commons.collections4.BidiMap;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.entity.core.query.IQuery;
import com.pyx4j.entity.core.query.QueryBinder;
import com.pyx4j.entity.core.query.QueryStorage;
import com.pyx4j.entity.server.Persistence;

class PersistableQueryManager {

    static <E extends IEntity, Q extends IQuery<E>> EntityQueryCriteria<E> convertToCriteria(Q query, QueryBinder<E, Q> binder) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<E> criteria = EntityQueryCriteria.create((Class<E>) query.proto().getEntityMeta().getEntityClass());

        EntityMeta cm = query.getEntityMeta();
        for (String memberName : cm.getMemberNames()) {
            MemberMeta memberMeta = cm.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            IObject<?> queryMember = query.getMember(memberName);
            if (queryMember instanceof ICondition) {
                ICondition condition = (ICondition) queryMember;
                ConditionTranslation<ICondition> ct = ConditionTranslationRegistry.instance().getConditionTranslation(condition);
                ct.enhanceCriteria(criteria, binder.toEntityPath(condition.getPath()), condition);
            }
        }
        return criteria;
    }

    /**
     * @param query
     *            PersistableQuery to save
     * @param queryStorage
     *            persists and update this object so app can save pointer.
     */
    static <Q extends IQuery<? extends IEntity>> void persistQuery(Q query, QueryStorage queryStorage) {
        if (!queryStorage.id().isNull()) {
            Persistence.ensureRetrieve(queryStorage, AttachLevel.Attached);
        }
        @SuppressWarnings("unchecked")
        BidiMap<Key, Path> map = ColumnStorage.instance().getCriteriaColumns((Class<? extends IQuery<?>>) query.getInstanceValueClass());

        EntityMeta cm = query.getEntityMeta();
        for (String memberName : cm.getMemberNames()) {
            MemberMeta memberMeta = cm.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            IObject<?> member = query.getMember(memberName);
            if (member instanceof ICondition) {
                // find existing criterion in a list by columnId
                ICondition condition = (ICondition) member;
                condition.columnId().setValue(map.getKey(condition.getPath()));

                ConditionTranslation<ICondition> ct = ConditionTranslationRegistry.instance().getConditionTranslation(condition);
                ct.onBeforePersist(condition);

                queryStorage.conditions().add(condition);
            }
        }
        Persistence.service().persist(queryStorage);
    }

    static <Q extends IQuery<? extends IEntity>> Q retriveQuery(Class<Q> queryClass, QueryStorage queryStorageId) {
        QueryStorage queryCriteriaStorage = Persistence.service().retrieve(QueryStorage.class, queryStorageId.getPrimaryKey());

        BidiMap<Key, Path> map = ColumnStorage.instance().getCriteriaColumns(queryClass);

        Q query = EntityFactory.create(queryClass);
        for (ICondition criterion : queryCriteriaStorage.conditions()) {
            ICondition condition = (ICondition) query.getMember(map.get(criterion.columnId().getValue()));
            ConditionTranslation<ICondition> ct = ConditionTranslationRegistry.instance().getConditionTranslation(condition);
            condition.set(criterion);
            ct.onAfterRetrive(condition);
        }

        return query;
    }
}
