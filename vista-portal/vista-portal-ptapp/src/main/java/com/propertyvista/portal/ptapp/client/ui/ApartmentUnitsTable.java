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
package com.propertyvista.portal.ptapp.client.ui;

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
import com.pyx4j.widgets.client.AnimationCallback;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.common.client.ui.decorations.ViewLineSeparator;
import com.propertyvista.common.domain.marketing.MarketRent;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.ptapp.AvailableUnitsByFloorplan;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.components.VistaReadOnlyComponentFactory;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;

public class ApartmentUnitsTable extends CEntityFolder<AptUnitDTO> {

    private static I18n i18n = I18nFactory.getI18n(ApartmentUnitsTable.class);

    public final static String DEFAULT_STYLE_PREFIX = "ApartmentViewForm";

    public static enum StyleSuffix implements IStyleSuffix {
        UnitListHeader, UnitRowPanel, UnitDetailPanel
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final List<EntityFolderColumnDescriptor> columns;

    private final VistaReadOnlyComponentFactory factory = new VistaReadOnlyComponentFactory();

    private final ValueChangeHandler<AptUnitDTO> selectedUnitChangeHandler;

    private final ValueChangeHandler<Integer> selectedMarketRentChangeHandler;

    private HorizontalPanel floorplanRawPanel;

    private AptUnitDTO currentApartmentUnit = null;

    private Integer selectedLeaseTerm = null;

