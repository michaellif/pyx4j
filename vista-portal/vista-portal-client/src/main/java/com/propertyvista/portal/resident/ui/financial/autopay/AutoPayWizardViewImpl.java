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
package com.propertyvista.portal.resident.ui.financial.autopay;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.resident.dto.financial.AutoPayDTO;
import com.propertyvista.portal.shared.ui.AbstractWizardFormView;
import com.propertyvista.portal.shared.ui.IWizardView;

public class AutoPayWizardViewImpl extends AbstractWizardFormView<AutoPayDTO> implements AutoPayWizardView {

    private static final I18n i18n = I18n.get(AutoPayWizardViewImpl.class);

    public AutoPayWizardViewImpl() {
        super();
        setWizard(new AutoPayWizard(this));
    }

    @Override
    public void setPresenter(IWizardView.IWizardFormPresenter<AutoPayDTO> presenter) {
        super.setPresenter(presenter);
        ((AutoPayWizard) getWizard()).setPresenter((AutoPayWizardView.Presenter) presenter);
    }
}
