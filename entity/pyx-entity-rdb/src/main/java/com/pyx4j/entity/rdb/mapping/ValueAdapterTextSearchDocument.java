package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.TextSearchDocument;

public class ValueAdapterTextSearchDocument extends ValueAdapterPrimitive {

    private static final Logger log = LoggerFactory.getLogger(ValueAdapterTextSearchDocument.class);

    private final MemberMeta memberMeta;

    protected ValueAdapterTextSearchDocument(Dialect dialect, MemberMeta memberMeta) {
        super(dialect, TextSearchDocument.class);
        this.memberMeta = memberMeta;
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
