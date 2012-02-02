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

import com.pyx4j.entity.shared.meta.MemberMeta;

public class MemberExternalOperationsMeta extends MemberOperationsMeta {

    private final String sqlOwnerName;

    private final String sqlValueName;

    private final ValueAdapter ownerValueAdapter;

    public MemberExternalOperationsMeta(EntityMemberAccess memberAccess, ValueAdapter valueAdapter, String sqlName, MemberMeta memberMeta, String memberPath,
            String sqlOwnerName, ValueAdapter ownerValueAdapter, String sqlValueName) {
        super(memberAccess, valueAdapter, sqlName, memberMeta, memberPath);
        this.sqlOwnerName = sqlOwnerName;
        this.ownerValueAdapter = ownerValueAdapter;
        this.sqlValueName = sqlValueName;
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

}
