/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.domain.ptapp;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.domain.tenant.TenantScreening;

@Deprecated
public interface PotentialTenantInfo extends PotentialTenant, TenantScreening {

    /**
     * TODO I think that it is better to have a list here since some forms may ask for
     * more than one previous address
     */
    @Override
    @Owned
    Address currentAddress();

    @Override
    @Owned
    Address previousAddress();

    @Override
    @Owned
    @Length(3)
    IList<Vehicle> vehicles();

    @Override
    @Owned
    @Length(3)
    IList<EmergencyContact> emergencyContacts();
}
