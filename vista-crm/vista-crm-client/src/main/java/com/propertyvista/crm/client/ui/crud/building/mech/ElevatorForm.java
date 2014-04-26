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

import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.LicenseEditor;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorForm extends MechBaseForm<ElevatorDTO> {

    private static final I18n i18n = I18n.get(ElevatorForm.class);

    public ElevatorForm(IForm<ElevatorDTO> view) {
        super(ElevatorDTO.class, view);
    }

    @Override
    protected FormPanel createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Information"));
        formPanel.append(Location.Left, proto().type()).decorate().componentWidth(160);
        formPanel.append(Location.Left, proto().make()).decorate().componentWidth(160);
        formPanel.append(Location.Left, proto().model()).decorate().componentWidth(160);
        formPanel.append(Location.Right, proto().build()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().isForMoveInOut()).decorate().componentWidth(80);

        formPanel.append(Location.Full, proto().description()).decorate();

        formPanel.h1(proto().license().getMeta().getCaption());
        formPanel.append(Location.Full, proto().license(), new LicenseEditor());

        return formPanel;
    }

    @Override
    public void addValidations() {
        get(proto().build()).addComponentValidator(new PastDateIncludeTodayValidator());
    }
}