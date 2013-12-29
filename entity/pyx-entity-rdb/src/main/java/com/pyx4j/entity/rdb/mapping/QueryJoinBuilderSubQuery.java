/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache* License, Version 2.0 (the "License"); you may not
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

import com.pyx4j.entity.core.Path;

class QueryJoinBuilderSubQuery extends QueryJoinBuilder {

    private final QueryJoinBuilder mainBuilder;

    private final String mainPropertyPath;

    private final JoinDef mainTableJoin;

    protected final Map<String, JoinDef> subQueryMemberJoinAliases = new LinkedHashMap<String, JoinDef>();

    QueryJoinBuilderSubQuery(QueryJoinBuilder mainBuilder, String propertyPath, boolean leftJoin) {
        super(mainBuilder.persistenceContext, mainBuilder.mappings, mainBuilder.operationsMeta, mainBuilder.mainTableSqlAlias, mainBuilder.versionedCriteria);
        this.mainBuilder = mainBuilder;
        this.mainPropertyPath = propertyPath;
        this.mainTableJoin = buildJoin(null, operationsMeta, mainTableSqlAlias, propertyPath, leftJoin, false, true);
    }

    JoinDef getMainTable() {
        return mainTableJoin;
    }

    @Override
    protected String nextJoinAliaseId() {
        return mainBuilder.nextJoinAliaseId();
    }

    @Override
    protected Collection<JoinDef> getMemberJoinAliases() {
        return subQueryMemberJoinAliases.values();
    }

    @Override
    protected JoinDef getMemberJoin(String accessPath, String path) {
        JoinDef memberJoin = subQueryMemberJoinAliases.get(path);
        if (memberJoin != null) {
            return memberJoin;
        } else {
            return mainBuilder.getMemberJoin(accessPath, path);
        }
    }

    @Override
    protected void putMemberJoin(String accessPath, String path, JoinDef memberJoin) {
        accessPath += path.substring(path.indexOf(Path.PATH_SEPARATOR) + 1);
        if (accessPath.startsWith(mainPropertyPath)) {
            subQueryMemberJoinAliases.put(path, memberJoin);
        } else {
            mainBuilder.putMemberJoin(accessPath, path, memberJoin);
        }
    }

    @Override
    protected boolean appenFromJoin(JoinDef memberJoin) {
        return mainTableJoin != memberJoin;
    }

}
