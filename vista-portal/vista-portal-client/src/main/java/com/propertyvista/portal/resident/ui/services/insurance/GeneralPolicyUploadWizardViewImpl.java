/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import java.math.BigDecimal;

import com.propertyvista.portal.rpc.portal.resident.dto.insurance.GeneralInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardFormView;

public class GeneralPolicyUploadWizardViewImpl extends AbstractWizardFormView<GeneralInsurancePolicyDTO> implements GeneralPolicyUploadWizardView {

    public GeneralPolicyUploadWizardViewImpl() {
        setWizard(new GeneralPolicyUploadWizard(this));
    }

    @Override
    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        ((GeneralPolicyUploadWizard) getWizard()).setMinRequiredLiability(minRequiredLiability);
    }

}
