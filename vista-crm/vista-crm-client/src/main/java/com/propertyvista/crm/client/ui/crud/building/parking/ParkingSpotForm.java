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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.ParkingSpot;

public class ParkingSpotForm extends CrmEntityForm<ParkingSpot> {

    private static final I18n i18n = I18n.get(ParkingSpotForm.class);

    public ParkingSpotForm(IForm<ParkingSpot> view) {
        super(ParkingSpot.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().type()).decorate().componentWidth(120);

        selectTab(addTab(formPanel, i18n.tr("General")));

    }
}