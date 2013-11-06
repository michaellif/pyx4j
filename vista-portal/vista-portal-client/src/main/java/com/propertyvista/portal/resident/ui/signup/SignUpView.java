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
package com.propertyvista.portal.resident.ui.signup;

import java.util.List;

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationBuildingDTO;
import com.propertyvista.portal.rpc.portal.web.dto.SelfRegistrationDTO;
import com.propertyvista.portal.rpc.shared.EntityValidationException;

public interface SignUpView extends IsView {

    interface SignUpPresenter {

        void register(SelfRegistrationDTO value);

        void showVistaTerms();

        Class<? extends Place> getPortalTermsPlace();
    }

    void setPresenter(SignUpPresenter presenter);

    void setBuildingOptions(List<SelfRegistrationBuildingDTO> buildings);

    void showError(String message);

    void showValidationError(EntityValidationException caught);

}
