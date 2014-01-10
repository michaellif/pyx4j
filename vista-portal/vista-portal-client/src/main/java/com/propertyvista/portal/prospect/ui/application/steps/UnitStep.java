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
import java.util.EnumSet;
import java.util.Vector;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.ItemActionsBar.ActionType;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BathroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BedroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.UnitTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class UnitStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(UnitStep.class);

    private final CComboBox<BedroomNumber> bedroomSelector = new CComboBox<BedroomNumber>(true);

    private final CComboBox<BathroomNumber> bathroomSelector = new CComboBox<BathroomNumber>(true);

    private final CEntityLabel<UnitTO> selectedUnit = new CEntityLabel<UnitTO>();

    private final AvailableUnitsFolder availableUnitsFolder = new AvailableUnitsFolder();

    private Widget availableUnitsHeader;

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
        bedroomSelector.setOptions(EnumSet.allOf(BedroomNumber.class));
        bathroomSelector.setOptions(EnumSet.allOf(BathroomNumber.class));
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Unit Selection"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().building(), new CEntityLabel<Building>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().moveIn()), 120).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().bedrooms(), bedroomSelector), 50).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().bathrooms(), bathroomSelector), 50).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unitSelection().selectedUnit(), selectedUnit)).build());

        panel.setH3(++row, 0, 1, i18n.tr("Available Units"));
        availableUnitsHeader = panel.getWidget(row, 0);

        panel.setWidget(++row, 0, inject(proto().unitSelection().availableUnits(), availableUnitsFolder));

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

        selectedUnit.setVisible(!editable);

        availableUnitsHeader.setVisible(editable);
        availableUnitsFolder.setVisible(editable);

        updateButton.setVisible(!editable);

        ((WizardDecorator<?>) getWizard().getDecorator()).getFooterPanel().asWidget().setVisible(!editable);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().unitSelection().moveIn()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(final ValueChangeEvent<LogicalDate> event) {
                updateAvailableUnits();
            }
        });

        bedroomSelector.addValueChangeHandler(new ValueChangeHandler<BedroomNumber>() {
            @Override
            public void onValueChange(final ValueChangeEvent<BedroomNumber> event) {
                updateAvailableUnits();
            }
        });

        bathroomSelector.addValueChangeHandler(new ValueChangeHandler<BathroomNumber>() {
            @Override
            public void onValueChange(final ValueChangeEvent<BathroomNumber> event) {
                updateAvailableUnits();
            }
        });

        selectedUnit.addValueChangeHandler(new ValueChangeHandler<UnitTO>() {
            @Override
            public void onValueChange(final ValueChangeEvent<UnitTO> event) {
                updateUnitOptions(event.getValue());
            }
        });
    }

    private void updateAvailableUnits() {
        UnitSelectionDTO current = getValue().unitSelection().<UnitSelectionDTO> duplicate();
        current.availableUnits().clear();
        current.potentialUnits().clear();

        getWizard().getPresenter().getAvailableUnits(new DefaultAsyncCallback<Vector<UnitTO>>() {
            @Override
            public void onSuccess(Vector<UnitTO> result) {
                setAvailableUnits(result);
            }
        }, current);
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

        availableUnitsFolder.populate(getValue().unitSelection().availableUnits());
    }

    private class AvailableUnitsFolder extends PortalBoxFolder<UnitTO> {

        public AvailableUnitsFolder() {
            super(UnitTO.class, false);
        }

        @Override
        public IFolderItemDecorator<UnitTO> createItemDecorator() {
            BoxFolderItemDecorator<UnitTO> decor = (BoxFolderItemDecorator<UnitTO>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        protected CEntityFolderItem<UnitTO> createItem(boolean first) {
            final CEntityFolderItem<UnitTO> item = super.createItem(first);
            item.addAction(ActionType.Cust1, i18n.tr("Select Unit"), PortalImages.INSTANCE.selectButton(), new Command() {
                @Override
                public void execute() {
                    selectedUnit.setValue(item.getValue());
                    setEditableState(false);
                }
            });
            return item;
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
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().number())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().floor())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().bedrooms())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().dens())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().bathrooms())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().available())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().price())).build());

                return mainPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().dens()).setVisible(!getValue().dens().isNull());
            }
        }
    }
}
