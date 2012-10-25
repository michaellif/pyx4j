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
 * Created on 2012-10-25
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class QueryJoinBuilderSubQuery extends QueryJoinBuilder {

    private final QueryJoinBuilder mainBuilder;

    private JoinDef mainTableJoin;

    protected final Map<String, JoinDef> subQueryMemberJoinAliases = new LinkedHashMap<String, JoinDef>();

    QueryJoinBuilderSubQuery(QueryJoinBuilder mainBuilder) {
        super(mainBuilder.persistenceContext, mainBuilder.mappings, mainBuilder.operationsMeta, mainBuilder.mainTableSqlAlias, mainBuilder.versionedCriteria);
        this.mainBuilder = mainBuilder;
    }

    @Override
    protected String nextJoinAliaseId() {
        return mainBuilder.nextJoinAliaseId();
    }

    @Override
    protected JoinDef getMemberJoin(String path) {
        JoinDef memberJoin = subQueryMemberJoinAliases.get(path);
        if (memberJoin != null) {
            return memberJoin;
        } else {
            return mainBuilder.getMemberJoin(path);
        }
    }

    @Override
    protected Collection<JoinDef> getMemberJoinAliases() {
        return subQueryMemberJoinAliases.values();
    }

    @Override
    protected void putMemberJoin(String path, JoinDef memberJoin) {
        subQueryMemberJoinAliases.put(path, memberJoin);
    }

    JoinDef buildSubQueryJoin(String propertyPath, boolean leftJoin) {
        mainTableJoin = buildJoin(operationsMeta, mainTableSqlAlias, propertyPath, leftJoin, false, true);
        return mainTableJoin;
    }

    @Override
    protected boolean appenFromJoin(JoinDef memberJoin) {
        return mainTableJoin != memberJoin;
    }

}
