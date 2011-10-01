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
package com.propertyvista.portal.ptapp.client.ui.steps.charges;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.portal.domain.ptapp.TenantCharge;

public class ChargeSplitListFolder extends CEntityFolder<TenantCharge> {

    private static I18n i18n = I18nFactory.getI18n(ChargeSplitListFolder.class);

    private final boolean summaryViewMode;

    private final List<EntityFolderColumnDescriptor> columns;

    ChargeSplitListFolder(boolean summaryViewMode) {
        super(TenantCharge.class);
        this.summaryViewMode = summaryViewMode;
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
    public void addValidations() {
        this.addValueValidator(new EditableValueValidator<IList<TenantCharge>>() {

            @Override
            public boolean isValid(CEditableComponent<IList<TenantCharge>, ?> component, IList<TenantCharge> value) {
                int totalPrc = 0;
                boolean first = true;
                for (TenantCharge charge : value) {
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
            public String getValidationMessage(CEditableComponent<IList<TenantCharge>, ?> component, IList<TenantCharge> value) {
                return i18n.tr("Sum of all percentages should not exceed 100%");
            }
        });
    }

    @Override
    public ChargeSplitListFolderDecorator getFolderDecorator() {
        return (ChargeSplitListFolderDecorator) super.getFolderDecorator();
    }

    @Override
    protected IFolderDecorator<TenantCharge> createFolderDecorator() {
        return new ChargeSplitListFolderDecorator();
    }

    @Override
    protected CEntityFolderItemEditor<TenantCharge> createItem() {
        return new CEntityFolderRowEditor<TenantCharge>(TenantCharge.class, columns) {

            @Override
            public IFolderItemDecorator<TenantCharge> createFolderItemDecorator() {
                TableFolderItemDecorator<TenantCharge> dec = new TableFolderItemDecorator<TenantCharge>(null);
                if (!isFirst()) {
                    Widget sp = new VistaLineSeparator(400, Unit.PX, 0.5, Unit.EM, 0.5, Unit.EM);
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
                    if (!summaryViewMode) {
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
                if (!summaryViewMode) {
                    CEditableComponent<Integer, ?> prc = get(proto().percentage());
                    if (prc instanceof CNumberField) {
                        ((CNumberField<Integer>) prc).setRange(0, 100);
                    }
                }
            }
        };
    }

    public class ChargeSplitListFolderDecorator extends VerticalPanel implements IFolderDecorator<TenantCharge> {

        private final HTML validationMessageHolder;

        ChargeSplitListFolderDecorator() {
            validationMessageHolder = new HTML();
            validationMessageHolder.getElement().getStyle().setColor("red");
            add(validationMessageHolder);
        }

        @Override
        public void onValueChange(ValueChangeEvent<IList<TenantCharge>> event) {
        }

        @Override
        public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
            return null;
        }

        @Override
        public void setFolder(final CEntityFolder<?> folder) {
            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            if (getWidgetCount() > 1) {
                remove(1);
            }
            insert(folder.getContainer(), 1);
            folder.addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VALIDITY) {
                        validationMessageHolder.setHTML(folder.getContainerValidationResults().getMessagesText(true));
                    }
                }
            });

        }
    }
}
