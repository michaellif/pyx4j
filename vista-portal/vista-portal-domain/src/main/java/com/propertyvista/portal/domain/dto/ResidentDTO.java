/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.domain.Person;
import com.propertyvista.portal.domain.ptapp.Address;
import com.propertyvista.portal.domain.ptapp.EmergencyContact;
import com.propertyvista.portal.domain.ptapp.Vehicle;

@Transient
public interface ResidentDTO extends Person {

    @Owned
    @Caption(name = "Address")
    Address currentAddress();

    @Owned
    @Length(3)
    @Caption(name = "Vehicls")
    IList<Vehicle> vehicles();

    @Owned
    @Length(3)
    @Caption(name = "Emergency Contact")
    EmergencyContact emergencyContact();

}
