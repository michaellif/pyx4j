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
 */
package com.propertyvista.crm.client.ui.crud.lease.application;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.PermitViewAccessAdapter;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.lease.ac.CreditCheckViewReport;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseParticipanApprovalFolder extends VistaBoxFolder<LeaseParticipanApprovalDTO> {

    private static final I18n i18n = I18n.get(LeaseParticipanApprovalFolder.class);

    private final LeaseApplicationViewerView view;

    public LeaseParticipanApprovalFolder(LeaseApplicationViewerView view) {
        super(LeaseParticipanApprovalDTO.class, false);
        this.view = view;
    }

    @Override
    protected CForm<LeaseParticipanApprovalDTO> createItemForm(IObject<?> member) {
        return new LeaseParticipanApprovalViewer();
    }

    private class LeaseParticipanApprovalViewer extends CForm<LeaseParticipanApprovalDTO> {

        private final Widget creditCheckResultPanel = createCreditCheckResultPanel();

        public LeaseParticipanApprovalViewer() {
            super(LeaseParticipanApprovalDTO.class);
            setEditable(false);
            setViewable(true);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();

            main.setWidget(0, 0, createLeaseParticipantInfoPanel());
            if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
                main.setWidget(0, 1, creditCheckResultPanel);
            }

            main.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            main.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

            return main;
        }

        Widget createLeaseParticipantInfoPanel() {
            BasicFlexFormPanel panel = new BasicFlexFormPanel();

            int row = -1;
            panel.setWidget(++row, 0, inject(proto().leaseParticipant().leaseParticipant().participantId(), new FieldDecoratorBuilder(10, 15).build()));
            panel.setWidget(
                    ++row,
                    0,
                    inject(proto().leaseParticipant().leaseParticipant().customer().person().name(), new CEntityLabel<Name>(),
                            new FieldDecoratorBuilder(10, 15).build()));
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

            panel.setWidget(++row, 0, inject(proto().leaseParticipant().role(), new FieldDecoratorBuilder(10, 15).build()));

            panel.setBR(++row, 0, 1);

            panel.setWidget(
                    ++row,
                    0,
                    inject(proto().screening(),
                            new CEntityCrudHyperlink<LeaseParticipantScreeningTO>(AppPlaceEntityMapper.resolvePlace(LeaseParticipantScreeningTO.class)),
                            new FieldDecoratorBuilder(10, 15).build()));
            return panel;
        }

        Widget createCreditCheckResultPanel() {
            BasicFlexFormPanel left = new BasicFlexFormPanel();

            int row = -1;
            left.setWidget(++row, 0, inject(proto().creditCheck().creditCheckResult(), new FieldDecoratorBuilder(15, 10).build()));
            left.setWidget(++row, 0, inject(proto().creditCheck().reason(), new FieldDecoratorBuilder(15, 10).build()));
            left.setWidget(++row, 0, inject(proto().creditCheck().amountApproved(), new FieldDecoratorBuilder(15, 10).build()));

            left.setHR(++row, 0, 1);

            left.setWidget(++row, 0, inject(proto().creditCheck().creditCheckDate(), new FieldDecoratorBuilder(15, 10).build()));

            CLabel<Key> creditCheckReport = new CLabel<Key>();
            creditCheckReport.setTitle(i18n.tr("View Full Report"));
            creditCheckReport.setFormatter(new IFormatter<Key, String>() {
                @Override
                public String format(Key value) {
                    if (value != null) {
                        return i18n.tr("View");
                    } else {
                        return null;
                    }
                }

            });
            creditCheckReport.addAccessAdapter(new PermitViewAccessAdapter(new ActionPermission(CreditCheckViewReport.class)));
            creditCheckReport.setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    ((LeaseApplicationViewerView.Presenter) ((IPrimeViewerView<?>) view).getPresenter())
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
            left.setWidget(++row, 0, inject(proto().creditCheck().creditCheckReport(), creditCheckReport, new FieldDecoratorBuilder(15, 10).build()));

            BasicFlexFormPanel right = new BasicFlexFormPanel();
            row = -1;

            right.setWidget(++row, 0, new HTML("<i>" + i18n.tr("Credit Check Parameters:") + "</i>"));
            right.getWidget(row, 0).getElement().getStyle().setFontWeight(FontWeight.NORMAL);

            right.setWidget(++row, 0, inject(proto().creditCheck().backgroundCheckPolicy().bankruptcy(), new FieldDecoratorBuilder(8, 3).build()));
            right.setWidget(++row, 0, inject(proto().creditCheck().backgroundCheckPolicy().judgment(), new FieldDecoratorBuilder(8, 3).build()));
            right.setWidget(++row, 0, inject(proto().creditCheck().backgroundCheckPolicy().collection(), new FieldDecoratorBuilder(8, 3).build()));
            right.setWidget(++row, 0, inject(proto().creditCheck().backgroundCheckPolicy().chargeOff(), new FieldDecoratorBuilder(8, 3).build()));

            // assemble main panel:
            BasicFlexFormPanel panel = new BasicFlexFormPanel();

            panel.setWidget(0, 0, left);
            panel.setWidget(0, 1, right);

            panel.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            panel.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

            panel.getWidget(0, 1).getElement().getStyle().setMarginLeft(2, Unit.EM);

            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().screening()).setVisible(!getValue().screening().isNull());
            creditCheckResultPanel.setVisible(!getValue().creditCheck().isNull());

            CreditCheckResult creditCheckResult = getValue().creditCheck().creditCheckResult().getValue();

            get(proto().creditCheck().amountApproved()).setVisible(creditCheckResult == CreditCheckResult.Accept);
            get(proto().creditCheck().reason()).setVisible(creditCheckResult != CreditCheckResult.Accept);

            get(proto().creditCheck().creditCheckReport()).setVisible(
                    !getValue().creditCheck().creditCheckReport().isNull() && (creditCheckResult != CreditCheckResult.Error));
        }
    }
}