/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-13
 * @author vlads
 */
package com.propertyvista.dto.vista2pmc;

import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.pmc.info.PersonalInformation;
import com.propertyvista.domain.pmc.info.PmcAddress;

//solution to CRM country namespace editing in CRM and saving in admin
@Transient
public interface PersonalInformationDTO extends PersonalInformation {

    PmcAddress dto_personalAddress();

}
