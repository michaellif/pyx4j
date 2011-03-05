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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyComponentFactory;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.MarketRent;

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
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class UnitsTable extends CEntityFolder<ApptUnit> {

    private static I18n i18n = I18nFactory.getI18n(UnitsTable.class);

    public final static String DEFAULT_STYLE_PREFIX = "ApartmentViewForm";

    public static enum StyleSuffix implements IStyleSuffix {
        UnitListHeader, SelectedUnit, unitRowPanel, unitDetailPanel
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final List<EntityFolderColumnDescriptor> columns;

    private final ReadOnlyComponentFactory factory = new ReadOnlyComponentFactory();

    private UnitDetailsPanel unitDetailsPanelShown = null;

    private ApptUnit selectedUnit;

    private MarketRent selectedmarketRent;

    public UnitsTable() {
        super();

        //        this.asWidget().getElement().getStyle().setPaddingLeft(1, Unit.EM);
        //        this.asWidget().getElement().getStyle().setPaddingRight(1, Unit.EM);
        this.setWidth("700px");

        ApptUnit proto = EntityFactory.getEntityPrototype(ApptUnit.class);
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto.floorplan().name(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.unitType(), "140px"));
        columns.add(new EntityFolderColumnDescriptor(proto.marketRent(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.requiredDeposit(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.bedrooms(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.bathrooms(), "70px"));
        columns.add(new EntityFolderColumnDescriptor(proto.area(), "70px"));
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
        tfd.getHeader().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.UnitListHeader);
        return tfd;

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

        private UnitDetailsPanel unitDetailsPanel;

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
            content.add(unitDetailsPanel = new UnitDetailsPanel());
            return content;
        }

        private void showDetails(ApptUnit unit, MarketRent marketRent) {
            unitDetailsPanel.showUnitDetail(unit, selectedmarketRent);
            unitDetailsPanelShown = unitDetailsPanel;
        }
    }

    public void populate(ApptUnit unit, MarketRent marketRent) {
        selectedUnit = unit;
        selectedmarketRent = marketRent;
        setSelected(selectedUnit);
    }

    private void setSelected(ApptUnit unit) {
        // clear all selected style:
        for (ApptUnit au : getValue()) {
            UnitTableRow unitTableRow = (UnitTableRow) getFolderRow(au);
            unitTableRow.getContent().removeStyleDependentName(StyleDependent.selected.name());
        }

        UnitTableRow unitTableRow = (UnitTableRow) getFolderRow(unit);
        if (unitTableRow != null) {
            unitTableRow.showDetails(unit, selectedmarketRent);
            unitTableRow.getContent().addStyleDependentName(StyleDependent.selected.name());
            if (!selectedUnit.equals(unit)) {
                selectedUnit.setValue(unit.getValue());
            }
        }
    }
}
