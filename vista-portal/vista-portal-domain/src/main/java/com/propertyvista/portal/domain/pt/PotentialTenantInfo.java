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
package com.propertyvista.portal.domain.pt;

import com.propertyvista.portal.domain.ref.Province;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface PotentialTenantInfo extends PotentialTenant {

    // secure information
    IPrimitive<String> driversLicense();

    @Caption(name = "License Province", description = "Province, in which a license has been issued.")
    @Editor(type = EditorType.combo)
    Province driversLicenseState();

    @Caption(name = "SIN")
    IPrimitive<String> secureIdentifier();

    IPrimitive<Boolean> canadianCitizen();

    /**
     * TODO I think that it is better to have a list here since some forms may ask for
     * more than one previous address
     */
    @Owned
    Address currentAddress();

    @Owned
    Address previousAddress();

    @Owned
    @Length(3)
    IList<Vehicle> vehicles();

    @Owned
    @Caption(name = "General Questions")
    LegalQuestions legalQuestions();

    @Owned
    EmergencyContact emergencyContact1();

    @Owned
    EmergencyContact emergencyContact2();

}
