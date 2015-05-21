package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

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
        if (sqlType == Types.VARCHAR) {
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
            stmt.setString(parameterIndex, doc.getText());
        }
        return 1;
    }

    static class TextSearchQueryValueBindAdapter implements ValueBindAdapter {

        @Override
        public List<String> getColumnNames(String memberSqlName) {
            return Arrays.asList(memberSqlName);
        }

        @Override
        public int bindValue(PersistenceContext persistenceContext, PreparedStatement stmt, int parameterIndex, Object value) throws SQLException {
            stmt.setString(parameterIndex, value.toString());
            return 1;
        }

    }

    @Override
    public ValueBindAdapter getQueryValueBindAdapter(Restriction restriction, Object value) {
        return new TextSearchQueryValueBindAdapter();
    }

}
