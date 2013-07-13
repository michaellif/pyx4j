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
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseParticipanApprovalFolder extends VistaBoxFolder<LeaseParticipanApprovalDTO> {

    private static final I18n i18n = I18n.get(LeaseParticipanApprovalFolder.class);

    private final LeaseApplicationViewerView view;

    public LeaseParticipanApprovalFolder(boolean modifyable, LeaseApplicationViewerView view) {
        super(LeaseParticipanApprovalDTO.class, modifyable);
        this.view = view;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
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
            TwoColumnFlexFormPanel left = new TwoColumnFlexFormPanel();
            int row = -1;
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().leaseParticipant().participantId()), 7).build());
            left.setWidget(++row, 0,
                    new FormDecoratorBuilder(inject(proto().leaseParticipant().leaseParticipant().customer().person().name(), new CEntityLabel<Name>()), 20)
                            .build());
            ((CField) get(proto().leaseParticipant().leaseParticipant().customer().person().name())).setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    if (getValue().leaseParticipant().isInstanceOf(LeaseTermTenant.class)) {
                        AppSite.getPlaceController().goTo(
                                AppPlaceEntityMapper.resolvePlace(Tenant.class, getValue().leaseParticipant().leaseParticipant().getPrimaryKey()));
                    } else if (getValue().leaseParticipant().isInstanceOf(LeaseTermGuarantor.class)) {
                        AppSite.getPlaceController().goTo(
                                AppPlaceEntityMapper.resolvePlace(Guarantor.class, getValue().leaseParticipant().leaseParticipant().getPrimaryKey()));
                    } else {
                        throw new IllegalArgumentException("Incorrect LeaseParticipant value!");
                    }
                }
            });

            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseParticipant().role()), 15).build());

            left.setBR(++row, 0, 1);

            left.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().screening(),
                            new CEntityCrudHyperlink<CustomerScreening>(AppPlaceEntityMapper.resolvePlace(CustomerScreening.class))), 10).build());

            creditCheckResultPanel = createCreditCheckResultPanel();

            // assemble main panel:
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            main.setWidget(0, 0, left);
            if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
                main.setWidget(0, 1, creditCheckResultPanel);
            }

            main.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            main.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

            main.getFlexCellFormatter().setWidth(0, 0, "35em");

            return main;
        }

        Widget createCreditCheckResultPanel() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

            TwoColumnFlexFormPanel left = new TwoColumnFlexFormPanel();

            int row = -1;
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().creditCheckResult()), 10).build());
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().reason()), 10).build());
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().amountApproved()), 10).build());

            left.setHR(++row, 0, 1);

            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().creditCheckDate()), 10).build());

            CLabel<Key> creditCheckReport = new CLabel<Key>(i18n.tr("View Full Report"));
            creditCheckReport.setFormat(new IFormat<Key>() {
                @Override
                public String format(Key value) {
                    if (value != null) {
                        return i18n.tr("View");
                    } else {
                        return null;
                    }
                }

                @Override
                public Key parse(String string) {
                    return null;
                }
            });
            creditCheckReport.setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    ((LeaseApplicationViewerView.Presenter) ((IViewer<?>) view).getPresenter())
                            .isCreditCheckViewAllowed(new DefaultAsyncCallback<VoidSerializable>() {

                                @Override
                                public void onSuccess(VoidSerializable result) {
                                    AppSite.getPlaceController().goTo(
                                            new CrmSiteMap.Tenants.CustomerCreditCheckLongReport().formViewerPlace(LeaseParticipanApprovalViewer.this
                                                    .getValue().leaseParticipant().leaseParticipant().customer().getPrimaryKey()));
                                }
                            });
                }
            });
            left.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().creditCheckReport(), creditCheckReport), 10).build());

            TwoColumnFlexFormPanel right = new TwoColumnFlexFormPanel();
            row = -1;

            right.setWidget(++row, 0, new HTML("<i>" + i18n.tr("Credit Check Parameters:") + "</i>"));
            right.getWidget(row, 0).getElement().getStyle().setFontWeight(FontWeight.NORMAL);
            right.getWidget(row, 0).getElement().getStyle().setMarginLeft(2, Unit.EM);

            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().bankruptcy()), 5).labelWidth(10).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().judgment()), 5).labelWidth(10).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().collection()), 5).labelWidth(10).build());
            right.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creditCheck().backgroundCheckPolicy().chargeOff()), 5).labelWidth(10).build());

            // assemble main panel:
            panel.setWidget(0, 0, left);
            panel.setWidget(0, 1, right);

            panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

            panel.getFlexCellFormatter().setWidth(0, 0, "30em");

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

            get(proto().creditCheck().creditCheckReport()).setVisible(!getValue().creditCheck().creditCheckReport().isNull());
        }
    }
}