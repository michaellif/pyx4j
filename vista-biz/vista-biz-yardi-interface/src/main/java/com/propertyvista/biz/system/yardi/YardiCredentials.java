/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author stanp
 */
package com.propertyvista.biz.system.yardi;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;

public class YardiCredentials {

    private static ThreadLocal<List<PmcYardiCredential>> ycCache = new ThreadLocal<>();

    public static PmcYardiCredential get(Building building) {
        List<PmcYardiCredential> ycMap = ycCache.get();
        if (ycMap == null) {
            return retrieveCredential(building);
        } else {
            for (PmcYardiCredential yc : ycCache.get()) {
                if (yc.getPrimaryKey().equals(building.integrationSystemId().getValue()) && yc.enabled().getValue(false)) {
                    return yc;
                }
            }
            throw new YardiCredentialDisabledException();
        }
    }

    public static List<PmcYardiCredential> getAll() {
        return ycCache.get() == null ? retrieveCredentials() : ycCache.get();
    }

    public static void init() {
        if (ycCache.get() == null) {
            ycCache.set(new ArrayList<PmcYardiCredential>(VistaDeployment.getPmcYardiCredentials()));
            for (PmcYardiCredential yc : ycCache.get()) {
                yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            }
        }
    }

    public static void clear() {
        ycCache.remove();
    }

    private static PmcYardiCredential retrieveCredential(Building building) {
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(building);
        if (yc == null) {
            throw new YardiCredentialDisabledException();
        }

        yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
        return yc;
    }

    private static List<PmcYardiCredential> retrieveCredentials() {
        List<PmcYardiCredential> ycList = VistaDeployment.getPmcYardiCredentials();
        if (ycList == null || ycList.isEmpty()) {
            throw new YardiCredentialDisabledException();
        }
        for (PmcYardiCredential yc : ycList) {
            yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
        }
        return ycList;
    }
}
