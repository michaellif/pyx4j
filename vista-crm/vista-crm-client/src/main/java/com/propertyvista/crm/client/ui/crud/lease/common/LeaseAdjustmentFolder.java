/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 13, 2012
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentFolder extends VistaBoxFolder<LeaseAdjustment> {

    private final List<LeaseAdjustment> populatedValues = new LinkedList<LeaseAdjustment>();

    private final CEntityEditor<? extends Lease> lease;

    public LeaseAdjustmentFolder(boolean modifyable, CEntityEditor<? extends Lease> lease) {
        super(LeaseAdjustment.class, modifyable);
        this.lease = lease;
    }

    @Override
    public IFolderItemDecorator<LeaseAdjustment> createItemDecorator() {
        BoxFolderItemDecorator<LeaseAdjustment> decor = (BoxFolderItemDecorator<LeaseAdjustment>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        // memorize populated values: 
        populatedValues.clear();
        populatedValues.addAll(getValue());
    }

    @Override
    protected void addItem(LeaseAdjustment newEntity) {
        if (newEntity.isEmpty()) {
            newEntity.targetDate().setValue(new LogicalDate());
        }
        super.addItem(newEntity);
    }

    @Override
    protected void removeItem(CEntityFolderItem<LeaseAdjustment> item) {
        if (!lease.getValue().approvalDate().isNull() && populatedValues.contains(item.getValue())) {
            item.setValue(item.getValue(), false);
            item.setEditable(false);
            ValueChangeEvent.fire(this, getValue());
        } else {
            super.removeItem(item);
        }
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LeaseAdjustment) {
            return new LeaseAdjustmentEditor();
        }
        return super.create(member);
    }

    @Override
    protected CEntityFolderItem<LeaseAdjustment> createItem(boolean first) {
        final CEntityFolderItem<LeaseAdjustment> item = super.createItem(first);
        item.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.repopulated) {
                    if (isAddable() && !lease.getValue().approvalDate().isNull()) {
                        LogicalDate value = item.getValue().targetDate().getValue();
                        if ((value != null) && !value.after(TimeUtils.today())) {
                            item.setViewable(true);
                            item.inheritViewable(false);

                            item.setMovable(false);
                            item.setRemovable(false);
                        }
                    }
                }
            }
        });
        return item;
    }

    private class LeaseAdjustmentEditor extends CEntityDecoratableEditor<LeaseAdjustment> {

        public LeaseAdjustmentEditor() {
            super(LeaseAdjustment.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reason()), 35).build());

            HorizontalPanel dates = new HorizontalPanel();
            dates.add(new DecoratorBuilder(inject(proto().targetDate()), 9).build());
            main.setWidget(++row, 0, dates);

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 9).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 35).build());

            return main;
        }
    }
}