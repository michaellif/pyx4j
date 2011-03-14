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
import java.util.Date;
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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.common.client.ui.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyComponentFactory;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.pt.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.pt.VistaFormsDebugId;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CAbstractLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class ApartmentUnitsTable extends CEntityFolder<ApptUnit> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentUnitsTable.class);

    public final static String DEFAULT_STYLE_PREFIX = "ApartmentViewForm";

    public static enum StyleSuffix implements IStyleSuffix {
        UnitListHeader, SelectedUnit, unitRowPanel, unitDetailPanel
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final List<EntityFolderColumnDescriptor> columns;

    private final ReadOnlyComponentFactory factory = new ReadOnlyComponentFactory();

    private ApartmentUnitDetailsPanel unitDetailsPanelShown = null;

    private final ValueChangeHandler<ApptUnit> selectedUnitChangeHandler;

    private final ValueChangeHandler<MarketRent> selectedMarketRentChangeHandler;

    private MarketRent selectedmarketRent;

    private HorizontalPanel floorplanRawPanel;

    public ApartmentUnitsTable(ValueChangeHandler<ApptUnit> selectedUnitChangeHandler, ValueChangeHandler<MarketRent> selectedMarketRentChangeHandler) {
        super(ApptUnit.class);
        this.selectedUnitChangeHandler = selectedUnitChangeHandler;
        this.selectedMarketRentChangeHandler = selectedMarketRentChangeHandler;

        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().floorplan().name(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto().unitType(), "100px"));
        columns.add(new EntityFolderColumnDescriptor(proto().marketRent(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto().requiredDeposit(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto().bedrooms(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto().bathrooms(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto().area(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto().avalableForRent(), "140px"));
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
        tfd.getHeader().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.UnitListHeader);

        floorplanRawPanel = new HorizontalPanel();
        tfd.insert(floorplanRawPanel, tfd.getWidgetIndex(tfd.getHeader()) + 1);
        tfd.insert(new ViewLineSeparator(700, Unit.PX, 0, Unit.EM, 0.5, Unit.EM), tfd.getWidgetCount() - 1);
        return tfd;

    }

    private String formatDoubleAsInt(double value) {
        if (Math.floor(value) == Math.ceil(value)) {
            return String.valueOf((int) value);
        } else {
            return String.valueOf(value);
        }
    }

    private void createFloorplanRaw(AvailableUnitsByFloorplan availableUnits) {
        floorplanRawPanel.clear();

        for (EntityFolderColumnDescriptor column : columns) {
            SimplePanel cellPanel = new SimplePanel();
            cellPanel.getElement().getStyle().setMarginLeft(3, Style.Unit.PX);
            cellPanel.getElement().getStyle().setMarginRight(3, Style.Unit.PX);
            cellPanel.setWidth(column.getWidth());

            String caption = "&nbsp";
            Widget widgetToInsert = null;

            if (proto().floorplan().name() == column.getObject()) {
                widgetToInsert = new Image(SiteImages.INSTANCE.floorplan());
                widgetToInsert.ensureDebugId(VistaFormsDebugId.Available_Units_ViewPlan.getDebugIdString());
            } else if (proto().unitType() == column.getObject()) {
                caption = availableUnits.floorplan().name().getStringView();
            } else {
                if (availableUnits.units().size() != 0) {
                    UnitsDataCalc calcs = new UnitsDataCalc(availableUnits.units());
                    // fill the row:
                    if (proto().marketRent() == column.getObject()) {
                        caption = "From <br />" + DomainUtil.createMoney(calcs.minRent).getStringView();
                    } else if (proto().requiredDeposit() == column.getObject()) {
                        caption = "<br />" + DomainUtil.createMoney(calcs.minDeposit).getStringView();
                    } else if (proto().bedrooms() == column.getObject()) {
                        caption = "<br />" + formatDoubleAsInt(calcs.minBed);
                    } else if (proto().bathrooms() == column.getObject()) {
                        caption = "<br />" + formatDoubleAsInt(calcs.minBath);
                    } else if (proto().area() == column.getObject()) {
                        caption = "<br />" + availableUnits.floorplan().area().getStringView();
                    } else if (proto().avalableForRent() == column.getObject()) {
                        caption = "<br />" + DateTimeFormat.getFormat(proto().avalableForRent().getMeta().getFormat()).format(calcs.minAvalableForRent);
                    }
                }
            }

            cellPanel.setWidget(widgetToInsert != null ? widgetToInsert : new HTML(caption));
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

                    getContent().addStyleDependentName(StyleDependent.selected.name());
                    setSelected(getValue());
                    selectedUnitChangeHandler.onValueChange(new ValueChangeEvent<ApptUnit>(getValue()) {
                    });
                }
            });

            decorator.addDomHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    if (!getContent().getStyleName().contains(StyleDependent.selected.name())) {
                        getContent().addStyleDependentName(StyleDependent.hover.name());
                    }
                }
            }, MouseOverEvent.getType());

            decorator.addDomHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    getContent().removeStyleDependentName(StyleDependent.hover.name());
                }
            }, MouseOutEvent.getType());

            getContent().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.unitRowPanel);
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
            unitDetailsPanel.showUnitDetail(unit, selectedmarketRent, selectedMarketRentChangeHandler, this.getDebugId());
            unitDetailsPanelShown = unitDetailsPanel;
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().floorplan().name()) {
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
            unitTableRow.getContent().removeStyleDependentName(StyleDependent.selected.name());
        }

        UnitTableRow unitTableRow = (UnitTableRow) getFolderRow(unit);
        if (unitTableRow != null) {
            unitTableRow.showDetails(unit);
            unitTableRow.getContent().addStyleDependentName(StyleDependent.selected.name());
        }
    }

    private static class UnitsDataCalc {

        public double minRent;

        public double minDeposit;

        public double minBed;

        public double minBath;

        public Date minAvalableForRent;

        public UnitsDataCalc(IList<ApptUnit> units) {
            minRent = Double.MAX_VALUE;
            minDeposit = Double.MAX_VALUE;
            minBed = Double.MAX_VALUE;
            minBath = Double.MAX_VALUE;

            for (com.propertyvista.portal.domain.ApptUnit u : units) {
                for (MarketRent mr : u.marketRent()) {
                    minRent = Math.min(minRent, mr.rent().amount().getValue());
                }
                minBed = Math.min(minBed, u.bedrooms().getValue());
                minBath = Math.min(minBath, u.bathrooms().getValue());
                minDeposit = Math.min(minDeposit, u.requiredDeposit().amount().getValue());

                if ((minAvalableForRent == null) || (minAvalableForRent.after(u.avalableForRent().getValue()))) {
                    minAvalableForRent = u.avalableForRent().getValue();
                }
            }
        }

    }
}
