/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.views;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantInsuranceStatusDTO;

public interface ProvideTenantInsuranceView extends IsView {

    interface Presenter {

        void onPurchaseTenantSure();

        void onUpdateInsuranceByOtherProvider();

    }

    void setPresenter(Presenter presenter);

    void setTenantSureInvitationEnabled(boolean tenantSureInvitationEnabled);

    void populate(TenantInsuranceStatusDTO insuranceStatus);

}
