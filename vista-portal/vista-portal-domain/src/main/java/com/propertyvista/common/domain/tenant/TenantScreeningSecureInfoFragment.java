/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.domain.tenant;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.common.domain.ref.Province;

@AbstractEntity
public interface TenantScreeningSecureInfoFragment extends IEntity {

    IPrimitive<String> driversLicense();

    @Caption(name = "Province/State", description = "Province/State, in which a license has been issued.")
    @Editor(type = EditorType.combo)
    Province driversLicenseState();

    @Caption(name = "SIN")
    IPrimitive<String> secureIdentifier();

    @Caption(name = "I'm not resident of Canada")
    IPrimitive<Boolean> notCanadianCitizen();

}
