/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopay;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.crm.client.ui.reports.eft.SelectedBuildingsFolder;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;

public class AutoPayChangesReportSettingsForm extends CForm<AutoPayChangesReportMetadata> {

    private final IPane parentView;

    public AutoPayChangesReportSettingsForm(IPane parentView) {
        super(AutoPayChangesReportMetadata.class);
        this.parentView = parentView;
    }

    @Override
    protected IsWidget createContent() {
        DualColumnForm formPanel = new DualColumnForm(this);

        FlowPanel leftSidePanel = new FlowPanel();

        leftSidePanel.add(inject(proto().leasesOnNoticeOnly(), new FieldDecoratorBuilder().build()));
        get(proto().leasesOnNoticeOnly()).asWidget().getElement().getStyle().setDisplay(Display.BLOCK);
        leftSidePanel.add(inject(proto().filterByExpectedMoveOut(), new FieldDecoratorBuilder().build()));
        get(proto().filterByExpectedMoveOut()).asWidget().getElement().getStyle().setDisplay(Display.BLOCK);
        get(proto().filterByExpectedMoveOut()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().minimum()).setVisible(event.getValue() == true);
                get(proto().maximum()).setVisible(event.getValue() == true);
            }
        });
        leftSidePanel.add(inject(proto().minimum(), new FieldDecoratorBuilder().componentWidth("100px").build()));
        get(proto().minimum()).asWidget().getElement().getStyle().setDisplay(Display.BLOCK);
        leftSidePanel.add(inject(proto().maximum(), new FieldDecoratorBuilder().componentWidth("100px").build()));
        get(proto().maximum()).asWidget().getElement().getStyle().setDisplay(Display.BLOCK);

        FlowPanel buildingFilterPanel = new FlowPanel();
        buildingFilterPanel.add(inject(proto().filterByBuildings(), new FieldDecoratorBuilder().build()));
        get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().buildings()).setVisible(event.getValue() == true);
            }
        });

        buildingFilterPanel.add(inject(proto().buildings(), new SelectedBuildingsFolder(parentView)));
        get(proto().buildings()).setVisible(false);

        formPanel.append(Location.Left, leftSidePanel);
        formPanel.append(Location.Right, buildingFilterPanel);

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().buildings()).setVisible(getValue().filterByBuildings().getValue(false));
        get(proto().minimum()).setVisible(getValue().filterByExpectedMoveOut().getValue(false));
        get(proto().maximum()).setVisible(getValue().filterByExpectedMoveOut().getValue(false));
    }

}
