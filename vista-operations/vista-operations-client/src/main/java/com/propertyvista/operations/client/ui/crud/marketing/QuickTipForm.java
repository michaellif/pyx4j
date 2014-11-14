/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.marketing;

import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.domain.marketing.PortalResidentMarketingTip;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;

public class QuickTipForm extends OperationsEntityForm<PortalResidentMarketingTip> {

    private final static I18n i18n = I18n.get(QuickTipForm.class);

    public QuickTipForm(IPrimeFormView<PortalResidentMarketingTip, ?> view) {
        super(PortalResidentMarketingTip.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().target()).decorate().componentWidth(200);
        formPanel.append(Location.Dual, proto().comments()).decorate().componentWidth(200);
        formPanel.append(Location.Dual, proto().content(), new CRichTextArea()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
