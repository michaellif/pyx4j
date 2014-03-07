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
 * @version $Id$
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
                if (yc.getPrimaryKey().equals(building.integrationSystemId().getValue())) {
                    return yc;
                }
            }
            return null;
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
        yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
        return yc;
    }

    private static List<PmcYardiCredential> retrieveCredentials() {
        List<PmcYardiCredential> ycList = VistaDeployment.getPmcYardiCredentials();
        for (PmcYardiCredential yc : ycList) {
            yc.password().number().setValue(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
        }
        return ycList;
    }
}
