/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

//
// Selection Boxes:
//
public abstract class SelectUnitBox extends OkCancelDialog {

    private static I18n i18n = I18n.get(VistaBoxFolder.class);

    private AptUnit selectedUnit;

    IListerView<Building> buildingListerView;

    IListerView<AptUnit> unitListerView;

    public SelectUnitBox(IListerView<Building> buildingListerView, IListerView<AptUnit> unitListerView) {
        super("Unit Selection");

        this.buildingListerView = buildingListerView;
        this.unitListerView = unitListerView;

        setBody(createBody());
        setSize("900px", "500px");
    }

    protected Widget createBody() {
        getOkButton().setEnabled(false);
        unitListerView.getLister().addItemSelectionHandler(new ItemSelectionHandler<AptUnit>() {
            @Override
            public void onSelect(AptUnit selectedItem) {
                selectedUnit = selectedItem;
                getOkButton().setEnabled(true);
            }
        });

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(new CrmSectionSeparator(i18n.tr("Select Building") + ":"));
        vPanel.add(buildingListerView.asWidget());
        vPanel.add(new CrmSectionSeparator(i18n.tr("Select Unit") + ":"));
        vPanel.add(unitListerView.asWidget());
        vPanel.setWidth("100%");
        return vPanel;
    }

    public AptUnit getSelectedItem() {
        return selectedUnit;
    }
}