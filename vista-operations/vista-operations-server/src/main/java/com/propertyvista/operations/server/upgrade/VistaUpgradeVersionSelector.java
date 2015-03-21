/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-26
 * @author vlads
 */
package com.propertyvista.operations.server.upgrade;

import com.pyx4j.config.server.ApplicationVersion;

import com.propertyvista.operations.server.upgrade.u_1_0_5.UpgradeProcedure105;
import com.propertyvista.operations.server.upgrade.u_1_0_6.UpgradeProcedure106;
import com.propertyvista.operations.server.upgrade.u_1_0_9.UpgradeProcedure109;
import com.propertyvista.operations.server.upgrade.u_1_1_0.UpgradeProcedure110;
import com.propertyvista.operations.server.upgrade.u_1_1_3.UpgradeProcedure113;
import com.propertyvista.operations.server.upgrade.u_1_4_0.UpgradeProcedure140;
import com.propertyvista.operations.server.upgrade.u_1_4_2.UpgradeProcedure142;

class VistaUpgradeVersionSelector {

    static UpgradeProcedure getUpgradeProcedure(String schemaVersion) {
        String selector = ApplicationVersion.extractVersionMajor(schemaVersion);
        if ("1.0.5".equals(selector)) {
            return new UpgradeProcedure105();
        } else if ("1.0.6".equals(selector)) {
            return new UpgradeProcedure106();
        } else if ("1.0.9".equals(selector)) {
            return new UpgradeProcedure109();
        } else if ("1.1.0".equals(selector)) {
            return new UpgradeProcedure110();
        } else if ("1.1.3".equals(selector)) {
            return new UpgradeProcedure113();
        } else if ("1.4.0".equals(selector)) {
            return new UpgradeProcedure140();
        } else if ("1.4.2".equals(selector)) {
            return new UpgradeProcedure142();
        } else {
            return null;
        }
    }
}
