/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.user.client.ui.FlowPanel;

import com.propertyvista.portal.shared.themes.DashboardTheme;

public class MoveInWizardCompletionConfirmationViewImpl extends FlowPanel implements MoveInWizardCompletionConfirmationView {

    private final MoveInWizardCompletionConfirmationGadget gadget;

    public MoveInWizardCompletionConfirmationViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        gadget = new MoveInWizardCompletionConfirmationGadget(this);
        add(gadget);

    }

}
