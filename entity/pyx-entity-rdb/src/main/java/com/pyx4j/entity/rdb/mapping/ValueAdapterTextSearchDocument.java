package com.pyx4j.entity.rdb.mapping;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public Serializable retrieveValue(ResultSet rs, String memberSqlName)
			throws SQLException {
		 String value = rs.getString(memberSqlName);
	        if (rs.wasNull() || (value.length() == 0)) {
	            return null;
	        } else {
	            return new TextSearchDocument(value);
	        }
	}

	@Override
	public int bindValue(PersistenceContext persistenceContext,
			PreparedStatement stmt, int parameterIndex, Object value)
			throws SQLException {
		TextSearchDocument doc = (TextSearchDocument) value;
        if (doc == null || doc.getText() == null) {
            stmt.setNull(parameterIndex, sqlType);
        } else {
            stmt.setString(parameterIndex, doc.getText());
        }
		return 1;
	}

}
