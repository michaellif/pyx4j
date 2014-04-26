/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.mech;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.LicenseEditor;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.dto.BoilerDTO;

public class BoilerForm extends MechBaseForm<BoilerDTO> {

    private static final I18n i18n = I18n.get(BoilerForm.class);

    public BoilerForm(IForm<BoilerDTO> view) {
        super(BoilerDTO.class, view);
    }

    @Override
    protected IsWidget createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Information"));
        formPanel.append(Location.Left, injectAndDecorate(proto().type(), 15));
        formPanel.append(Location.Left, injectAndDecorate(proto().make(), 15));
        formPanel.append(Location.Left, injectAndDecorate(proto().model(), 15));
        formPanel.append(Location.Full, injectAndDecorate(proto().description(), true));

        formPanel.h1(proto().license().getMeta().getCaption());
        formPanel.append(Location.Full, inject(proto().license(), new LicenseEditor()));

        formPanel.append(Location.Right, injectAndDecorate(proto().build(), 9));

        return formPanel;
    }

    @Override
    public void addValidations() {
        get(proto().build()).addComponentValidator(new PastDateIncludeTodayValidator());
    }
}