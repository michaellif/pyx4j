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
 * Created on 2011-02-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.pyx4j.entity.rdb.SQLUtils;

public abstract class ResultSetIterator<T> implements Iterator<T>, Closeable {

    protected ResultSet rs = null;

    protected PreparedStatement stmt = null;

    private boolean next = false;

    public ResultSetIterator(PreparedStatement stmt, ResultSet rs) {
        this.stmt = stmt;
        this.rs = rs;
    }

    @Override
    public boolean hasNext() {
        if (rs == null) {
            return false;
        }
        if (!next) {
            try {
                next = rs.next();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return next;
    }

    protected abstract T retrieve();

    @Override
    public T next() {
        if (hasNext()) {
            next = false;
            return retrieve();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        try {
            rs.deleteRow();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        SQLUtils.closeQuietly(rs);
        SQLUtils.closeQuietly(stmt);
        rs = null;
        stmt = null;
    }

}
