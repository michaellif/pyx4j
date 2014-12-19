/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-07-17
 * @author VladL
 */
package com.propertyvista.operations.client.ui.crud.outgoingmail;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.OutgoingMailQueueDTO;

public class OutgoingMailForm extends OperationsEntityForm<OutgoingMailQueueDTO> {

    private static final I18n i18n = I18n.get(OutgoingMailForm.class);

    public OutgoingMailForm(IPrimeFormView<OutgoingMailQueueDTO, ?> view) {
        super(OutgoingMailQueueDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().status()).decorate();
        formPanel.append(Location.Left, proto().namespace()).decorate();
        formPanel.append(Location.Left, proto().configurationId()).decorate();
        formPanel.append(Location.Left, proto().statusCallbackClass()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();
        formPanel.append(Location.Left, proto().updated()).decorate();
        formPanel.append(Location.Left, proto().attempts()).decorate();
        formPanel.append(Location.Left, proto().priority()).decorate();
        formPanel.append(Location.Left, proto().lastAttemptErrorMessage()).decorate();
        formPanel.append(Location.Left, proto().sendTo()).decorate();
        formPanel.append(Location.Left, proto().sentDate()).decorate();
        formPanel.append(Location.Left, proto().messageId()).decorate();
        formPanel.append(Location.Left, proto().keywords()).decorate();
        formPanel.append(Location.Left, proto().subject()).decorate();
        formPanel.append(Location.Dual, proto().message()).decorate();

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
