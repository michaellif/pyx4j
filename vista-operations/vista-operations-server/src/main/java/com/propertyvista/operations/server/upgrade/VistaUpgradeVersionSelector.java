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
 * @version $Id$
 */
package com.propertyvista.operations.server.upgrade;

import com.pyx4j.config.server.ApplicationVersion;

import com.propertyvista.operations.server.upgrade.u_1_0_5.UpgradeProcedure105;
import com.propertyvista.operations.server.upgrade.u_1_0_6.UpgradeProcedure106;
import com.propertyvista.operations.server.upgrade.u_1_0_9.UpgradeProcedure109;
import com.propertyvista.operations.server.upgrade.u_1_1_0.UpgradeProcedure110;
import com.propertyvista.operations.server.upgrade.u_1_1_0_7.UpgradeProcedure1107;

class VistaUpgradeVersionSelector {

    static UpgradeProcedure getUpgradeProcedure(String schemaVersion) {
        String majorVersion = ApplicationVersion.extractVersionMajor(schemaVersion);
        if ("1.0.5".equals(majorVersion)) {
            return new UpgradeProcedure105();
        } else if ("1.0.6".equals(majorVersion)) {
            return new UpgradeProcedure106();
        } else if ("1.0.9".equals(majorVersion)) {
            return new UpgradeProcedure109();
        } else if ("1.1.0".equals(majorVersion)) {
            return new UpgradeProcedure110();
        } else if ("1.1.0.7".equals(majorVersion)) {
            return new UpgradeProcedure1107();
        } else {
            return null;
        }
    }
}
