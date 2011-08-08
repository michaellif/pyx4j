/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps;

import com.propertyvista.portal.rpc.ptapp.dto.UnitInfoDTO;

public class ApartmentViewImpl extends WizardStepViewImpl<UnitInfoDTO, ApartmentViewPresenter> implements ApartmentView {

    public ApartmentViewImpl() {
        super(new ApartmentViewForm());

    }

    @Override
    public void setPresenter(ApartmentViewPresenter presenter) {
        super.setPresenter(presenter);
        ((ApartmentViewForm) getForm()).setPresenter(getPresenter());
    }

}
