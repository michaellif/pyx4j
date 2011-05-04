/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.Date;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.decorations.ViewLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.client.ptapp.ui.components.BuildingPicture;
import com.propertyvista.portal.client.ptapp.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentViewForm.class);

    private ApartmentViewPresenter presenter;

    private ApartmentUnit selectedUnit = EntityFactory.create(ApartmentUnit.class);

    public ApartmentViewForm() {
        super(UnitSelection.class, new VistaEditorsComponentFactory());
    }

    public void setPresenter(ApartmentViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        // Form first table header: 
        HorizontalPanel header = new HorizontalPanel();

        DecorationData decorData = new DecorationData(0, 8.1);
        decorData.hideInfoHolder = true;
        decorData.showMandatory = DecorationData.ShowMandatory.None;
        header.add(new VistaWidgetDecorator(inject(proto().selectionCriteria().availableFrom()), decorData));

        decorData = new DecorationData(0, 8.1);
        decorData.hideInfoHolder = true;
        decorData.showMandatory = DecorationData.ShowMandatory.None;
        header.add(new VistaWidgetDecorator(inject(proto().selectionCriteria().availableTo()), decorData));

        Button changeBtn = new Button(i18n.tr("Change"));
        changeBtn.ensureDebugId(VistaFormsDebugId.Available_Units_Change.debugId());
        changeBtn.getElement().getStyle().setMarginTop(0, Unit.PX);
        changeBtn.getElement().getStyle().setMarginLeft(1, Unit.EM);
        changeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // This is owned entity, it can't be serialised by itself because all values are in owner. So we do clone
                // TODO fix the RPC services to know about this case.
                presenter.selectByDates((UnitSelectionCriteria) getValue().selectionCriteria().cloneEntity());
            }
        });
        header.add(changeBtn);

        header.getElement().getStyle().setMarginTop(7, Unit.PX);
        header.getElement().getStyle().setMarginLeft(130, Unit.PX);

        main.add(new ViewHeaderDecorator(i18n.tr("Available Units"), header, "100%"));

        // units table:
        main.add(inject(proto().availableUnits().units(), new ApartmentUnitsTable(new ValueChangeHandler<ApartmentUnit>() {

            @Override
            public void onValueChange(ValueChangeEvent<ApartmentUnit> event) {
                selectedUnit = event.getValue();
                getValue().selectedUnitId().set(selectedUnit.id());
                getValue().selectedLeaseTerm().setValue(null);
                CEditableComponent<Date, ?> rentStart = get(proto().rentStart());
                if ((rentStart.getValue() == null) || (!rentStart.isVisited())) {
                    rentStart.setValue(selectedUnit.avalableForRent().getValue());
                } else {
                    rentStart.revalidate();
                }
            }
        }, new ValueChangeHandler<Integer>() {

            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                getValue().selectedLeaseTerm().setValue(event.getValue());
            }
        })));

        // start date:
        main.add(new ViewLineSeparator(0, Unit.PCT, 1, Unit.EM, 1, Unit.EM));

        DecorationData captionDecoration = new DecorationData(16d, 8.2);
        captionDecoration.labelStyleName = ViewHeaderDecorator.DEFAULT_STYLE_PREFIX + ViewHeaderDecorator.StyleSuffix.Caption.name();
        main.add(new VistaWidgetDecorator(inject(proto().rentStart()), captionDecoration));

        // last step - add building picture on the right:
        HorizontalPanel content = new HorizontalPanel();
        main.setWidth("700px");
        content.add(main);
        content.add(new BuildingPicture());
        return content;
    }

    @Override
    public void populate(UnitSelection value) {
        super.populate(value);
        ApartmentUnitsTable unitsTable = ((ApartmentUnitsTable) getRaw(proto().availableUnits().units()));
        unitsTable.populateFloorplan(value.availableUnits());
        selectedUnit.set(unitsTable.setSelectedUnit(getValue().selectedUnitId().getValue(), getValue().selectedLeaseTerm().getValue()));
    }

    void setAvailableUnits(AvailableUnitsByFloorplan availableUnits) {
        ApartmentUnitsTable unitsTable = ((ApartmentUnitsTable) getRaw(proto().availableUnits().units()));
        unitsTable.populateFloorplan(availableUnits);
        unitsTable.populate(availableUnits.units());
        selectedUnit.set(unitsTable.setSelectedUnit(getValue().selectedUnitId().getValue(), getValue().selectedLeaseTerm().getValue()));
        if (selectedUnit.isNull()) {
            getValue().selectedUnitId().setValue(null);
        }
    }

    @Override
    public void addValidations() {
        this.addValueValidator(new EditableValueValidator<UnitSelection>() {

            @Override
            public boolean isValid(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return !value.selectedUnitId().isNull();
            }

            @Override
            public String getValidationMessage(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return i18n.tr("Please select the Unit");
            }
        });

        this.addValueValidator(new EditableValueValidator<UnitSelection>() {

            @Override
            public boolean isValid(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return !value.selectedLeaseTerm().isNull();
            }

            @Override
            public String getValidationMessage(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return i18n.tr("Please select the Lease Terms");
            }
        });

        this.get(proto().rentStart()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                Date avalableForRent = selectedUnit.avalableForRent().getValue();
                if ((avalableForRent == null) || (value == null)) {
                    return true;
                } else {
                    return (value.compareTo(avalableForRent) >= 0);
                }
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("Start Rent Date for this unit can not be before {0,date,medium}", selectedUnit.avalableForRent().getValue());
            }
        });

        this.get(proto().rentStart()).addValueValidator(new EditableValueValidator<Date>() {

            @SuppressWarnings("deprecation")
            private Date getLastAvailableDay(Date avalableForRent) {
                if (avalableForRent == null) {
                    return null;
                }
                Date date = new Date(avalableForRent.getTime());
                int availableDays = 4;
                date.setDate(date.getDate() + availableDays);
                return date;
            }

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                Date avalableForRent = selectedUnit.avalableForRent().getValue();
                if ((avalableForRent == null) || (value == null)) {
                    return true;
                } else {
                    return TimeUtils.isWithinRange(value, avalableForRent, getLastAvailableDay(avalableForRent));
                }
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("Start Rent Date for this unit can not be later than {0,date,medium}", getLastAvailableDay(selectedUnit.avalableForRent()
                        .getValue()));
            }
        });
    }
}
