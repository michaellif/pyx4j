/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.operations.rpc.dto.ConnectionTestResultDTO;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.stubs.YardiLicense;

public class YardiOperationsFacadeImpl implements YardiOperationsFacade {

    @Override
    public void restLicenseCache() {
        YardiLicense.restLicenseCache();
    }

    @Override
    public ConnectionTestResultDTO verifyInterface(PmcYardiCredential yc) {
        ConnectionTestResultDTO result = new ConnectionTestResultDTO();
        // TODO implement ping   VISTA-3820

        for (YardiInterface yardiInterface : YardiInterface.values()) {
            result.append("TODO Connection Ping ").append(yardiInterface.name()).ok().cr();
            result.append("Version ").append(yardiInterface.name()).append(" v1.5").ok().cr();
        }

        result.error("Mandaroy resdentTransaction interface not working").cr();
        result.warn("Collections not requeired and it do not work");

        return result;
    }
}
