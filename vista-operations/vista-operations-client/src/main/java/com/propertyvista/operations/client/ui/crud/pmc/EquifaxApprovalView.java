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
package com.propertyvista.operations.client.ui.crud.pmc;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.operations.rpc.dto.EquifaxSetupRequestDTO;

public interface EquifaxApprovalView extends IPrimeViewerView<EquifaxSetupRequestDTO> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void approveAndSendToEquifax();

        void reject();

        void confirmSuccess();
    }

    void setEnableApprovalControls(boolean isApprovalControlsEnabled);

    void reportResult(String result);
}
