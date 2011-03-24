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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.common.client.ui.ViewLineSeparator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.pt.TenantCharge;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.validators.EditableValueValidator;

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
        columns.add(new EntityFolderColumnDescriptor(proto().tenantFullName(), "260px"));
        columns.add(new EntityFolderColumnDescriptor(proto().percentage(), "35px"));
        columns.add(new EntityFolderColumnDescriptor(proto().charge(), "80px"));
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member == proto().tenantFullName()) {
            return new CLabel();
        } else {
            return super.create(member);
        }
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
                    Widget sp = new ViewLineSeparator(400, Unit.PX, 0.5, Unit.EM, 0.5, Unit.EM);
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
            protected Widget createCellDecorator(EntityFolderColumnDescriptor column, CComponent<?> component, String width) {
                Widget w = super.createCellDecorator(column, component, width);
                if (column.getObject() != proto().tenantFullName()) {
                    w.getElement().getStyle().setProperty("textAlign", "right");
                    component.asWidget().getElement().getStyle().setProperty("textAlign", "right");
                }

                if (column.getObject() == proto().percentage()) {
                    FlowPanel wrap = new FlowPanel();
                    wrap.add(DecorationUtils.inline(w, "35px"));
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
                        prc.addValueValidator(new EditableValueValidator<Integer>() {

                            @Override
                            public boolean isValid(CEditableComponent<Integer, ?> component, Integer value) {
                                int totalPrc = 0;
                                boolean first = true;
                                for (TenantCharge charge : ChargeSplitListFolder.this.getValue()) {
                                    if (first) {
                                        // Ignore first one since it is calculated
                                        first = false;
                                        continue;
                                    }
                                    Integer p = charge.percentage().getValue();
                                    if (p != null) {
                                        totalPrc += p.intValue();
                                    }
                                }
                                return totalPrc <= 100;
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Integer, ?> component, Integer value) {
                                return i18n.tr("Sum of all percentages should not exceed 100%");
                            }
                        });
                    }
                }
            }
        };
    }

}
