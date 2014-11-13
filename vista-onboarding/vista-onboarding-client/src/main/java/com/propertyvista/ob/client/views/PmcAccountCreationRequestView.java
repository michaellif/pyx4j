/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.views;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;

import com.propertyvista.ob.rpc.dto.PmcAccountCreationRequest;

public interface PmcAccountCreationRequestView extends IPrimePaneView {

    interface Presenter extends IPrimePaneView.IPrimePanePresenter {

        void checkDns(AsyncCallback<Boolean> callback, String dnsName);

        void createAccount();

        void openTerms();

    }

    void setPresenter(Presenter presenter);

    void setEnabled(boolean isEnabled);

    void setMessage(String message);

    PmcAccountCreationRequest getPmcAccountCreationRequest();

    void setPmcAccountCreationRequest(PmcAccountCreationRequest request);

}
