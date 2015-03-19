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
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.util.EnumSet;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.BaseFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.ImageButton;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BathroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.BedroomNumber;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.UnitTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.util.CBuildingLabel;

public class UnitStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(UnitStep.class);

    private final CComboBox<BedroomNumber> bedroomSelector = new CComboBox<>(true);

    private final CComboBox<BathroomNumber> bathroomSelector = new CComboBox<>(true);

    private final CLabel<UnitTO> selectedUnit = new CLabel<UnitTO>() {

        @Override
        public void setFormatter(IFormatter<UnitTO, String> formatter) {
            super.setFormatter(new IFormatter<UnitTO, String>() {
                @Override
                public String format(UnitTO value) {
                    if (value != null && !value.isEmpty()) {
                        return value.getStringView();
                    } else {
                        return i18n.tr("No unit is selected yet.");
                    }
                }
            });
        };

        @Override
        public boolean isValidatable() {
            return true;
        }
    };

    private final AvailableUnitsFolder availableUnitsFolder = new AvailableUnitsFolder();

    private final AvailableUnitsFolder potentialUnitsFolder = new AvailableUnitsFolder();

    private Widget availableUnitsHeader, potentialUnitsHeader;

    private final Button updateButton = new Button(i18n.tr("Change Unit Selection"), new Command() {
        @Override
        public void execute() {
            MessageDialog.confirm(i18n.tr("Warning"), i18n.tr("You will lose already selected Unit Options. Do you really want to change current selection?"),
                    new Command() {
                        @Override
                        public void execute() {
                            setEditableState(true);
                        }
                    });
        }
    });

    public UnitStep() {
        super(OnlineApplicationWizardStepMeta.Unit);
        bedroomSelector.setOptions(EnumSet.allOf(BedroomNumber.class));
        bathroomSelector.setOptions(EnumSet.allOf(BathroomNumber.class));
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());

        formPanel.append(Location.Left, proto().unitSelection().building(), new CBuildingLabel()).decorate();
        formPanel.append(Location.Left, proto().unitSelection().selectedUnit(), selectedUnit).decorate();
        formPanel.append(Location.Left, proto().unitSelection().moveIn()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().unitSelection().bedrooms(), bedroomSelector).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().unitSelection().bathrooms(), bathroomSelector).decorate().componentWidth(120);

        availableUnitsHeader = formPanel.h3(i18n.tr("Exact match:"));

        formPanel.append(Location.Left, proto().unitSelection().availableUnits(), availableUnitsFolder);

        potentialUnitsHeader = formPanel.h3(i18n.tr("Partial match:"));
        formPanel.append(Location.Left, proto().unitSelection().potentialUnits(), potentialUnitsFolder);

        formPanel.br();

        formPanel.append(Location.Left, updateButton);

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getValue().unitSelection().bedrooms().isNull()) {
            bedroomSelector.setValue(BedroomNumber.Any);
        }
        if (getValue().unitSelection().bathrooms().isNull()) {
            bathroomSelector.setValue(BathroomNumber.Any);
        }

        setEditableState(getValue().unitSelection().selectedUnit().isNull());
    }

    @Override
    public void onStepVizible(boolean visible) {
        super.onStepVizible(visible);
        if (visible) {
            setEditableState(selectedUnit.isValueEmpty());
        }
    }

    void setEditableState(Boolean editable) {
        get(proto().unitSelection().moveIn()).setEditable(editable);
        get(proto().unitSelection().bedrooms()).setEditable(editable);
        get(proto().unitSelection().bathrooms()).setEditable(editable);

        availableUnitsHeader.setVisible(editable);
        availableUnitsFolder.setVisible(editable);
        availableUnitsFolder.setNoDataNotificationWidget(null);

        potentialUnitsHeader.setVisible(editable);
        potentialUnitsFolder.setVisible(editable);
        potentialUnitsFolder.setNoDataNotificationWidget(null);

        updateButton.setVisible(!editable);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        selectedUnit.addComponentValidator(new AbstractComponentValidator<UnitTO>() {
            @Override
            public AbstractValidationError isValid() {
                return (selectedUnit.isVisited() && selectedUnit.isValueEmpty() ? new BasicValidationError(getCComponent(), i18n
                        .tr("Unit selection is required")) : null);
            }
        });

        selectedUnit.addValueChangeHandler(new ValueChangeHandler<UnitTO>() {
            @Override
            public void onValueChange(final ValueChangeEvent<UnitTO> event) {
                updateUnitOptions(event.getValue());
            }
        });

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
    }

    private void updateAvailableUnits() {
        UnitSelectionDTO current = getValue().unitSelection().<UnitSelectionDTO> duplicate();
        current.availableUnits().clear();
        current.potentialUnits().clear();

        if (!current.moveIn().isNull()) {
            getWizard().getPresenter().getAvailableUnits(new DefaultAsyncCallback<UnitSelectionDTO>() {
                @Override
                public void onSuccess(UnitSelectionDTO result) {
                    setAvailableUnits(result);
                }
            }, current);
        }
    }

    private void updateUnitOptions(UnitTO unit) {
        if (unit != null) {
            getWizard().getPresenter().getAvailableUnitOptions(new DefaultAsyncCallback<UnitOptionsSelectionDTO>() {
                @Override
                public void onSuccess(UnitOptionsSelectionDTO result) {
                    ((OptionsStep) getWizard().getStep(OptionsStep.class)).setStepValue(result);

                    // update summary gadgets:
                    getValue().unit().set(result.unit());
                    getValue().leaseChargesData().clear();
                    getValue().leaseChargesData().selectedService().set(result.selectedService());
                    ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(getWizard(),
                            ApplicationWizardStateChangeEvent.ChangeType.termChange));
                }
            }, unit, get(proto().unitSelection().moveIn()).getValue());
        }
    }

    private void setAvailableUnits(UnitSelectionDTO result) {
        getValue().unitSelection().availableUnits().clear();
        getValue().unitSelection().availableUnits().addAll(result.availableUnits());
        availableUnitsFolder.populate(getValue().unitSelection().availableUnits());
        availableUnitsFolder.setNoDataNotificationWidget(new Label(i18n.tr("Please refine your search. No matches have been found")));

        getValue().unitSelection().potentialUnits().clear();
        getValue().unitSelection().potentialUnits().addAll(result.potentialUnits());
        potentialUnitsFolder.populate(getValue().unitSelection().potentialUnits());
        potentialUnitsFolder.setNoDataNotificationWidget(new Label(i18n.tr("Please refine your search. No matches have been found")));
    }

    private class AvailableUnitsFolder extends CFolder<UnitTO> {

        public AvailableUnitsFolder() {
            super(UnitTO.class);
            setAddable(false);
        }

        @Override
        public ItemDecorator createItemDecorator() {
            return new ItemDecorator();
        }

        @Override
        protected IFolderDecorator<UnitTO> createFolderDecorator() {
            return new BoxFolderDecorator<UnitTO>(VistaImages.INSTANCE);
        }

        @Override
        protected CForm<UnitTO> createItemForm(IObject<?> member) {
            return new AvailableUnitForm();
        }

        class AvailableUnitForm extends CForm<UnitTO> {

            public AvailableUnitForm() {
                super(UnitTO.class);
            }

            @Override
            protected IsWidget createContent() {
                FlowPanel flowPanel = new FlowPanel();
                flowPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                flowPanel.setWidth("100%");

                CLabel<String> numberLabel = new CLabel<>(new IFormatter<String, String>() {

                    @Override
                    public String format(String value) {
                        return i18n.tr("Unit ") + value;
                    }
                });
                flowPanel.add(inject(proto().number(), numberLabel));
                numberLabel.asWidget().getElement().getStyle().setFloat(Float.LEFT);

                CLabel<String> priceLabel = new CLabel<>(new IFormatter<String, String>() {

                    @Override
                    public String format(String value) {
                        return i18n.tr("$") + value;
                    }
                });
                flowPanel.add(inject(proto().price(), priceLabel));
                priceLabel.asWidget().getElement().getStyle().setFloat(Float.RIGHT);

                flowPanel.add(inject(proto().floor()));
                flowPanel.add(inject(proto().bedrooms()));
                flowPanel.add(inject(proto().dens()));
                flowPanel.add(inject(proto().bathrooms()));
                flowPanel.add(inject(proto().available()));

                ImageButton selectButton = new ImageButton(PortalImages.INSTANCE.selectButton(), new Command() {

                    @Override
                    public void execute() {
                        selectedUnit.setValue(getValue());
                        setEditableState(false);
                    }
                });
                flowPanel.add(selectButton);
                selectButton.asWidget().getElement().getStyle().setFloat(Float.RIGHT);

                return flowPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().dens()).setVisible(!getValue().dens().isNull());
            }
        }
    }

    class ItemDecorator extends BaseFolderItemDecorator<UnitTO> {

        public ItemDecorator() {
            super(VistaImages.INSTANCE);
        }

        @Override
        public void setActionsState(boolean remove, boolean up, boolean down) {
        }

        @Override
        public void adoptItemActionsBar() {
        }

        @Override
        public void onSetDebugId(IDebugId parentDebugId) {
        }

        @Override
        public void setContent(IsWidget content) {
            add(content);
        }

    }

}
