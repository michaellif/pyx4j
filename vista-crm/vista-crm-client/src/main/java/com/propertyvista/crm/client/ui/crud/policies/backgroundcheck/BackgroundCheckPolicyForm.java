/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.backgroundcheck;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.resources.CrmResources;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.BackgroundCheckPolicyDTO;

public class BackgroundCheckPolicyForm extends PolicyDTOTabPanelBasedForm<BackgroundCheckPolicyDTO> {

    private final static I18n i18n = I18n.get(BackgroundCheckPolicyForm.class);

    public BackgroundCheckPolicyForm(IForm<BackgroundCheckPolicyDTO> view) {
        super(BackgroundCheckPolicyDTO.class, view);
        addTab(createItemsPanel(), i18n.tr("Policy"));
    }

    private IsWidget createItemsPanel() {
        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.append(Location.Left, proto().version().bankruptcy()).decorate().componentWidth(60);
        formPanel.append(Location.Left, proto().version().judgment()).decorate().componentWidth(60);
        formPanel.append(Location.Left, proto().version().collection()).decorate().componentWidth(60);
        formPanel.append(Location.Left, proto().version().chargeOff()).decorate().componentWidth(60);

        formPanel.h3(i18n.tr("Help"));

        HTML backgroundCheckHelp = new HTML(CrmResources.INSTANCE.backgroundCheckHelp().getText());
        formPanel.append(Location.Left, backgroundCheckHelp);
        backgroundCheckHelp.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        return formPanel;
    }
}
