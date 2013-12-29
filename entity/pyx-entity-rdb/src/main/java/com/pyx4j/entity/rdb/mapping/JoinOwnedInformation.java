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
 * Created on Feb 9, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.dialect.Dialect;

public class JoinOwnedInformation extends JoinInformation {

    // Relationship is managed in CHILD table using PARENT column.
    public JoinOwnedInformation(Dialect dialect, EntityMeta entityMeta, MemberMeta memberMeta, MemberMeta ownerMemberMeta) {
        @SuppressWarnings("unchecked")
        Class<? extends IEntity> childEntityClass = (Class<IEntity>) memberMeta.getValueClass();
        EntityMeta childEntityMeta = EntityFactory.getEntityMeta(childEntityClass);

        joinTableClass = childEntityClass;
        sqlName = TableModel.getTableName(dialect, childEntityMeta);
        sqlValueName = dialect.getNamingConvention().sqlIdColumnName();
        joinTableSameAsTarget = true;

        sqlOwnerName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(ownerMemberMeta));
        ownerValueAdapter = EntityOperationsMeta.createEntityValueAdapter(dialect, entityMeta, ownerMemberMeta);

        if (memberMeta.getObjectClassType() == ObjectClassType.EntityList) {
            MemberMeta orderMemberMeta = MemberCollectionOrderMeta.findOrderMember(entityMeta, memberMeta, false, childEntityMeta);
            if (orderMemberMeta == null) {
                throw new AssertionError("Unmapped @OrderBy member in table " + childEntityClass.getName() + " for '" + memberMeta.getFieldName() + "' in "
                        + entityMeta.getEntityClass().getName() + "\n add @OrderColumn or @MemberColumn to " + childEntityClass);
            }
            collectionOrderMeta = new MemberCollectionOrderMeta(dialect, orderMemberMeta);
        }

        if (EntityFactory.getEntityMeta(childEntityClass).getPersistableSuperClass() != null) {
            List<String> discriminatorStrings = new ArrayList<String>();
            for (Class<? extends IEntity> subclass : Mappings.getPersistableAssignableFrom(childEntityClass)) {
                DiscriminatorValue discriminator = subclass.getAnnotation(DiscriminatorValue.class);
                if (discriminator != null) {
                    discriminatorStrings.add(discriminator.value());
                }
            }
            if (discriminatorStrings.size() == 1) {
                sqlChildJoinContition = dialect.sqlDiscriminatorColumnName() + " = '" + discriminatorStrings.get(0) + "'";
            } else {
                sqlChildJoinContition = dialect.sqlDiscriminatorColumnName() + " IN (";
                boolean first = true;
                for (String desc : discriminatorStrings) {
                    if (first) {
                        first = false;
                    } else {
                        sqlChildJoinContition += ",";
                    }
                    sqlChildJoinContition += "'" + desc + "'";
                }
                sqlChildJoinContition += ") ";
            }
        }

    }

}
