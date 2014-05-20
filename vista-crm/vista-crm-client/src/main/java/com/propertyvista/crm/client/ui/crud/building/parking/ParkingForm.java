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
package com.propertyvista.crm.client.ui.crud.building.parking;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.ParkingDTO;

public class ParkingForm extends CrmEntityForm<ParkingDTO> {

    private static final I18n i18n = I18n.get(ParkingForm.class);

    public ParkingForm(IForm<ParkingDTO> view) {
        super(ParkingDTO.class, view);

        Tab tab = addTab(createDetailsTab(), i18n.tr("Details"));
        selectTab(tab);

        tab = addTab(isEditable() ? new HTML() : ((ParkingViewerView) getParentView()).getSpotView().asWidget(), i18n.tr("Spots"));
        setTabEnabled(tab, !isEditable());
    }

    private DualColumnForm createDetailsTab() {
        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().type()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().levels()).decorate().componentWidth(50);

        formPanel.append(Location.Right, proto().totalSpaces()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().regularSpaces()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().disabledSpaces()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().wideSpaces()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().narrowSpaces()).decorate().componentWidth(50);

        formPanel.append(Location.Dual, proto().description()).decorate();

        return formPanel;
    }
}