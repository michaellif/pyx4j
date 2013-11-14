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
import com.propertyvista.yardi.stub.YardiLicense;

public class YardiOperationsFacadeImpl implements YardiOperationsFacade {

    @Override
    public void restLicenseCache() {
        YardiLicense.restLicenseCache();
    }

    @Override
    public void verifyInterface(PmcYardiCredential yc, Void yardiInterfaceType) {
        // TODO implement ping
    }

}
