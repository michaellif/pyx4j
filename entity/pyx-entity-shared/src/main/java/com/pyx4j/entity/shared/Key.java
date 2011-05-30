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
 * Created on 2011-05-30
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

/**
 * Serializable DB Reference (Primary Key) representation
 */
public class Key implements java.io.Serializable {

    private static final long serialVersionUID = 7972137198592582112L;

    private String value;

    private transient long longValue;

    protected Key() {

    }

    public Key(String serialPresntation) {
        assert (serialPresntation != null);
        value = serialPresntation;
    }

    public Key(long dbPrimaryKey) {
        assert (dbPrimaryKey != 0);
        this.longValue = dbPrimaryKey;
    }

    @Override
    public String toString() {
        if (value == null) {
            value = String.valueOf(longValue);
        }
        return value;
    }

    public long toPk() {
        if (longValue == 0) {
            longValue = Long.valueOf(value);
        }
        return longValue;
    }
}
