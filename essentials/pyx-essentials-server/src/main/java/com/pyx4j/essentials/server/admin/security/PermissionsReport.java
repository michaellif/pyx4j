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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.pyx4j.essentials.server.docs.sheet.ReportTableFormatter;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.Permission;

public class PermissionsReport {

    Map<String, PermissionReportColumn> permissionInfo = new TreeMap<>();

    public PermissionsReport() {

    }

    public void add(Behavior behavior, Permission permission) {
        String key = new PermissionInfo(permission).getKey();

        PermissionReportColumn info = permissionInfo.get(key);
        if (info == null) {
            info = new PermissionReportColumn();
            permissionInfo.put(key, info);
        }

        info.add(behavior, permission);
    }

    public void formatReport(ReportTableFormatter formater, List<Behavior> behaviors) {
        formater.header("Behavior");
        for (PermissionReportColumn column : permissionInfo.values()) {
            formater.header(column.getTitle());
        }
        formater.newRow();

        for (Behavior behavior : behaviors) {
            formater.cell(behavior);
            for (PermissionReportColumn column : permissionInfo.values()) {
                formater.cell(column.getValue(behavior));
            }
            formater.newRow();
        }
    }
}