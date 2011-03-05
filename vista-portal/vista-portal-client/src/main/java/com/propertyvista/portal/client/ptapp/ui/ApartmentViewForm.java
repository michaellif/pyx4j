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

import static com.pyx4j.commons.HtmlUtils.h3;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentViewForm.class);

    private AvailableUnitsTable availableUnitsTable;

    private ApartmentViewPresenter presenter;

    public static String DEFAULT_STYLE_PREFIX = "ApartmentViewForm";

    public static enum StyleSuffix implements IStyleSuffix {
        UnitListHeader, SelectedUnit, unitRowPanel, unitDetailPanel
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    public ApartmentViewForm() {
        super(UnitSelection.class);
    }

    public ApartmentViewPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(ApartmentViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        // Form first table header: 
        FlowPanel header = new FlowPanel();
        DecorationData decorData = new DecorationData();
        decorData.labelWidth = 0;
        decorData.componentWidth = 7.2;
        decorData.hideInfoHolder = true;
        decorData.showMandatory = DecorationData.ShowMandatory.None;
        VistaWidgetDecorator dateFrom = new VistaWidgetDecorator(inject(proto().selectionCriteria().availableFrom()), decorData);
        dateFrom.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dateFrom.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        header.add(dateFrom);

        decorData = new DecorationData();
        decorData.labelWidth = 0;
        decorData.componentWidth = 7.2;
        decorData.hideInfoHolder = true;
        decorData.showMandatory = DecorationData.ShowMandatory.None;
        VistaWidgetDecorator dateTo = new VistaWidgetDecorator(inject(proto().selectionCriteria().availableTo()), decorData);
        dateTo.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        dateTo.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        header.add(dateTo);

        Button changeBtn = new Button(i18n.tr("Change"));
        changeBtn.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        changeBtn.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
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
        main.add(new HTML());
        availableUnitsTable = new AvailableUnitsTable();
        main.add(availableUnitsTable);

        // start date:
        main.add(new ViewLineSeparator(0, Unit.PCT, 1, Unit.EM, 1, Unit.EM));

        main.add(new VistaWidgetDecorator(inject(proto().rentStart()), 0, 7.2));

        addValidations();

        return main;
    }

    private void addValidations() {
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

    @Override
    public void populate(UnitSelection entity) {
        super.populate(entity);
        availableUnitsTable.populate(entity);
    }

    /*
     * Here is the workaround of the problem: our ViewHeaderDecorator has padding 1em on
     * both ends in the CSS style, so in order to set all other internal widgets intended
     * to be whole-width-wide by means of percentage width it's necessary to add those
     * padding values!
     */
    private Widget upperLevelElementElignment(Widget e) {
        e.getElement().getStyle().setPaddingLeft(1, Unit.EM);
        e.getElement().getStyle().setPaddingRight(1, Unit.EM);
        e.setWidth("70%");
        return e;
    }

    private Widget innerLevelElementElignment(Widget e) {
        upperLevelElementElignment(e);
        e.setWidth("100%");
        return e;
    }

    class AvailableUnitsTable extends FlowPanel {

        private final FlowPanel content;

        private final Map<String, String> tableLayout = new LinkedHashMap<String, String>();

        public AvailableUnitsTable() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            //            upperLevelElementElignment(this);
            setWidth("700px");

            tableLayout.put("Plan", "10%");
            tableLayout.put("Type", "20%");
            tableLayout.put("Rent", "10%");
            tableLayout.put("Deposit", "10%");
            tableLayout.put("Beds", "10%");
            tableLayout.put("Baths", "10%");
            tableLayout.put("Sq F", "10%");
            tableLayout.put("Available", "20%");

            FlowPanel header = new FlowPanel();
            header.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.UnitListHeader);
            header.getElement().getStyle().setMarginBottom(1, Unit.EM);
            header.setHeight("2.3em");
            innerLevelElementElignment(header);

            // fill header:
            for (Entry<String, String> e : tableLayout.entrySet()) {
                HTML label = new HTML(e.getKey());
                label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                label.asWidget().getElement().getStyle().setProperty("lineHeight", "2em");
                label.setWidth(e.getValue());
                header.add(label);
            }

            add(header);

            content = new FlowPanel();
            add(content);
        }

        protected void populate(UnitSelection units) {
            ApartmentViewForm.super.populate(units);
            content.clear();

            populateFloorplan(units.availableUnits());
            populateUnits(units.availableUnits());
        }

        private void populateFloorplan(AvailableUnitsByFloorplan availableUnits) {
            FlowPanel floorplan = new FlowPanel();
            floorplan.getElement().getStyle().setPaddingLeft(1, Unit.EM);

            addCell("Plan", "&nbsp;", floorplan);
            addCell("Type", availableUnits.floorplan().name().getStringView(), floorplan);
            addCell("Rent", "From <br />" + "$" + minRentValue(availableUnits.units()), floorplan);
            addCell("Deposit", "&nbsp;", floorplan);
            addCell("Beds", "&nbsp;", floorplan);
            addCell("Baths", "&nbsp;", floorplan);
            addCell("Sq F", availableUnits.floorplan().area().getStringView(), floorplan);
            addCell("Available", "&nbsp;", floorplan);

            content.add(innerLevelElementElignment(floorplan));
            content.add(new ViewLineSeparator(100, Unit.PCT, 1, Unit.EM, 0.5, Unit.EM));
        }

        private void populateUnits(AvailableUnitsByFloorplan availableUnits) {

            for (final com.propertyvista.portal.domain.ApptUnit unit : availableUnits.units()) {
                final FlowPanel unitRowPanel = new FlowPanel();
                unitRowPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.unitRowPanel);
                unitRowPanel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                unitRowPanel.getElement().getStyle().setCursor(Cursor.POINTER);
                unitRowPanel.addDomHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        getValue().selectedUnit().set(unit); // update user-selected unit...
                        selectUnitRow(unitRowPanel);
                    }
                }, ClickEvent.getType());

                unitRowPanel.addDomHandler(new MouseOverHandler() {

                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        if (!unitRowPanel.getStyleName().contains(StyleDependent.selected.name())) {
                            unitRowPanel.addStyleDependentName(StyleDependent.hover.name());
                        }
                    }
                }, MouseOverEvent.getType());

                unitRowPanel.addDomHandler(new MouseOutHandler() {

                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        unitRowPanel.removeStyleDependentName(StyleDependent.hover.name());
                    }
                }, MouseOutEvent.getType());

                addCell("Plan", "&nbsp;", unitRowPanel);
                addCell("Type", unit.unitType().getStringView(), unitRowPanel);
                addCell("Rent", "$" + Double.toString(minRentValue(unit)), unitRowPanel);
                addCell("Deposit", "$" + unit.requiredDeposit().getStringView(), unitRowPanel);
                addCell("Beds", unit.bedrooms().getStringView(), unitRowPanel);
                addCell("Baths", unit.bathrooms().getStringView(), unitRowPanel);
                addCell("Sq F", unit.area().getStringView(), unitRowPanel);
                addCell("Available", unit.avalableForRent().getStringView(), unitRowPanel);

                content.add(innerLevelElementElignment(unitRowPanel));

                populateUnitDetail(unit);

                if (unit.equals(getValue().selectedUnit())) {
                    selectUnitRow(unitRowPanel);
                }
            }
        }

        private void addCell(String cellName, String cellContent, FlowPanel container) {
            HTML label = new HTML(cellContent);
            label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            label.setWidth(tableLayout.get(cellName));
            container.add(label);
        }

        private void populateUnitDetail(com.propertyvista.portal.domain.ApptUnit unit) {
            FlowPanel unitDetailPanel = new FlowPanel();
            unitDetailPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.unitDetailPanel);
            unitDetailPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);

            FlowPanel infoPanel = new FlowPanel();
            infoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            infoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            infoPanel.add(new HTML(h3(i18n.tr("Info"))));
            infoPanel.add(new HTML(unit.infoDetails().getStringView()));
            infoPanel.setWidth("33%");
            unitDetailPanel.add(infoPanel);

            FlowPanel amenitiesPanel = new FlowPanel();
            amenitiesPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            amenitiesPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            amenitiesPanel.add(new HTML(h3(i18n.tr("Amenities/Utilities:"))));
            amenitiesPanel.add(new HTML(unit.amenities().getStringView()));
            amenitiesPanel.add(new HTML(unit.utilities().getStringView()));
            amenitiesPanel.setWidth("33%");
            unitDetailPanel.add(amenitiesPanel);

            FlowPanel concessionPanel = new FlowPanel();
            concessionPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            concessionPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            concessionPanel.add(new HTML(h3(i18n.tr("Concession"))));
            concessionPanel.add(new HTML(unit.concessions().getStringView()));
            concessionPanel.setWidth("33%");
            unitDetailPanel.add(concessionPanel);

            Widget sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
            unitDetailPanel.add(sp);

            FlowPanel addonsPanel = new FlowPanel();
            addonsPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            addonsPanel.add(new HTML(h3(i18n.tr("Available add-ons"))));
            addonsPanel.setWidth("33%");
            unitDetailPanel.add(addonsPanel);
            addonsPanel.add(new HTML(unit.addOns().getStringView()));

            sp = new ViewLineSeparator(98, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPaddingLeft(0, Unit.EM);
            unitDetailPanel.add(sp);

            // lease term:
            unitDetailPanel.add(new HTML());
            unitDetailPanel.add(new HTML(h3(i18n.tr("Lease Terms"))));
            FlowPanel leaseTermsPanel = new FlowPanel();

            String groupName = "TermVariants" + unit.hashCode();
            RadioButton term = null; // fill the variants:
            for (final MarketRent mr : unit.marketRent()) {
                term = new RadioButton(groupName, mr.leaseTerm().getStringView() + "&nbsp;&nbsp;&nbsp;&nbsp; month &nbsp;&nbsp;&nbsp;&nbsp; $"
                        + mr.rent().amount().getValue(), true);

                // set preselected term for selected unit:
                if (unit.equals(getValue().selectedUnit())) {
                    term.setValue(mr.leaseTerm().equals(getValue().markerRent().leaseTerm()));
                }

                term.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        getValue().markerRent().setValue(mr.getValue());
                    }
                });

                term.getElement().getStyle().setDisplay(Display.BLOCK);
                leaseTermsPanel.add(term);
            }

            // set last (longest) term for all other units:
            if (term != null && !unit.equals(getValue().selectedUnit())) {
                term.setValue(true);
            }

            unitDetailPanel.add(leaseTermsPanel);

            unitDetailPanel.getElement().getStyle().setPadding(1, Unit.EM);
            unitDetailPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
            unitDetailPanel.getElement().getStyle().setBackgroundColor("white");
            unitDetailPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            unitDetailPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
            unitDetailPanel.getElement().getStyle().setBorderColor("black");
            unitDetailPanel.getElement().getStyle().setProperty("borderTop", "none");
            unitDetailPanel.setVisible(false);
            content.add(innerLevelElementElignment(unitDetailPanel));
        }

        public void selectUnitRow(FlowPanel unitRowPanel) {

            // tweak selected unit data view:
            for (Widget w : content) {
                if (w.getStyleName().contains(DEFAULT_STYLE_PREFIX + StyleSuffix.unitRowPanel)) {
                    w.removeStyleDependentName(StyleDependent.selected.name());
                }
                // hide all detail panels:
                if (w.getStyleName().contains(DEFAULT_STYLE_PREFIX + StyleSuffix.unitDetailPanel)) {
                    w.removeStyleDependentName(StyleDependent.selected.name());
                    w.setVisible(false);
                }
            }

            // show current selected row with details:
            unitRowPanel.addStyleDependentName(StyleDependent.selected.name());
            Widget unitDetailPanel = content.getWidget(content.getWidgetIndex(unitRowPanel) + 1);
            unitDetailPanel.addStyleDependentName(StyleDependent.selected.name());
            unitDetailPanel.setVisible(true);
        }

        private double minRentValue(com.propertyvista.portal.domain.ApptUnit unit) {
            double rent = Double.MAX_VALUE;
            for (MarketRent mr : unit.marketRent())
                rent = Math.min(rent, mr.rent().amount().getValue());
            return (rent != Double.MAX_VALUE ? rent : 0);
        }

        private double minRentValue(IList<com.propertyvista.portal.domain.ApptUnit> units) {
            double rent = Double.MAX_VALUE;
            for (com.propertyvista.portal.domain.ApptUnit u : units)
                rent = Math.min(rent, minRentValue(u));
            return (rent != Double.MAX_VALUE ? rent : 0);
        }
    }
}
