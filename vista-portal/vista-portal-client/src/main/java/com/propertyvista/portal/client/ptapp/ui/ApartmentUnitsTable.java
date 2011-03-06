/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.themes.VistaStyles;
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.UnitSelection;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CAbstractLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.IFormat;

public class ApartmentUnitsTable extends CEntityFolder<ApptUnit> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentUnitsTable.class);

    private final List<EntityFolderColumnDescriptor> columns;

    private final ReadOnlyComponentFactory factory = new ReadOnlyComponentFactory();

    private ApartmentUnitDetailsPanel unitDetailsPanelShown = null;

    private final ValueChangeHandler<ApptUnit> selectedUnitChangeHandler;

    private final ValueChangeHandler<MarketRent> selectedMarketRentChangeHandler;

    private MarketRent selectedmarketRent;

    private FlowPanel floorplanRawPanel;

    private final ApptUnit proto;

    public ApartmentUnitsTable(ValueChangeHandler<ApptUnit> selectedUnitChangeHandler, ValueChangeHandler<MarketRent> selectedMarketRentChangeHandler) {
        super();
        this.selectedUnitChangeHandler = selectedUnitChangeHandler;
        this.selectedMarketRentChangeHandler = selectedMarketRentChangeHandler;

        proto = EntityFactory.getEntityPrototype(ApptUnit.class);

        this.setWidth("700px");

        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto.floorplan().name(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.unitType(), "100px"));
        columns.add(new EntityFolderColumnDescriptor(proto.marketRent(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.requiredDeposit(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.bedrooms(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto.bathrooms(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto.area(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto.avalableForRent(), "140px"));
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass() == MarketRent.class) {
            return new MarketRentLabel();
        } else {
            return factory.create(member);
        }
    }

    @Override
    protected CEntityFolderItem<ApptUnit> createItem() {
        return new UnitTableRow(ApptUnit.class, columns);
    }

    @Override
    protected FolderDecorator<ApptUnit> createFolderDecorator() {
        TableFolderDecorator<ApptUnit> tfd = new TableFolderDecorator<ApptUnit>(columns);
        tfd.getHeader().setStyleName(VistaStyles.ApartmentUnits.StylePrefix + VistaStyles.ApartmentUnits.StyleSuffix.UnitListHeader);

        floorplanRawPanel = new FlowPanel();
        floorplanRawPanel.setWidth("100%");
        floorplanRawPanel.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        tfd.insert(floorplanRawPanel, tfd.getWidgetIndex(tfd.getHeader()) + 1);
        tfd.insert(new ViewLineSeparator(700, Unit.PX, 0, Unit.EM, 0.5, Unit.EM), tfd.getWidgetCount() - 1);
        return tfd;

    }

    private void createFloorplanRaw(AvailableUnitsByFloorplan availableUnits) {
        floorplanRawPanel.clear();
        for (EntityFolderColumnDescriptor column : columns) {
            HorizontalPanel cellPanel = new HorizontalPanel();
            cellPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
            cellPanel.getElement().getStyle().setMarginLeft(3, Style.Unit.PX);
            cellPanel.getElement().getStyle().setMarginRight(3, Style.Unit.PX);
            cellPanel.setWidth(column.getWidth());

            String caption = "&nbsp";
            Widget widgetToInsert = null;

            UnitsDataCalculatino calcs = new UnitsDataCalculatino(availableUnits.units());

            // fill the row:
            if (proto.floorplan().name() == column.getObject()) {
                widgetToInsert = new Image(SiteImages.INSTANCE.floorplan());
            } else if (proto.unitType() == column.getObject()) {
                caption = availableUnits.floorplan().name().getStringView();
            } else if (proto.marketRent() == column.getObject()) {
                caption = "From <br />" + "$" + calcs.minRent;
            } else if (proto.requiredDeposit() == column.getObject()) {
            } else if (proto.bedrooms() == column.getObject()) {
                caption = calcs.minBed + " - <br />" + calcs.maxBed;
            } else if (proto.bathrooms() == column.getObject()) {
                caption = calcs.minBath + " - <br />" + calcs.maxBath;
            } else if (proto.area() == column.getObject()) {
                caption = availableUnits.floorplan().area().getStringView();
            } else if (proto.avalableForRent() == column.getObject()) {
            }

            cellPanel.add(widgetToInsert != null ? widgetToInsert : new HTML(caption));
            floorplanRawPanel.add(cellPanel);
            widgetToInsert = null;
        }
    }

    static private class MarketRentLabel extends CAbstractLabel<IList<MarketRent>> {

        public MarketRentLabel() {
            super();
            setFormat(new IFormat<IList<MarketRent>>() {

                @Override
                public IList<MarketRent> parse(String string) {
                    return null;
                }

                @Override
                public String format(IList<MarketRent> value) {
                    if (value.size() < 1) {
                        return null;
                    } else {
                        return value.get(value.size() - 1).rent().getStringView();
                    }
                }
            });
        }
    }

    //
    // Unit representation:
    //
    private class UnitTableRow extends CEntityFolderRow<ApptUnit> {

        private ApartmentUnitDetailsPanel unitDetailsPanel;

        public UnitTableRow(Class<ApptUnit> clazz, List<EntityFolderColumnDescriptor> columns) {
            super(clazz, columns);
        }

        @Override
        public FolderItemDecorator createFolderItemDecorator() {
            final TableFolderItemDecorator decorator = new TableFolderItemDecorator(null, null, false);
            decorator.addItemClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    if (unitDetailsPanelShown != null) {
                        unitDetailsPanelShown.hide();
                    }

                    getContent().addStyleDependentName(VistaStyles.ApartmentUnits.StylePrefix + VistaStyles.ApartmentUnits.StyleDependent.selected.name());
                    setSelected(getValue());
                    selectedUnitChangeHandler.onValueChange(new ValueChangeEvent<ApptUnit>(getValue()) {
                    });
                }
            });

            decorator.addDomHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    if (!getContent().getStyleName().contains(VistaStyles.ApartmentUnits.StyleDependent.selected.name())) {
                        getContent().addStyleDependentName(VistaStyles.ApartmentUnits.StyleDependent.hover.name());
                    }
                }
            }, MouseOverEvent.getType());

            decorator.addDomHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    getContent().removeStyleDependentName(VistaStyles.ApartmentUnits.StyleDependent.hover.name());
                }
            }, MouseOutEvent.getType());

            getContent().setStyleName(VistaStyles.ApartmentUnits.StylePrefix + VistaStyles.ApartmentUnits.StyleSuffix.unitRowPanel);
            return decorator;
        }

        @Override
        public IsWidget createContent() {
            FlowPanel content = new FlowPanel();
            content.add(super.createContent());
            content.add(unitDetailsPanel = new ApartmentUnitDetailsPanel());
            return content;
        }

        private void showDetails(ApptUnit unit) {
            unitDetailsPanel.showUnitDetail(unit, selectedmarketRent, selectedMarketRentChangeHandler);
            unitDetailsPanelShown = unitDetailsPanel;
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto.floorplan().name()) {
                CLabel l = new CLabel();
                l.setAllowHtml(true);
                l.setValue("&nbsp");
                return l;
            } else {
                return super.createCell(column);
            }
        }
    }

    public void populate(UnitSelection value) {
        selectedmarketRent = value.markerRent();
        createFloorplanRaw(value.availableUnits());
        setSelected(value.selectedUnit());
    }

    private void setSelected(ApptUnit unit) {
        // clear all selected style:
        for (ApptUnit au : getValue()) {
            UnitTableRow unitTableRow = (UnitTableRow) getFolderRow(au);
            unitTableRow.getContent().removeStyleDependentName(VistaStyles.ApartmentUnits.StyleDependent.selected.name());
        }

        UnitTableRow unitTableRow = (UnitTableRow) getFolderRow(unit);
        if (unitTableRow != null) {
            unitTableRow.showDetails(unit);
            unitTableRow.getContent().addStyleDependentName(VistaStyles.ApartmentUnits.StyleDependent.selected.name());
        }
    }

    private static class UnitsDataCalculatino {

        private double minTemp, maxTemp;

        public double minRent, maxRent;

        public double minBed, maxBed;

        public double minBath, maxBath;

        public UnitsDataCalculatino() {
        }

        public UnitsDataCalculatino(IList<ApptUnit> units) {
            calcValues(units);
        }

        public void calcValues(IList<ApptUnit> units) {
            minRent = Double.MAX_VALUE;
            maxRent = Double.MIN_VALUE;

            minBed = Double.MAX_VALUE;
            maxBed = Double.MIN_VALUE;

            minBath = Double.MAX_VALUE;
            maxBath = Double.MIN_VALUE;

            for (com.propertyvista.portal.domain.ApptUnit u : units) {
                calcValues(u);
                minRent = Math.min(minRent, minTemp);
                maxRent = Math.max(maxRent, maxTemp);

                minBed = Math.min(minBed, u.bedrooms().getValue());
                maxBed = Math.max(maxBed, u.bedrooms().getValue());

                minBath = Math.min(minBath, u.bedrooms().getValue());
                maxBath = Math.max(maxBath, u.bedrooms().getValue());
            }

            // correct values if there was no integrations at all!
            if (minRent == Double.MAX_VALUE)
                minRent = 0;
            if (maxRent == Double.MIN_VALUE)
                maxRent = 0;

            if (minBed == Double.MAX_VALUE)
                minBed = 0;
            if (maxBed == Double.MIN_VALUE)
                maxBed = 0;

            if (minBath == Double.MAX_VALUE)
                minBath = 0;
            if (maxBath == Double.MIN_VALUE)
                maxBath = 0;
        }

        private void calcValues(ApptUnit unit) {
            minTemp = Double.MAX_VALUE;
            maxTemp = Double.MIN_VALUE;
            for (MarketRent mr : unit.marketRent()) {
                minTemp = Math.min(minTemp, mr.rent().amount().getValue());
                maxTemp = Math.max(minTemp, mr.rent().amount().getValue());
            }
        }
    }
}
