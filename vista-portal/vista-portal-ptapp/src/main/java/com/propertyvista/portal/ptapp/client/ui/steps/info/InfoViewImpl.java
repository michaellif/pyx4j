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
package com.propertyvista.portal.ptapp.client.ui.steps.info;

import com.propertyvista.portal.ptapp.client.ui.steps.WizardStepViewImpl;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInfoDTO;

public class InfoViewImpl extends WizardStepViewImpl<TenantInfoDTO, InfoViewPresenter> implements InfoView {

    public InfoViewImpl() {
        super(new InfoViewForm());
    }

}
