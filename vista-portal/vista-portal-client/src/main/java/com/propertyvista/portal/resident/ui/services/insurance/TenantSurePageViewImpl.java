/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.AbstractEditorView;

public class TenantSurePageViewImpl extends AbstractEditorView<TenantSureInsurancePolicyDTO> implements TenantSurePageView {

    public TenantSurePageViewImpl() {
        setForm(new TenantSurePage(this));
    }

    @Override
    public void setPresenter(com.propertyvista.portal.shared.ui.IFormView.IFormPresenter<TenantSureInsurancePolicyDTO> presenter) {
        ((TenantSurePage) getForm()).setPresenter((TenantSurePagePresenter) presenter);
        super.setPresenter(presenter);
    }

    @Override
    public void displayMakeAClaimDialog() {
        MessageDialog.info(i18n.tr("To make a claim please call {0} at {1}", TenantSureConstants.TENANTSURE_LEGAL_NAME,
                TenantSureConstants.TENANTSURE_PHONE_NUMBER));
    }

    @Override
    public void acknowledgeSentCertificateSuccesfully(String email) {
        MessageDialog.info(i18n.tr("Your insurance certificate was sent to {0}.", email));
    }
}
