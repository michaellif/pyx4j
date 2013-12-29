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
 * Created on Jan 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.dialect.Dialect;

public class MemberVersionDataOperationsMeta extends MemberExternalOperationsMeta {

    private final String sqlFromDateColumnName;

    private final String sqlToDateColumnName;

    public MemberVersionDataOperationsMeta(Dialect dialect, EntityMemberAccess memberAccess, ValueAdapter valueAdapter, String sqlName, MemberMeta memberMeta,
            String memberPath, Class<? extends IEntity> joinTableClass, boolean joinTableSameAsTarget, String sqlOwnerName, ValueAdapter ownerValueAdapter,
            String sqlValueName, String sqlChildJoinContition) {
        super(memberAccess, valueAdapter, sqlName, memberMeta, memberPath, joinTableClass, joinTableSameAsTarget, sqlOwnerName, ownerValueAdapter,
                sqlValueName, sqlChildJoinContition);

        @SuppressWarnings("unchecked")
        Class<? extends IVersionData<?>> versionDataEntityClass = (Class<IVersionData<?>>) memberMeta.getValueClass();
        IVersionData<?> proto = EntityFactory.getEntityPrototype(versionDataEntityClass);
        sqlFromDateColumnName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(proto.fromDate().getMeta()));
        sqlToDateColumnName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(proto.toDate().getMeta()));
    }

    @Override
    public boolean isVersionData() {
        return true;
    }

    public String getSqlFromDateColumnName() {
        return sqlFromDateColumnName;
    }

    public String getSqlToDateColumnName() {
        return sqlToDateColumnName;
    }

    @Override
    public String toString() {
        return super.toString() + " VersionData";
    }
}
