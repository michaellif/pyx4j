package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.TextSearchDocument;

public class ValueAdapterTextSearchDocument extends ValueAdapterPrimitive {

    protected ValueAdapterTextSearchDocument(Dialect dialect) {
        super(dialect, TextSearchDocument.class);
    }

    @Override
    public String sqlColumnTypeDefinition(Dialect dialect, MemberOperationsMeta member, String columnName) {
        if ("varchar".equals(dialect.getSqlType(TextSearchDocument.class))) {
            int maxLength = member.getMemberMeta().getLength();
            if (maxLength == 0) {
                maxLength = TableModel.ORDINARY_STRING_LENGHT_MAX;
            }
            return super.sqlColumnTypeDefinition(dialect, member, columnName) + '(' + maxLength + ')';
        } else {
            return super.sqlColumnTypeDefinition(dialect, member, columnName);
        }
    }

    @Override
    public Serializable retrieveValue(ResultSet rs, String memberSqlName) throws SQLException {
        String value = rs.getString(memberSqlName);
        if (rs.wasNull() || (value.length() == 0)) {
            return null;
        } else {
            return new TextSearchDocument(value);
        }
    }

    @Override
    public String toSqlValue(Dialect dialect, String columnName, String argumentPlaceHolder) {
        return dialect.textSearchToSqlValue(argumentPlaceHolder);
    }

    @Override
    public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
        TextSearchDocument doc = (TextSearchDocument) value;
        if (doc == null || doc.getText() == null) {
            stmt.setNull(parameterIndex, sqlType);
        } else {
            stmt.setString(parameterIndex, persistenceContext.getDialect().textSearchToBindValue(doc.getText()));
        }
        return 1;
    }

    static class TextSearchQueryValueBindAdapter extends ValueBindAdapterAbstract {

        @Override
        public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
            stmt.setString(parameterIndex, value.toString());
            return 1;
        }

        @Override
        public String querySqlFunctionOnValue(Dialect dialect, Restriction restriction, String argumentPlaceHolder) {
            return dialect.textSearchToSqlQueryValue(argumentPlaceHolder);
        }

    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        return new TextSearchQueryValueBindAdapter();
    }

}
