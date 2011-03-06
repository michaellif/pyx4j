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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.widgets.client.Button;

public class ApartmentViewForm extends BaseEntityForm<UnitSelection> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentViewForm.class);

    private ApartmentViewPresenter presenter;

    public ApartmentViewForm() {
        super(UnitSelection.class);
    }

    public void setPresenter(ApartmentViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        HorizontalPanel content = new HorizontalPanel();
        FlowPanel main = new FlowPanel();
        content.add(main);

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
        changeBtn.getElement().getStyle().setMargin(5, Unit.PX);
        changeBtn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // This is owned entity, it can't be serialised by itself because all values are in owner. So we do clone
                // TODO fix the RPC services to know about this case.
                presenter.selectByDates((UnitSelectionCriteria) getValue().selectionCriteria().cloneEntity());
            }
        });
        header.add(changeBtn);

        Widget w = new ViewHeaderDecorator(i18n.tr("Available Units"), header);
        main.add(w);

        // units table:
        main.add(inject(proto().availableUnits().units(), new ApartmentUnitsTable(new ValueChangeHandler<ApptUnit>() {

            @Override
            public void onValueChange(ValueChangeEvent<ApptUnit> event) {
                if (!getValue().selectedUnit().equals(event.getValue())) {
                    getValue().selectedUnit().set(event.getValue());
                    getValue().markerRent().set(null);
                    CEditableComponent<Date, ?> rentStart = get(proto().rentStart());
                    if (rentStart.getValue() == null) {
                        rentStart.setValue(getValue().selectedUnit().avalableForRent().getValue());
                    }
                }

            }
        }, new ValueChangeHandler<MarketRent>() {

            @Override
            public void onValueChange(ValueChangeEvent<MarketRent> event) {
                getValue().markerRent().set(event.getValue());
            }
        })));

        // start date:
        main.add(new ViewLineSeparator(0, Unit.PCT, 1, Unit.EM, 1, Unit.EM));

        DecorationData captionDecoration = new DecorationData(150, 100);
        captionDecoration.labelStyle = ViewHeaderDecorator.DEFAULT_STYLE_PREFIX + ViewHeaderDecorator.StyleSuffix.Caption.name();
        main.add(new VistaWidgetDecorator(inject(proto().rentStart()), captionDecoration));

        addValidations();

        main.setWidth("700px");

        FlowPanel buildingView = new FlowPanel();
        buildingView.add(new Image(SiteImages.INSTANCE.building()));
        buildingView.add(new HTML("<b>ADDRESS: </b><br/>2060-2070 Prospect Street, Burlington"));
        buildingView.getElement().getStyle().setMarginLeft(20, Unit.PX);

        content.add(buildingView);

        return content;
    }

    @Override
    public void populate(UnitSelection value) {
        super.populate(value);
        ((ApartmentUnitsTable) getRaw(value.availableUnits().units())).populate(getValue());
    }

    private void addValidations() {
        this.addValueValidator(new EditableValueValidator<UnitSelection>() {

            @Override
            public boolean isValid(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return !value.selectedUnit().isNull();
            }

            @Override
            public String getValidationMessage(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return i18n.tr("Please select the Unit");
            }
        });

        this.addValueValidator(new EditableValueValidator<UnitSelection>() {

            @Override
            public boolean isValid(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return !value.markerRent().isNull();
            }

            @Override
            public String getValidationMessage(CEditableComponent<UnitSelection, ?> component, UnitSelection value) {
                return i18n.tr("Please select the Lease Terms");
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
                Date avalableForRent = getValue().selectedUnit().avalableForRent().getValue();
                if ((avalableForRent == null) || (value == null)) {
                    return true;
                } else {
                    return TimeUtils.isWithinRange(value, avalableForRent, getLastAvailableDay(avalableForRent));
                }
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("Start Rent Date for this unit can not be later than {0,date,medium}", getLastAvailableDay(getValue().selectedUnit()
                        .avalableForRent().getValue()));
            }
        });

    }
}
