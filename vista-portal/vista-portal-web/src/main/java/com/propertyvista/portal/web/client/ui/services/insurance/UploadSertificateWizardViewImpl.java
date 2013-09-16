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
package com.propertyvista.portal.web.client.ui.services.insurance;

import java.math.BigDecimal;

import com.propertyvista.domain.tenant.insurance.InsuranceGeneric;
import com.propertyvista.portal.web.client.ui.AbstractWizardView;

public class UploadSertificateWizardViewImpl extends AbstractWizardView<InsuranceGeneric> implements UploadSertificateWizardView {

    public UploadSertificateWizardViewImpl() {
        setWizard(new UploadSertificateWizard(this));
    }

    @Override
    public void setMinRequiredLiability(BigDecimal minRequiredLiability) {
        ((UploadSertificateWizard) getWizard()).setMinRequiredLiability(minRequiredLiability);
    }

}
