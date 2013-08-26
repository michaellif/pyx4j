/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.registration;

import java.util.List;

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.dto.SelfRegistrationDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;

public interface TenantRegistrationView extends IsView {

    interface Presenter {

        void onRegister();

        void onShowVistaTerms();

        Class<? extends Place> getPortalTermsPlace();

    }

    void setPresenter(Presenter presenter);

    void populate(List<SelfRegistrationBuildingDTO> buildings);

    SelfRegistrationDTO getValue();

    void showError(String message);

    void showValidationError(EntityValidationException caught);

}
