/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.visors;

import com.pyx4j.site.client.ui.visor.IVisor;

import com.propertyvista.crm.client.ui.tools.legal.l1.L1FormDataReviewWizardView;
import com.propertyvista.crm.client.ui.tools.legal.l1.L1FormDataReviewWizardViewImpl;

public class L1FormDataReviewVisorView extends L1FormDataReviewWizardViewImpl implements IVisor {

    private final Controller controller;

    public L1FormDataReviewVisorView(Controller controller, L1FormDataReviewWizardView.Presenter presenter) {
        this.controller = controller;
        this.setPresenter(presenter);
        this.asWidget().addStyleName(L1VisorStyles.L1GenerationVisor.name());
    }

    @Override
    public Controller getController() {
        return this.controller;
    }
}
