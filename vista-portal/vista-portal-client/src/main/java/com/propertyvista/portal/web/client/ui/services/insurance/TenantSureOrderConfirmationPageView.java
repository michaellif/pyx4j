/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-10
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.web.client.ui.IFormView;

public interface TenantSureOrderConfirmationPageView extends IFormView<TenantSureInsurancePolicyDTO> {

    public interface TenantSureOrderConfirmationPagePresenter extends IFormView.IFormViewPresenter<TenantSureInsurancePolicyDTO> {

        void back();
    }
}
