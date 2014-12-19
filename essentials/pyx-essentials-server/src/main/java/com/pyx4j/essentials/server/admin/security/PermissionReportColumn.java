/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 11, 2014
 * @author vlads
 */
package com.pyx4j.essentials.server.admin.security;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.Permission;

class PermissionReportColumn {

    Map<Behavior, PermissionInfo> values = new HashMap<>();

    PermissionReportColumn() {

    }

    public void add(Behavior behavior, Permission permission) {
        PermissionInfo info = values.get(behavior);
        if (info == null) {
            info = new PermissionInfo(permission);
            values.put(behavior, info);
        }
        info.add(permission);
    }

    String getValue(Behavior behavior) {
        PermissionInfo info = values.get(behavior);
        if (info == null) {
            return null;
        } else {
            return info.getValue();
        }
    }

    String getTitle() {
        return values.values().iterator().next().getTitle();
    }
}