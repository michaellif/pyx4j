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
package com.propertyvista.crm.client.ui.crud.settings.financial.leaseadjustmentreason;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.components.boxes.GlCodeSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentReasonForm extends CrmEntityForm<LeaseAdjustmentReason> {

    private static final I18n i18n = I18n.get(LeaseAdjustmentReasonForm.class);

    public LeaseAdjustmentReasonForm() {
        this(false);
    }

    public LeaseAdjustmentReasonForm(boolean viewMode) {
        super(LeaseAdjustmentReason.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().actionType()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().glCode(), new CEntitySelectorHyperlink<GlCode>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(GlCode.class, getValue().glCodeCategory().getPrimaryKey());
            }

            @Override
            protected AbstractEntitySelectorDialog<GlCode> getSelectorDialog() {
                return new GlCodeSelectorDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            get(LeaseAdjustmentReasonForm.this.proto().glCode()).setValue(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 25).build());

        selectTab(addTab(content, i18n.tr("General")));

    }
}