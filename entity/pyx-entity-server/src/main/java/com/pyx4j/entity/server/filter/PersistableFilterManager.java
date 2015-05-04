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
package com.pyx4j.entity.server.filter;

import org.apache.commons.collections4.BidiMap;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.filter.IQueryFilter;
import com.pyx4j.entity.core.filter.IQueryFilterList;
import com.pyx4j.entity.core.filter.QueryFilterBinder;
import com.pyx4j.entity.core.filter.QueryFilterStorage;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.server.Persistence;

public class PersistableFilterManager {

    public static <E extends IEntity, C extends IQueryFilterList<E>> EntityQueryCriteria<E> convertQueryCriteria(C query, QueryFilterBinder<E, C> binder) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<E> criteria = EntityQueryCriteria.create((Class<E>) query.proto().getEntityMeta().getEntityClass());

        EntityMeta cm = query.getEntityMeta();
        for (String memberName : cm.getMemberNames()) {
            MemberMeta memberMeta = cm.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            IObject<?> criteriaMember = query.getMember(memberName);
            if (criteriaMember instanceof IQueryFilter) {
                DefaultCriterionTranslation.addCriteria(criteria, binder.toEntityPath(criteriaMember.getPath()), (IQueryFilter) criteriaMember);
            }
        }
        return criteria;
    }

    /**
     * @param query
     *            PersistableQuery to save
     * @param queryCriteriaStorage
     *            persists and update this object so app can save pointer.
     */
    public static <C extends IQueryFilterList<? extends IEntity>> void persistCriteria(C query, QueryFilterStorage queryCriteriaStorage) {
        if (!queryCriteriaStorage.id().isNull()) {
            Persistence.ensureRetrieve(queryCriteriaStorage, AttachLevel.Attached);
        }
        @SuppressWarnings("unchecked")
        BidiMap<Key, Path> map = ColumnStorage.instance().getCriteriaColumns((Class<? extends IQueryFilterList<?>>) query.getInstanceValueClass());

        EntityMeta cm = query.getEntityMeta();
        for (String memberName : cm.getMemberNames()) {
            MemberMeta memberMeta = cm.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            IObject<?> criteriaMember = query.getMember(memberName);
            if (criteriaMember instanceof IQueryFilter) {
                // find existing criterion in a list by columnId
                IQueryFilter criterion = (IQueryFilter) criteriaMember;
                criterion.columnId().setValue(map.getKey(criterion.getPath()));

                queryCriteriaStorage.filters().add(criterion);
            }
        }
        Persistence.service().persist(queryCriteriaStorage);
    }

    public static <C extends IQueryFilterList<? extends IEntity>> C retriveCriteria(Class<C> criteriaClass, QueryFilterStorage queryCriteriaStorageId) {
        QueryFilterStorage queryCriteriaStorage = Persistence.service().retrieve(QueryFilterStorage.class, queryCriteriaStorageId.getPrimaryKey());

        BidiMap<Key, Path> map = ColumnStorage.instance().getCriteriaColumns(criteriaClass);

        C query = EntityFactory.create(criteriaClass);
        for (IQueryFilter criterion : queryCriteriaStorage.filters()) {
            IQueryFilter member = (IQueryFilter) query.getMember(map.get(criterion.columnId().getValue()));
            member.set(criterion);
        }

        return query;
    }
}
