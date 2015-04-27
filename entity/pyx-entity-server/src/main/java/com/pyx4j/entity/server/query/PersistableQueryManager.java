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

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.core.query.ICriterion;
import com.pyx4j.entity.core.query.IQueryCriteria;
import com.pyx4j.entity.core.query.QueryCriteriaBinder;
import com.pyx4j.entity.core.query.QueryCriteriaStorage;
import com.pyx4j.entity.server.Persistence;

public class PersistableQueryManager {

    public static <E extends IEntity, C extends IQueryCriteria<E>> EntityQueryCriteria<E> convertQueryCriteria(C query, QueryCriteriaBinder<E, C> binder) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<E> criteria = EntityQueryCriteria.create((Class<E>) query.proto().getEntityMeta().getEntityClass());

        EntityMeta cm = query.getEntityMeta();
        for (String memberName : cm.getMemberNames()) {
            MemberMeta memberMeta = cm.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            IObject<?> criteriaMember = query.getMember(memberName);
            if (criteriaMember instanceof ICriterion) {
                DefaultCriterionTranslation.addCriteria(criteria, binder.toEntityPath(criteriaMember.getPath()), (ICriterion) criteriaMember);
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
    public static <E extends IEntity> void saveCriteria(IQueryCriteria<E> query, QueryCriteriaStorage queryCriteriaStorage) {
        if (!queryCriteriaStorage.id().isNull()) {
            Persistence.ensureRetrieve(queryCriteriaStorage, AttachLevel.Attached);
        }
        EntityMeta cm = query.getEntityMeta();
        for (String memberName : cm.getMemberNames()) {
            MemberMeta memberMeta = cm.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            IObject<?> criteriaMember = query.getMember(memberName);
            if (criteriaMember instanceof ICriterion) {
                // TODO find existing criterion in a list by columnId
                queryCriteriaStorage.criterions().add((ICriterion) criteriaMember);
            }
        }
    }

    public static <C extends IQueryCriteria<? extends IEntity>> C retriveCriteria(Class<C> criteriaClass, QueryCriteriaStorage queryCriteriaStorageId) {
        QueryCriteriaStorage queryCriteriaStorage = Persistence.service().retrieve(QueryCriteriaStorage.class, queryCriteriaStorageId.getPrimaryKey());

        C query = EntityFactory.create(criteriaClass);

        return query;
    }
}
