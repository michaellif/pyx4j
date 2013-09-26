/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceTenantSureCertificateDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;

public class TenantSureOrderWizard extends CPortalEntityWizard<InsuranceTenantSureCertificateDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderWizard.class);

    public TenantSureOrderWizard(TenantSureOrderWizardView view, String endButtonCaption) {
        super(InsuranceTenantSureCertificateDTO.class, view, i18n.tr("TenantSure Insurance"), endButtonCaption, ThemeColor.contrast3);
    }

}
