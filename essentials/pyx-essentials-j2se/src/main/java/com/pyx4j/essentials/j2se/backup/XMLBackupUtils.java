/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Oct 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.j2se.backup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.appengine.repackaged.com.google.common.util.Base64;

import com.pyx4j.essentials.rpc.admin.BackupKey;
import com.pyx4j.geo.GeoPoint;

public class XMLBackupUtils {

    public static String getValueType(Object value) {
        if ((value == null) || (value instanceof String)) {
            return null;
        } else if (value instanceof byte[]) {
            return "byte[]";
        } else if (value instanceof BackupKey) {
            return "Key";
        } else if (value instanceof GeoPoint) {
            return "GeoPoint";
        } else {
            String name = value.getClass().getName();
            if ((name.startsWith("java.lang.")) || (name.startsWith("java.util."))) {
                name = value.getClass().getSimpleName();
            }
            return name;
        }
    }

    public static String getValueAsString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof byte[]) {
            return Base64.encode((byte[]) value);
        } else if (value instanceof java.sql.Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format((Date) value);
        } else if (value instanceof Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.format((Date) value);
        } else {
            return value.toString();
        }
    }
}
