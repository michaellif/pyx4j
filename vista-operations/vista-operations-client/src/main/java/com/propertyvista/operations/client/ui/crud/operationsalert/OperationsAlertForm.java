/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-06-23
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.operationsalert;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.OperationsAlertDTO;

public class OperationsAlertForm extends OperationsEntityForm<OperationsAlertDTO> {

    private static final I18n i18n = I18n.get(OperationsAlertForm.class);

    public OperationsAlertForm(IFormView<OperationsAlertDTO> view) {
        super(OperationsAlertDTO.class, view);

        setTabBarVisible(false);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().namespace()).decorate();

        formPanel.append(Location.Left, proto().remoteAddr()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();
        formPanel.append(Location.Left, proto().app()).decorate();

        formPanel.append(Location.Left, proto().entityId(), new CLabel<Key>()).decorate();
        formPanel.append(Location.Left, proto().entityClass()).decorate();

        formPanel.append(Location.Dual, proto().details()).decorate();

        formPanel.append(Location.Left, proto().user(), new CLabel<Key>()).decorate();
        formPanel.append(Location.Left, proto().resolved()).decorate();
        formPanel.append(Location.Dual, proto().operationsNotes()).decorate();

        selectTab(addTab(formPanel, i18n.tr("OperationsAlert")));
    }
}
