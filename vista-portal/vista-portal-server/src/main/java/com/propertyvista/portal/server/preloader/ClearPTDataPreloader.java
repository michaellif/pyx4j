/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.domain.ptapp.UnitSelection;

public class ClearPTDataPreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Application.class, UnitSelection.class, PotentialTenantList.class, PotentialTenantFinancial.class, PotentialTenantInfo.class);
        } else {
            return "This is production";
        }
    }

}
