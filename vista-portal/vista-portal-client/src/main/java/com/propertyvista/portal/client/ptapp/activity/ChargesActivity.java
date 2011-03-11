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
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.ui.ChargesView;
import com.propertyvista.portal.client.ptapp.ui.ChargesViewPresenter;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.rpc.pt.services.ChargesServices;

public class ChargesActivity extends WizardStepActivity<Charges, ChargesViewPresenter> implements ChargesViewPresenter {

    @Inject
    public ChargesActivity(ChargesView view) {
        super(view, Charges.class, (ChargesServices) GWT.create(ChargesServices.class));
    }
}
