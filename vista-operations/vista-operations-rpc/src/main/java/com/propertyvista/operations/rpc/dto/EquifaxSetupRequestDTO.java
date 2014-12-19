/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 */
package com.propertyvista.operations.rpc.dto;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.dto.vista2pmc.BusinessInformationDTO;
import com.propertyvista.dto.vista2pmc.PersonalInformationDTO;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface EquifaxSetupRequestDTO extends PmcEquifaxInfo {

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> setupFee();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> perApplicantFeee();

    @Override
    BusinessInformationDTO businessInformation();

    @Override
    PersonalInformationDTO personalInformation();
}
