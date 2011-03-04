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

import static com.pyx4j.commons.HtmlUtils.h3;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.ui.components.ReadOnlyComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewLineSeparator;
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

public class UnitsTable extends CEntityFolder<com.propertyvista.portal.domain.Unit> {

    private static I18n i18n = I18nFactory.getI18n(UnitsTable.class);

    public static String DEFAULT_STYLE_PREFIX = "UnitsTable";

    public static enum StyleSuffix implements IStyleSuffix {
        UnitListHeader, SelectedUnit, unitRowPanel, unitDetailPanel
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final List<EntityFolderColumnDescriptor> columns;

    private final ReadOnlyComponentFactory factory = new ReadOnlyComponentFactory();

    private UnitDetailsPanel unitDetailsPanelShown = null;

    com.propertyvista.portal.domain.Unit selectUnit;

    public UnitsTable() {
        super();

        com.propertyvista.portal.domain.Unit proto = EntityFactory.getEntityPrototype(com.propertyvista.portal.domain.Unit.class);
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto.unitType(), "120px"));
        columns.add(new EntityFolderColumnDescriptor(proto.marketRent(), "120px"));
        columns.add(new EntityFolderColumnDescriptor(proto.requiredDeposit(), "100px"));
        columns.add(new EntityFolderColumnDescriptor(proto.bedrooms(), "100px"));
        columns.add(new EntityFolderColumnDescriptor(proto.bathrooms(), "100px"));
        columns.add(new EntityFolderColumnDescriptor(proto.area(), "100px"));
        columns.add(new EntityFolderColumnDescriptor(proto.avalableForRent(), "100px"));
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
    protected CEntityFolderItem<com.propertyvista.portal.domain.Unit> createItem() {
        return new UnitTableRow(com.propertyvista.portal.domain.Unit.class, columns);
    }

    @Override
    protected FolderDecorator<com.propertyvista.portal.domain.Unit> createFolderDecorator() {
        return new TableFolderDecorator<com.propertyvista.portal.domain.Unit>(columns);
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
    private class UnitTableRow extends CEntityFolderRow<com.propertyvista.portal.domain.Unit> {

        private UnitDetailsPanel unitDetailsPanel;

        public UnitTableRow(Class<com.propertyvista.portal.domain.Unit> clazz, List<EntityFolderColumnDescriptor> columns) {
            super(clazz, columns);
        }

        @Override
        public FolderItemDecorator createFolderItemDecorator() {
            TableFolderItemDecorator decorator = new TableFolderItemDecorator(null, null, false);
            decorator.addItemClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    if (unitDetailsPanelShown != null) {
                        unitDetailsPanelShown.hide();
                    }
                    selectUnit.set(getValue());
                    unitDetailsPanel.showUnitDetail(selectUnit);
                    unitDetailsPanelShown = unitDetailsPanel;
                }
            });
            return decorator;
        }

        @Override
        public IsWidget createContent() {
            FlowPanel content = new FlowPanel();
            content.add(super.createContent());
            content.add(unitDetailsPanel = new UnitDetailsPanel());
            return content;
        }
    }

    public void setSelected(com.propertyvista.portal.domain.Unit unit, MarketRent marketRent) {
        selectUnit = unit;
        UnitTableRow unitTableRow = (UnitTableRow) getFolderRow(unit);
        if (unitTableRow != null) {
            //unitTableRow.showDetails());
        }
    }
}
