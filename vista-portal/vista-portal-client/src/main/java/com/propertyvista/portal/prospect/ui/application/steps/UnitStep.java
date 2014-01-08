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

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.UnitTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class UnitStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(UnitStep.class);

    private final CComboBox<Integer> bedroomSelector = new CComboBox<Integer>();

    private final CComboBox<Integer> bathroomSelector = new CComboBox<Integer>();

    private final CEntityLabel<UnitTO> selectedUnit = new CEntityLabel<UnitTO>();

    private final AvailableUnitsFolder availableUnitsFolder = new AvailableUnitsFolder();

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

    public UnitStep() {
        Integer[] opt = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        bedroomSelector.setOptions(Arrays.asList(opt));
        bathroomSelector.setOptions(Arrays.asList(opt));
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Unit Selection"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().building(), new CEntityLabel<Building>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().moveIn()), 120).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().bedrooms(), bedroomSelector)).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().bathrooms(), bathroomSelector)).build());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().selectedUnit(), selectedUnit)).build());
        panel.setWidget(++row, 0, inject(proto().unitSelection().availableUnits(), availableUnitsFolder));

//        panel.setBR(++row, 0, 1);

        panel.setWidget(++row, 0, updateButton);
        panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT);

        return panel;
    }

    @Override
    public void onValueSet() {
        super.onValueSet();

        setEditableState(getValue().unitSelection().selectedUnit().isNull());
    }

    @Override
    public void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);

        if (selectedStep.equals(this)) {
            setEditableState(selectedUnit.isValueEmpty());
        }
    }

    void setEditableState(Boolean editable) {
        get(proto().unitSelection().moveIn()).setEditable(editable);
        get(proto().unitSelection().building()).setEditable(editable);
        get(proto().unitSelection().bedrooms()).setEditable(editable);
        get(proto().unitSelection().bathrooms()).setEditable(editable);

        get(proto().unitSelection().selectedUnit()).setVisible(!editable);
        get(proto().unitSelection().availableUnits()).setVisible(editable);

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

        bedroomSelector.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> event) {
                updateAvailableUnits(null);
            }
        });

        bathroomSelector.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Integer> event) {
                updateAvailableUnits(null);
            }
        });

        selectedUnit.addValueChangeHandler(new ValueChangeHandler<UnitTO>() {
            @Override
            public void onValueChange(final ValueChangeEvent<UnitTO> event) {
                updateUnitOptions(event.getValue());
            }
        });
    }

    private void updateAvailableUnits(LogicalDate moveIn) {
        if (moveIn == null) {
            moveIn = get(proto().unitSelection().moveIn()).getValue();
        }

        getWizard().getPresenter().getAvailableUnits(new DefaultAsyncCallback<Vector<UnitTO>>() {
            @Override
            public void onSuccess(Vector<UnitTO> result) {
                setAvailableUnits(result);
            }
        }, get(proto().unitSelection().bedrooms()).getValue(), get(proto().unitSelection().bathrooms()).getValue(), moveIn);
    }

    private void updateUnitOptions(UnitTO unit) {
        if (unit != null) {
            getWizard().getPresenter().getAvailableUnitOptions(new DefaultAsyncCallback<UnitOptionsSelectionDTO>() {
                @Override
                public void onSuccess(UnitOptionsSelectionDTO result) {
                    ((OptionsStep) getWizard().getStep(OptionsStep.class)).setStepValue(result);
                }
            }, unit);
        }
    }

    private void setAvailableUnits(Collection<UnitTO> result) {
        getValue().unitSelection().availableUnits().clear();
        getValue().unitSelection().availableUnits().addAll(result);

        availableUnitsFolder.setValue(getValue().unitSelection().availableUnits());
    }

    private class AvailableUnitsFolder extends PortalBoxFolder<UnitTO> {

        public AvailableUnitsFolder() {
            super(UnitTO.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof UnitTO) {
                return new AvailableUnitForm();
            } else {
                return super.create(member);
            }
        }

        class AvailableUnitForm extends CEntityForm<UnitTO> {

            public AvailableUnitForm() {
                super(UnitTO.class);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

                int row = -1;
                mainPanel.setWidget(++row, 0, inject(proto().display()));
                mainPanel.setWidget(++row, 0, new Button(i18n.tr("Select"), new Command() {
                    @Override
                    public void execute() {
                        selectedUnit.setValue(getValue());
                    }
                }));

                return mainPanel;
            }
        }
    }
}
