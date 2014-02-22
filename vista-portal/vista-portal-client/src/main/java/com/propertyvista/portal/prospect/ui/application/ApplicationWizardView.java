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
package com.propertyvista.portal.prospect.ui.application;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.UnitTO;
import com.propertyvista.portal.shared.ui.IWizardView;

public interface ApplicationWizardView extends IWizardView<OnlineApplicationDTO> {

    public interface ApplicationWizardPresenter extends IWizardFormPresenter<OnlineApplicationDTO> {

        void getAvailableUnits(AsyncCallback<UnitSelectionDTO> callback, UnitSelectionDTO editableEntity);

        void getAvailableUnitOptions(AsyncCallback<UnitOptionsSelectionDTO> callback, UnitTO unit);

        void getProfiledPaymentMethods(AsyncCallback<List<LeasePaymentMethod>> callback);

        void downloadLeaseAgreementDraft();
    }

    ApplicationWizard getWizard();
}
