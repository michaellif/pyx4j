/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-03
 * @author vlads
 */
package com.propertyvista.biz.tenant.insurance;

import java.io.File;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.operations.domain.tenantsure.TenantSureHQUpdateFile;

class HQUpdate {

    static TenantSureHQUpdateFile receiveHQUpdatesFile() {
        File sftpDir = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureInterfaceSftpDirectory();
        File dirHqUpdate = new File(sftpDir, "hq-update");
        return null;
    }

    static void processHQUpdate(ExecutionMonitor executionMonitor, TenantSureHQUpdateFile fileId) {
        // Process data for single PMC.
        // TODO Auto-generated method stub

    }
}
