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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
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

        private Widget creditCheckResultPanel;

        public LeaseParticipanApprovalViewer() {
            super(LeaseParticipanApprovalDTO.class);
            setEditable(false);
            setViewable(true);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel left = new FormFlexPanel();
            int row = -1;
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().leaseParticipant().participantId()), 7).build());
            left.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().leaseParticipant().leaseParticipant().customer().person().name(), new CEntityHyperlink<Name>(null,
                            new Command() {
                                @Override
                                public void execute() {
                                    if (getValue().leaseParticipant().isInstanceOf(LeaseTermTenant.class)) {
                                        AppSite.getPlaceController().goTo(
                                                AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().leaseParticipant().leaseParticipant()
                                                        .getPrimaryKey()));
                                    } else if (getValue().leaseParticipant().isInstanceOf(LeaseTermGuarantor.class)) {
                                        AppSite.getPlaceController().goTo(
                                                AppPlaceEntityMapper.resolvePlace(Guarantor.class, getValue().leaseParticipant().leaseParticipant()
                                                        .getPrimaryKey()));
                                    } else {
                                        throw new IllegalArgumentException("Incorrect LeaseParticipant value!");
                                    }
                                }
                            })), 20).build());

            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant().role()), 15).build());

            left.setBR(++row, 0, 1);

            left.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().screening(),
                            new CEntityCrudHyperlink<CustomerScreening>(AppPlaceEntityMapper.resolvePlace(CustomerScreening.class))), 10).build());

            creditCheckResultPanel = createCreditCheckResultPanel();

            // assemble main panel:
            FormFlexPanel main = new FormFlexPanel();

            main.setWidget(0, 0, left);
            main.setWidget(0, 1, creditCheckResultPanel);

            main.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            main.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

            return main;
        }

        Widget createCreditCheckResultPanel() {
            FormFlexPanel panel = new FormFlexPanel();

            FormFlexPanel left = new FormFlexPanel();

            int row = -1;
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().creditCheckResult()), 10).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().reason()), 10).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().amountApproved()), 10).build());

            left.setBR(++row, 0, 1);

            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().creditCheckDate()), 10).build());
            left.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().creditCheckReport(), new CCreditCheckReportHyperlink()), 10).build());

            FormFlexPanel right = new FormFlexPanel();
            row = -1;

            right.setWidget(++row, 0, new HTML("<i>" + i18n.tr("Credit Check Parameters:") + "</i>"));
            right.getWidget(row, 0).getElement().getStyle().setFontWeight(FontWeight.NORMAL);
            right.getWidget(row, 0).getElement().getStyle().setMarginLeft(2, Unit.EM);

            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().bankruptcy()), 5).labelWidth(10).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().judgment()), 5).labelWidth(10).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().collection()), 5).labelWidth(10).build());
            right.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().chargeOff()), 5).labelWidth(10).build());

            // assemble main panel:
            panel.setWidget(0, 0, left);
            panel.setWidget(0, 1, right);

            panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().screening()).setVisible(!getValue().screening().isNull());
            creditCheckResultPanel.setVisible(!getValue().creditCheck().isNull());

            CreditCheckResult creditCheckResult = getValue().creditCheck().creditCheckResult().getValue();

            get(proto().creditCheck().creditCheckResult()).setVisible(creditCheckResult != CreditCheckResult.Accept);
            get(proto().creditCheck().amountApproved()).setVisible(creditCheckResult == CreditCheckResult.Accept);
            get(proto().creditCheck().reason()).setVisible(creditCheckResult != CreditCheckResult.Accept);
        }

        private class CCreditCheckReportHyperlink extends CHyperlink<Key> {

            public CCreditCheckReportHyperlink() {
                super("View Full Report");

                setCommand(new Command() {
                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(
                                new CrmSiteMap.Tenants.CustomerCreditCheckLongReport().formViewerPlace(LeaseParticipanApprovalViewer.this.getValue()
                                        .leaseParticipant().leaseParticipant().customer().getPrimaryKey()));
                    }
                });

                setFormat(new IFormat<Key>() {
                    @Override
                    public String format(Key value) {
                        if (value != null) {
                            return "View";
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public Key parse(String string) {
                        return null;
                    }
                });
            }
        }
    }
}