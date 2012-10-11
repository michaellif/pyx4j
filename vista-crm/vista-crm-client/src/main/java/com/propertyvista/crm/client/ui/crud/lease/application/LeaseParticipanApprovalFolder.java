/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;

public class LeaseParticipanApprovalFolder extends VistaBoxFolder<LeaseParticipanApprovalDTO> {

    private static final I18n i18n = I18n.get(LeaseParticipanApprovalFolder.class);

    public LeaseParticipanApprovalFolder(boolean modifyable) {
        super(LeaseParticipanApprovalDTO.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LeaseParticipanApprovalDTO) {
            return new LeaseParticipanApprovalViewer();
        }
        return super.create(member);
    }

    private class LeaseParticipanApprovalViewer extends CEntityDecoratableForm<LeaseParticipanApprovalDTO> {

        public LeaseParticipanApprovalViewer() {
            super(LeaseParticipanApprovalDTO.class);
            setEditable(false);
            setViewable(true);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            FormFlexPanel left = new FormFlexPanel();
            int row = -1;
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().leaseCustomer().participantId()), 7).build());
            left.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().leaseParticipant().leaseCustomer().customer().person(), new CEntityHyperlink<LeaseParticipant<?>>(null,
                            new Command() {
                                @Override
                                public void execute() {
                                    AppSite.getPlaceController().goTo(getTargetPlace());
                                }

                                private AppPlace getTargetPlace() {
                                    if (getValue().leaseParticipant().isInstanceOf(Tenant.class)) {
                                        return AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().leaseParticipant().getPrimaryKey());
                                    } else if (getValue().leaseParticipant().isInstanceOf(Guarantor.class)) {
                                        return AppPlaceEntityMapper.resolvePlace(Guarantor.class, getValue().leaseParticipant().getPrimaryKey());
                                    } else {
                                        throw new IllegalArgumentException("Incorrect LeaseParticipant value!");
                                    }
                                }
                            })), 20).build());

            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().role()), 15).build());

            FormFlexPanel right = new FormFlexPanel();
            row = -1;
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().creditCheckResult()), 10).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().declineReason()), 25).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().amountApproved()), 10).build());

            right.setBR(++row, 0, 1);

            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().creditCheckDate()), 10).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().creditCheckReport(), new CLabel<Key>()), 10).build());
            right.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().creditCheck().screening(),
                            new CEntityCrudHyperlink<PersonScreening>(AppPlaceEntityMapper.resolvePlace(PersonScreening.class))), 10).build());

            // assemble main panel:
            main.setWidget(0, 0, left);
            main.setWidget(0, 1, right);

            main.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            main.getColumnFormatter().setWidth(0, "30%");
            main.getColumnFormatter().setWidth(1, "70%");

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().creditCheck().amountApproved()).setVisible(getValue().creditCheck().creditCheckResult().getValue() == CreditCheckResult.Accept);
            get(proto().creditCheck().declineReason()).setVisible(getValue().creditCheck().creditCheckResult().getValue() != CreditCheckResult.Accept);
        }
    }
}