/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

import java.util.List;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;

public class YardiConfigurationFacadeImpl implements YardiConfigurationFacade {

    @Override
    public void initYardiCredentialCache() {
        YardiCredentials.init();
    }

    @Override
    public void clearYardiCredentialCache() {
        YardiCredentials.clear();
    }

    @Override
    public PmcYardiCredential getYardiCredential(Building building) {
        return YardiCredentials.get(building);
    }

    @Override
    public List<PmcYardiCredential> getYardiCredentials() {
        return YardiCredentials.getAll();
    }

}
