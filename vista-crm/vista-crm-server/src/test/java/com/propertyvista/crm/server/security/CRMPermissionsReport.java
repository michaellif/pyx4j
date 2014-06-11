/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.essentials.server.admin.security.PermissionsReport;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class CRMPermissionsReport {

    public static void main(String[] args) {
        VistaTestDBSetup.init();
        List<Behavior> allNewRoles = new ArrayList<>();
        List<Behavior> allOldRoles = new ArrayList<>();
        for (VistaCrmBehavior behavior : VistaCrmBehavior.values()) {
            if (behavior.name().endsWith("_OLD")) {
                allOldRoles.add(behavior);
            } else {
                allNewRoles.add(behavior);
            }

        }
        new CRMPermissionsReport().createReport(allNewRoles);
    }

    private void createReport(List<Behavior> behaviors) {

        PermissionsReport report = new PermissionsReport();

        for (Behavior behavior : behaviors) {
            TestLifecycle.testSession(new UserVisit(new Key(-101), "bob"), behavior);
            TestLifecycle.beginRequest();

            for (Permission permission : filterClientPermissions(SecurityController.getPermissions())) {
                report.add(behavior, permission);
            }

            TestLifecycle.endRequest();
        }

        ReportTableXLSXFormatter formater = new ReportTableXLSXFormatter();
        report.formatReport(formater, behaviors);
        writeReport(formater, "CRM-permissions");
    }

    static boolean isClientPermissions(Permission permission) {
        if ((permission instanceof DataModelPermission) || (permission instanceof ActionPermission)) {
            return true;
        } else {
            return false;
        }
    }

    static Collection<Permission> filterClientPermissions(Collection<Permission> permissions) {
        Collection<Permission> cleintPermissions = new ArrayList<Permission>();
        for (Permission permission : permissions) {
            if (isClientPermissions(permission)) {
                cleintPermissions.add(permission);
            }
        }
        return cleintPermissions;
    }

    static void writeReport(ReportTableFormatter formater, String fileName) {
        String reportName = FilenameUtils.getBaseName(fileName) + ".xlsx";
        OutputStream out = null;
        try {
            out = new FileOutputStream(reportName);
            out.write(formater.getBinaryData());
        } catch (Throwable e) {
            System.err.println(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