    public ApartmentUnitsTable(ValueChangeHandler<AptUnitDTO> selectedUnitChangeHandler, ValueChangeHandler<Integer> selectedMarketRentChangeHandler) {
        super(AptUnitDTO.class);
        this.selectedUnitChangeHandler = selectedUnitChangeHandler;
        this.selectedMarketRentChangeHandler = selectedMarketRentChangeHandler;

        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().floorplan().name(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto().unitType(), "142px"));
        columns.add(new EntityFolderColumnDescriptor(proto().unitRent(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto().requiredDeposit(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto().bedrooms(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto().bathrooms(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto().area(), "60px"));
        columns.add(new EntityFolderColumnDescriptor(proto().avalableForRent(), "120px"));
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass() == MarketRent.class) {
            return new MarketRentLabel();
        } else {
            return factory.create(member);
        }
    }

    public void populateFloorplan(AvailableUnitsByFloorplan availableUnits) {
        createFloorplanRaw(availableUnits);
    }

    AptUnitDTO setSelectedUnit(String unitId, Integer selectedLeaseTerm) {
        AptUnitDTO unitToSelect = null;
        if (unitId != null) {
            AptUnitDTO unit = EntityFactory.create(AptUnitDTO.class);
            unit.setPrimaryKey(unitId);
            int idx = getValue().indexOf(unit);
            if (idx != -1) {
                unitToSelect = getValue().get(idx);
                this.selectedLeaseTerm = selectedLeaseTerm;
            }
        } else {
            this.selectedLeaseTerm = null;
        }
        setSelectedUnit(unitToSelect, false);
        return unitToSelect;
    }

    private void setSelectedUnit(AptUnitDTO unit, boolean onClick) {
        if ((currentApartmentUnit != null) && currentApartmentUnit.equals(unit)) {
            return;
        }
        currentApartmentUnit = unit;

        if (onClick) {
            selectedLeaseTerm = null;
        }

        for (AptUnitDTO au : getValue()) {
            UnitTableRow unitTableRow = (UnitTableRow) getFolderRow(au);
            unitTableRow.setSelected(unitTableRow.getValue().equals(unit), onClick);
        }

        if (onClick) {
            selectedUnitChangeHandler.onValueChange(new ValueChangeEvent<AptUnitDTO>(unit) {
            });
        }
    }

    @Override
    protected CEntityFolderItem<AptUnitDTO> createItem() {
        return new UnitTableRow(AptUnitDTO.class, columns);
    }

    @Override
    protected FolderDecorator<AptUnitDTO> createFolderDecorator() {
        TableFolderDecorator<AptUnitDTO> tfd = new TableFolderDecorator<AptUnitDTO>(columns);
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
                widgetToInsert = new Image(PortalImages.INSTANCE.floorplan());
                widgetToInsert.ensureDebugId(VistaFormsDebugId.Available_Units_ViewPlan.debugId());
            } else if (proto().unitType() == column.getObject()) {
                caption = availableUnits.floorplan().name().getStringView();
            } else {
                if (availableUnits.units().size() != 0) {
                    UnitsDataCalc calcs = new UnitsDataCalc(availableUnits.units());
                    // fill the row:
                    if (proto().unitRent() == column.getObject()) {
                        caption = i18n.tr("From") + "<br />" + DomainUtil.createMoney(calcs.minRent).getStringView();
                    } else if (proto().requiredDeposit() == column.getObject()) {
                        caption = i18n.tr("From") + "<br />" + DomainUtil.createMoney(calcs.minDeposit).getStringView();
                    } else if (proto().bedrooms() == column.getObject()) {
                        caption = "<br />" + formatDoubleAsInt(calcs.minBed);
                    } else if (proto().bathrooms() == column.getObject()) {
                        caption = "<br />" + formatDoubleAsInt(calcs.minBath);
                    } else if (proto().area() == column.getObject()) {
                        caption = i18n.tr("From") + "<br />" + formatDoubleAsInt(calcs.minSqFt);
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

    private class UnitTableRow extends CEntityFolderRow<AptUnitDTO> {

        private ApartmentUnitDetailsPanel unitDetailsPanel;

        private Widget header;

        public UnitTableRow(Class<AptUnitDTO> clazz, List<EntityFolderColumnDescriptor> columns) {
            super(clazz, columns);
        }

        @Override
        public FolderItemDecorator createFolderItemDecorator() {
            final TableFolderItemDecorator decorator = new TableFolderItemDecorator(null, null, false);
            decorator.addItemClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    setSelectedUnit(getValue(), true);
                }
            });

            decorator.addDomHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    if (!header.getStyleName().contains(StyleDependent.selected.name())) {
                        header.addStyleDependentName(StyleDependent.hover.name());
                    }
                }
            }, MouseOverEvent.getType());

            decorator.addDomHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    header.removeStyleDependentName(StyleDependent.hover.name());
                }
            }, MouseOutEvent.getType());

            return decorator;
        }

        @Override
        public IsWidget createContent() {
            FlowPanel content = new FlowPanel();

            header = (Widget) super.createContent();
            header.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.UnitRowPanel);

            content.add(header);
            unitDetailsPanel = new ApartmentUnitDetailsPanel(header);
            unitDetailsPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.UnitDetailPanel);
            content.add(unitDetailsPanel);
            return content;
        }

        private void setSelected(boolean selected, boolean animate) {
            if (selected) {
                unitDetailsPanel.showUnitDetails(getValue(), selectedLeaseTerm, selectedMarketRentChangeHandler, animate, this.getDebugId());
                unitDetailsPanel.addStyleDependentName(StyleDependent.selected.name());
                header.addStyleDependentName(StyleDependent.selected.name());
                header.removeStyleDependentName(StyleDependent.hover.name());
            } else {
                unitDetailsPanel.hideUnitDetails(new AnimationCallback() {
                    @Override
                    public void onComplete() {
                        header.removeStyleDependentName(StyleDependent.selected.name());
                        unitDetailsPanel.removeStyleDependentName(StyleDependent.selected.name());
                    }
                }, animate);
            }
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

    private static class UnitsDataCalc {

        public double minRent;

        public double minDeposit;

        public double minBed;

        public double minBath;

        public double minSqFt;

        public Date minAvalableForRent;

        public UnitsDataCalc(IList<AptUnitDTO> units) {
            minRent = Double.MAX_VALUE;
            minDeposit = Double.MAX_VALUE;
            minBed = Double.MAX_VALUE;
            minBath = Double.MAX_VALUE;
            minSqFt = Double.MAX_VALUE;

            for (AptUnitDTO u : units) {
                minRent = Math.min(minRent, u.unitRent().getValue());
                minDeposit = Math.min(minDeposit, u.requiredDeposit().getValue());
                minBed = Math.min(minBed, u.bedrooms().getValue());
                minBath = Math.min(minBath, u.bathrooms().getValue());
                minSqFt = Math.min(minSqFt, u.area().getValue());
                if ((minAvalableForRent == null) || (minAvalableForRent.after(u.avalableForRent().getValue()))) {
                    minAvalableForRent = u.avalableForRent().getValue();
                }
            }
        }

    }
}
