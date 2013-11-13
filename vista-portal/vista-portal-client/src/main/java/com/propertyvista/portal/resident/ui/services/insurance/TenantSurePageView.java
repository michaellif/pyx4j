/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.IEditorView;

public interface TenantSurePageView extends IEditorView<TenantSureInsurancePolicyDTO> {

    public interface TenantSurePagePresenter extends IEditorPresenter<TenantSureInsurancePolicyDTO> {

        void sendCertificate(String email);

        void updateCreditCardDetails();

        void viewFaq();

        void viewAboutTenantSure();

        void cancelTenantSure();

        void reinstate();

        void makeAClaim();

    }

    void displayMakeAClaimDialog();

    void acknowledgeSentCertificateSuccesfully(String email);

}
