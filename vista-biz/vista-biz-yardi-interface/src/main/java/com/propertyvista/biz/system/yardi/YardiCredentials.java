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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;

public class YardiCredentials {

    private static class YardiCredentialsCache {

        Map<Key, PmcYardiCredential> byInterfaceId = new HashMap<>();

        Map<Building, PmcYardiCredential> byBuildingId = new HashMap<>();

    }

    private static ThreadLocal<YardiCredentialsCache> ycCacheThreadLocal = new ThreadLocal<>();

    public static PmcYardiCredential get(Building buildingId) {
        YardiCredentialsCache ycCache = ycCacheThreadLocal.get();
        if (ycCache == null) {
            return retrieveCredential(buildingId);
        } else {
            PmcYardiCredential yc = ycCache.byBuildingId.get(buildingId);
            if (yc != null) {
                return yc;
            }
            Key ycKey = VistaDeployment.getPmcYardiInterfaceId(buildingId);
            yc = ycCache.byInterfaceId.get(ycKey);
            if (yc == null) {
                yc = retrieveCredential(buildingId);
                // New building case...
                ycCache.byInterfaceId.put(yc.getPrimaryKey(), yc);
            }
            ycCache.byBuildingId.put(buildingId, yc);
            return yc;
        }
    }

    public static Collection<PmcYardiCredential> getAll() {
        return ycCacheThreadLocal.get() == null ? retrieveAllCredentials() : ycCacheThreadLocal.get().byInterfaceId.values();
    }

    public static void init() {
        if (ycCacheThreadLocal.get() == null) {
            YardiCredentialsCache ycCache = new YardiCredentialsCache();
            for (PmcYardiCredential yc : VistaDeployment.getPmcYardiCredentials()) {
                yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
                ycCache.byInterfaceId.put(yc.getPrimaryKey(), yc);
            }
            ycCacheThreadLocal.set(ycCache);
        }
    }

    public static void clear() {
        ycCacheThreadLocal.remove();
    }

    private static PmcYardiCredential retrieveCredential(Building building) {
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(building);
        if (yc == null) {
            throw new YardiCredentialDisabledException();
        }

        yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
        return yc;
    }

    private static List<PmcYardiCredential> retrieveAllCredentials() {
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
