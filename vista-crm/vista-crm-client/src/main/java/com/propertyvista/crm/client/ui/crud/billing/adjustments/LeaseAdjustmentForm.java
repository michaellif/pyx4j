/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.adjustments;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.components.boxes.LeaseAdjustmentReasonSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentForm extends CrmEntityForm<LeaseAdjustment> {

    public LeaseAdjustmentForm() {
        this(false);
    }

    public LeaseAdjustmentForm(boolean viewMode) {
        super(LeaseAdjustment.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reason(), new CEntitySelectorHyperlink<LeaseAdjustmentReason>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(LeaseAdjustmentReason.class, getValue().getPrimaryKey());
            }

            @Override
            protected AbstractEntitySelectorDialog<LeaseAdjustmentReason> getSelectorDialog() {
                return new LeaseAdjustmentReasonSelectorDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            get(LeaseAdjustmentForm.this.proto().reason()).setValue(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 25).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().tax()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().executionType()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().receivedDate()), 10).build());
        main.setWidget(row, 1, new DecoratorBuilder(inject(proto().targetDate()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        if (!isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createdWhen()), 10).build());
            main.setWidget(row, 1, new DecoratorBuilder(inject(proto().updated()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().createdBy()), 10).build());
        }
        return new ScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
    }
}