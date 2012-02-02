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
 * Created on Feb 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.server.contexts.NamespaceManager;

public class TableModleExternal {

    private static final Logger log = LoggerFactory.getLogger(TableModleExternal.class);

    public static void retrieve(Connection connection, Dialect dialect, IEntity entity, MemberExternalOperationsMeta member) {
        if (member.getMemberMeta().getAttachLevel() == AttachLevel.Detached) {
            return;
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT ");
            boolean firstColumn = true;
            for (String name : member.getValueAdapter().getColumnNames(member.sqlValueName())) {
                if (firstColumn) {
                    firstColumn = false;
                } else {
                    sql.append(", ");
                }
                sql.append(name);
            }
            sql.append(" FROM ").append(member.sqlName()).append(" WHERE ");

            boolean firstWhereColumn = true;
            for (String name : member.getOwnerValueAdapter().getColumnNames(member.sqlOwnerName())) {
                if (firstWhereColumn) {
                    firstWhereColumn = false;
                } else {
                    sql.append(" AND ");
                }
                sql.append(name).append(" = ?");
            }

            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            stmt = connection.prepareStatement(sql.toString());
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);

            int parameterIndex = 1;
            parameterIndex += member.getOwnerValueAdapter().bindValue(stmt, parameterIndex, entity);

            if (dialect.isMultitenant()) {
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                parameterIndex++;
            }
            rs = stmt.executeQuery();

            IEntity childEntity = (IEntity) member.getMember(entity);
            if (!childEntity.isNull()) {
                log.warn("retrieving to not empty external member {}\n called from {}", member.getMemberPath(),
                        Trace.getCallOrigin(EntityPersistenceServiceRDB.class));
            }
            if (rs.next()) {
                Object value = member.getValueAdapter().retrieveValue(rs, member.sqlValueName());
                childEntity.set((IEntity) value);
            }
        } catch (SQLException e) {
            log.error("{} SQL {}", member.sqlName(), sql);
            log.error("{} SQL select error", member.sqlName(), e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

}
