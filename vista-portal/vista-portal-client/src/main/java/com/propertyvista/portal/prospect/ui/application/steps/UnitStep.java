/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.util.Vector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class UnitStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(UnitStep.class);

    private final CComboBox<Floorplan> floorplanSelector = new CComboBox<Floorplan>() {
        @Override
        public String getItemName(Floorplan o) {
            return (o != null ? o.getStringView() : "");
        };
    };

    private final CComboBox<AptUnit> unitSelector = new CComboBox<AptUnit>() {
        @Override
        public String getItemName(AptUnit o) {
            return (o != null ? o.getStringView() : "");
        };
    };

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Unit Selection"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().moveIn())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().building(), new CEntityLabel<Building>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().floorplan(), floorplanSelector)).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().unit(), unitSelector)).build());

        return panel;
    }

    @Override
    public void onValueSet() {
        super.onValueSet();

        floorplanSelector.setOptions(getValue().unitSelection().availableFloorplans());
        unitSelector.setOptions(getValue().unitSelection().availableUnits());
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().unitSelection().moveIn()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                Floorplan floorplanSelected = get(proto().unitSelection().floorplan()).getValue();
                if (floorplanSelected != null) {
                    getWizard().getPresenter().getAvailableUnits(new DefaultAsyncCallback<Vector<AptUnit>>() {
                        @Override
                        public void onSuccess(Vector<AptUnit> result) {
                            unitSelector.reset();
                            unitSelector.setOptions(result);
                        }
                    }, floorplanSelected, event.getValue());
                }
            }
        });

        floorplanSelector.addValueChangeHandler(new ValueChangeHandler<Floorplan>() {
            @Override
            public void onValueChange(ValueChangeEvent<Floorplan> event) {
                getWizard().getPresenter().getAvailableUnits(new DefaultAsyncCallback<Vector<AptUnit>>() {
                    @Override
                    public void onSuccess(Vector<AptUnit> result) {
                        unitSelector.reset();
                        unitSelector.setOptions(result);
                    }
                }, event.getValue(), get(proto().unitSelection().moveIn()).getValue());
            }
        });
    }
}
