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

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardFormView;
import com.propertyvista.portal.shared.ui.IWizardView;

public class ApplicationWizardViewImpl extends AbstractWizardFormView<OnlineApplicationDTO> implements ApplicationWizardView {

    private static final I18n i18n = I18n.get(ApplicationWizardViewImpl.class);

    public ApplicationWizardViewImpl() {
        super();

    }

    @Override
    public void setPresenter(IWizardView.IWizardFormPresenter<OnlineApplicationDTO> presenter) {
        super.setPresenter(presenter);
        if (presenter == null) {
            setWizard(null);
        } else {
            ApplicationWizard applicationWizard = new ApplicationWizard(this);
            setWizard(applicationWizard);

            applicationWizard.setPresenter((ApplicationWizardPresenter) presenter);
        }
    }
}
