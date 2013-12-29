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
 * Created on Mar 19, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.dialect.Dialect;

public class JoinVersionDataInformation extends JoinInformation {

    JoinVersionDataInformation(Dialect dialect, EntityMeta rootEntityMeta, EntityMeta entityMeta, MemberMeta memberMeta) {

        @SuppressWarnings("unchecked")
        Class<? extends IEntity> versionDataEntityClass = (Class<IEntity>) memberMeta.getValueClass();
        EntityMeta versionDataEntityMeta = EntityFactory.getEntityMeta(versionDataEntityClass);

        joinTableClass = versionDataEntityClass;
        sqlName = TableModel.getTableName(dialect, versionDataEntityMeta);
        sqlValueName = dialect.getNamingConvention().sqlIdColumnName();
        joinTableSameAsTarget = true;

        MemberMeta ownerMemberMeta = findOwnerMember(versionDataEntityMeta, memberMeta, rootEntityMeta, entityMeta);
        sqlOwnerName = dialect.getNamingConvention().sqlFieldName(EntityOperationsMeta.memberPersistenceName(ownerMemberMeta));
    }
}
