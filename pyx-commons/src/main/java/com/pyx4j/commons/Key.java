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
 */
package com.pyx4j.commons;

import java.util.Date;

/**
 * Serializable DB Reference (Primary Key) representation
 */
public final class Key implements java.io.Serializable, Comparable<Key> {

    private static final long serialVersionUID = 7972137198592582112L;

    private String value;

    private transient long longValue;

    public static final long VERSION_DRAFT = 0;

    public static final long VERSION_CURRENT = -1;

    private transient long versionValue = VERSION_CURRENT;

    protected Key() {

    }

    public Key(String serialPresentation) {
        assert (serialPresentation != null);
        value = serialPresentation;
    }

    public Key(long dbPrimaryKey) {
        assert (dbPrimaryKey != 0);
        this.longValue = dbPrimaryKey;
        value = String.valueOf(longValue);
    }

    public Key(long dbPrimaryKey, long dbVersion) {
        assert (dbPrimaryKey != 0);
        this.longValue = dbPrimaryKey;
        this.versionValue = dbVersion;
        value = String.valueOf(longValue);
        if (dbVersion != VERSION_CURRENT) {
            value += "." + String.valueOf(dbVersion);
        }
    }

    public Key asDraftKey() {
        return new Key(this.asLong(), VERSION_DRAFT);
    }

    public Key asCurrentKey() {
        return new Key(this.asLong(), VERSION_CURRENT);
    }

    public Key asVersionKey(Date forDate) {
        if (forDate == null) {
            return new Key(this.asLong(), VERSION_CURRENT);
        } else {
            return new Key(this.asLong(), forDate.getTime());
        }
    }

    public boolean isDraft() {
        return (getVersion() == VERSION_DRAFT);
    }

    /**
     * This may be return inconsistent results if you retrieve the entity by version.
     * The returned key of Entity does not know if entity retrieved is current.
     * Cause the retrieval procedure do not change the Key!
     * 
     * use VersionedEntityUtils.isCurrent(entity)
     */
    public boolean isCurrent() {
        return (getVersion() == VERSION_CURRENT);
    }

    public boolean isVersion() {
        return !(isDraft() || isCurrent());
    }

    @Override
    public String toString() {
        return value;
    }

    /*
     * Should not be used in GWT app to get the value, string may be obfucated.
     * 
     * @exception NumberFormatException if the string representation does not contain a parsable <code>long</code>.
     */
    public long asLong() throws NumberFormatException {
        if (longValue == 0) {
            pars();
        }
        return longValue;
    }

    public long getVersion() throws NumberFormatException {
        if (longValue == 0) {
            pars();
        }
        return versionValue;
    }

    private void pars() throws NumberFormatException {
        int vp = value.indexOf(".");
        if (vp == -1) {
            versionValue = VERSION_CURRENT;
            longValue = Long.valueOf(value);
        } else {
            versionValue = Long.valueOf(value.substring(vp + 1));
            longValue = Long.valueOf(value.substring(0, vp));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Key)) {
            return false;
        }
        return this.toString().equals(other.toString());
    }

    public boolean equalsIgnoreVersion(Key other) {
        if (other == this) {
            return true;
        }
        return this.asLong() == other.asLong();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public int compareTo(Key other) {
        if (this == other) {
            return 0;
        } else if (other == null) {
            // Null in Front
            return 1;
        } else {
            int r = GWTJava5Helper.longCompare(asLong(), other.asLong());
            if (r != 0) {
                return r;
            } else {
                return GWTJava5Helper.longCompare(getVersion(), other.getVersion());
            }
        }
    }
}
