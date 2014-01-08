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

import java.util.Collection;
import java.util.Vector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
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

    private final Button updateButton = new Button(i18n.tr("Change Selection"), new Command() {
        @Override
        public void execute() {
            MessageDialog.confirm(i18n.tr("Warning"), i18n.tr("You will lost already selected Unit Options. Do you really want to change current selection?"),
                    new Command() {
                        @Override
                        public void execute() {
                            setEditableState(true);
                        }
                    });
        }
    });

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Unit Selection"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().moveIn()), 120).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().building(), new CEntityLabel<Building>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().floorplan(), floorplanSelector)).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().unit(), unitSelector)).build());

//        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, updateButton);
        panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT);

        return panel;
    }

    @Override
    public void onValueSet() {
        super.onValueSet();

        floorplanSelector.setOptions(getValue().unitSelection().availableFloorplans());
        unitSelector.setOptions(getValue().unitSelection().availableUnits());

        setEditableState(unitSelector.isValueEmpty());
    }

    @Override
    public void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);

        if (selectedStep.equals(this)) {
            setEditableState(unitSelector.isValueEmpty());
        }
    }

    void setEditableState(Boolean editable) {
        get(proto().unitSelection().moveIn()).setEditable(editable);
        get(proto().unitSelection().building()).setEditable(editable);
        get(proto().unitSelection().floorplan()).setEditable(editable);
        get(proto().unitSelection().unit()).setEditable(editable);

        updateButton.setVisible(!editable);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().unitSelection().moveIn()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(final ValueChangeEvent<LogicalDate> event) {
                updateAvailableUnits(event.getValue());
            }
        });

        floorplanSelector.addValueChangeHandler(new ValueChangeHandler<Floorplan>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Floorplan> event) {
                updateAvailableUnits(null);
            }
        });

        unitSelector.addValueChangeHandler(new ValueChangeHandler<AptUnit>() {
            @Override
            public void onValueChange(final ValueChangeEvent<AptUnit> event) {
                updateUnitOptions(event.getValue());
            }
        });
    }

    private void updateAvailableUnits(LogicalDate moveIn) {
        if (moveIn == null) {
            moveIn = get(proto().unitSelection().moveIn()).getValue();
        }

        if (!get(proto().unitSelection().floorplan()).isValueEmpty()) {
            getWizard().getPresenter().getAvailableUnits(new DefaultAsyncCallback<Vector<AptUnit>>() {
                @Override
                public void onSuccess(Vector<AptUnit> result) {
                    setAvailableUnits(result);
                }
            }, get(proto().unitSelection().floorplan()).getValue(), moveIn);
        }
    }

    private void updateUnitOptions(AptUnit unit) {
        if (unit != null) {
            getWizard().getPresenter().getAvailableUnitOptions(new DefaultAsyncCallback<UnitOptionsSelectionDTO>() {
                @Override
                public void onSuccess(UnitOptionsSelectionDTO result) {
                    ((OptionsStep) getWizard().getStep(OptionsStep.class)).setStepValue(result);
                }
            }, unit);
        }
    }

    private void setAvailableUnits(Collection<AptUnit> result) {
        unitSelector.reset();
        unitSelector.setOptions(result);

        if (result.isEmpty()) {
            MessageDialog.warn(i18n.tr("Sorry"),
                    i18n.tr("There are no available from {0} units for selected floorplan!", get(proto().unitSelection().moveIn()).getValue()));
        }
    }
}
