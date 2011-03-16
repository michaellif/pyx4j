/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.common.client.ui.ViewLineSeparator;
import com.propertyvista.common.client.ui.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.VistaWidgetDecorator.DecorationData;
import com.propertyvista.common.client.ui.VistaWidgetDecorator.DecorationData.ShowMandatory;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.pt.TenantCharge;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.CNumberLabel;

public class ChargeSplitListFolder extends CEntityFolder<TenantCharge> {

    private static I18n i18n = I18nFactory.getI18n(ChargeSplitListFolder.class);

    @SuppressWarnings("rawtypes")
    private final ValueChangeHandler valueChangeHandler;

    private final List<EntityFolderColumnDescriptor> columns;

    @SuppressWarnings("rawtypes")
    ChargeSplitListFolder(ValueChangeHandler valueChangeHandler) {
        super(TenantCharge.class);
        this.valueChangeHandler = valueChangeHandler;
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().tenant(), "270px"));
        columns.add(new EntityFolderColumnDescriptor(proto().percentage(), "25px"));
        columns.add(new EntityFolderColumnDescriptor(proto().charge(), "80px"));
    }

    @Override
    protected FolderDecorator<TenantCharge> createFolderDecorator() {
        return new BoxReadOnlyFolderDecorator<TenantCharge>() {

            @Override
            public void setFolder(CEntityFolder<?> w) {
                super.setFolder(w);
                this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            }
        };
    }

    @Override
    protected CEntityFolderItem<TenantCharge> createItem() {
        return new CEntityFolderRow<TenantCharge>(TenantCharge.class, columns) {

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                TableFolderItemDecorator dec = new TableFolderItemDecorator(null);
                if (!isFirst()) {
                    Widget sp = new ViewLineSeparator(0, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
                    sp.getElement().getStyle().setPadding(0, Unit.EM);
                    ((VerticalPanel) dec.getWidget()).insert(sp, 0);
                }
                return dec;
            }

            @Override
            protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                if (isFirst() && (column.getObject() == proto().percentage())) {
                    return inject(column.getObject(), new CNumberLabel());
                }
                return super.createCell(column);
            }

            @Override
            protected Widget createDecorator(EntityFolderColumnDescriptor column, CComponent<?> component, String width) {
                Widget w = super.createDecorator(column, component, width);
                if (column.getObject() != proto().tenant()) {
                    w.getElement().getStyle().setProperty("textAlign", "right");
                    component.asWidget().getElement().getStyle().setProperty("textAlign", "right");
                }

                if (column.getObject() == proto().percentage()) {
                    FlowPanel wrap = new FlowPanel();
                    wrap.add(DecorationUtils.inline(w, "25px"));
                    // Add $ label before or after Input
                    IsWidget lable = DecorationUtils.inline(new HTML("%"), "10px");
                    if (valueChangeHandler != null) {
                        wrap.insert(lable, 0);
                    } else {
                        wrap.add(lable);
                    }
                    return wrap;
                }
                return w;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void addValidations() {
                if (valueChangeHandler != null) {
                    CEditableComponent<Integer, ?> prc = get(proto().percentage());
                    if (prc instanceof CNumberField) {
                        prc.addValueChangeHandler(valueChangeHandler);
                        ((CNumberField<Integer>) prc).setRange(0, 100);
                    }
                }
            }
        };
    }

    protected CEntityFolderItem<TenantCharge> createItemOld() {

        return new CEntityFolderItem<TenantCharge>(TenantCharge.class) {

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                return new BoxReadOnlyFolderItemDecorator(!isFirst());
            }

            @SuppressWarnings("unchecked")
            @Override
            public IsWidget createContent() {

                FlowPanel main = new FlowPanel();
                main.add(DecorationUtils.inline(inject(proto().tenant()), "285px", null));
                main.add(DecorationUtils.inline(new HTML("%"), "10px", "right"));

                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 4;
                decorData.labelWidth = 0;
                decorData.labelAlignment = HasHorizontalAlignment.ALIGN_RIGHT;
                decorData.readOnlyMode = (isFirst() || valueChangeHandler == null);
                decorData.showMandatory = ShowMandatory.None;
                main.add(DecorationUtils.inline(new VistaWidgetDecorator(inject(proto().percentage()), decorData), "25px", "right"));

                main.add(DecorationUtils.inline(inject(proto().charge()), "80px", "right"));
                if (valueChangeHandler != null) {
                    get(proto().percentage()).addValueChangeHandler(valueChangeHandler);
                }
                CEditableComponent<Integer, ?> prc = get(proto().percentage());
                if (prc instanceof CNumberField) {
                    ((CNumberField<Integer>) prc).setRange(0, 100);
                }
                return main;
            }
        };
    }
}
