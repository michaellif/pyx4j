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

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.meta.MemberMeta;

public class MemberExternalOperationsMeta extends MemberOperationsMeta {

    private final Class<? extends IEntity> joinTableClass;

    private final boolean joinTableSameAsTarget;

    private final String sqlOwnerName;

    private final String sqlValueName;

    private final String sqlChildJoinContition;

    private final ValueAdapter ownerValueAdapter;

    public MemberExternalOperationsMeta(EntityMemberAccess memberAccess, ValueAdapter valueAdapter, String sqlName, MemberMeta memberMeta, String memberPath,
            Class<? extends IEntity> joinTableClass, boolean joinTableSameAsTarget, String sqlOwnerName, ValueAdapter ownerValueAdapter, String sqlValueName,
            String sqlChildJoinContition) {
        super(memberAccess, valueAdapter, sqlName, memberMeta, memberPath);
        this.joinTableClass = joinTableClass;
        this.joinTableSameAsTarget = joinTableSameAsTarget;
        this.sqlOwnerName = sqlOwnerName;
        this.ownerValueAdapter = ownerValueAdapter;
        this.sqlValueName = sqlValueName;
        this.sqlChildJoinContition = sqlChildJoinContition;
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    public Class<? extends IEntity> joinTableClass() {
        return joinTableClass;
    }

    public boolean isJoinTableSameAsTarget() {
        return joinTableSameAsTarget;
    }

    public String sqlOwnerName() {
        return sqlOwnerName;
    }

    public String sqlValueName() {
        return sqlValueName;
    }

    public ValueAdapter getOwnerValueAdapter() {
        return ownerValueAdapter;
    }

    public boolean hasChildJoinContition() {
        return sqlChildJoinContition != null;
    }

    public String getSqlChildJoinContition() {
        return sqlChildJoinContition;
    }

    @Override
    public String toString() {
        return super.toString() + " External";
    }
}
