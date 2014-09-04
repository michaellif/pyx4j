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
package com.propertyvista.biz.system.yardi;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.operations.rpc.dto.ConnectionTestResultDTO;
import com.propertyvista.yardi.YardiInterface;
import com.propertyvista.yardi.YardiInterfaceType;
import com.propertyvista.yardi.stubs.YardiLicense;
import com.propertyvista.yardi.stubs.YardiStubFactory;

public class YardiOperationsFacadeImpl implements YardiOperationsFacade {

    @Override
    public void restLicenseCache() {
        YardiLicense.restLicenseCache();
    }

    @Override
    public ConnectionTestResultDTO verifyInterface(PmcYardiCredential yc) {
        yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));

        ConnectionTestResultDTO result = new ConnectionTestResultDTO();

        verifyInterface(yc, result);

        return result;
    }

    @Override
    public void verifyInterface(PmcYardiCredential yc, ConnectionTestResultDTO result) {
        yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));

        int processed = 0;

        for (YardiInterfaceType yardiInterface : YardiInterfaceType.values()) {
            if (yardiInterface.ifClass == null) {
                updateProgress(result, ++processed, null);
                continue;
            }

            result.append("Plugin: ").append(yardiInterface.name()).cr();
            YardiInterface stub = YardiStubFactory.create(yardiInterface.ifClass);

            result.sp().sp().append("Ping: ");
            try {
                stub.ping(yc);
                result.ok();
            } catch (Throwable t) {
                result.error(t.getMessage());
            }
            result.cr();
            updateProgress(result, ++processed, yardiInterface.name() + " - checking connection...");

            result.sp().sp().append("Version: ");
            try {
                result.append(stub.getPluginVersion(yc)).ok();
            } catch (Throwable t) {
                result.error(t.getMessage());
            }
            result.cr();
            updateProgress(result, ++processed, yardiInterface.name() + " - checking version...");

            result.sp().sp().append("Credentials: ");
            try {
                stub.validate(yc);
                result.ok();
            } catch (Throwable t) {
                result.error(t.getMessage());
            }
            result.cr();
            updateProgress(result, ++processed, yardiInterface.name() + " - checking credentials...");
        }
    }

    private void updateProgress(ConnectionTestResultDTO result, int processed, String message) {
        int total = YardiInterfaceType.values().length * 3;
        result.setProgressPct(processed * 100 / total);
        result.setProgressMessage(message);
    }
}
