/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.autopay;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class AutoPayListForm extends CPortalEntityForm<AutoPaySummaryDTO> {

    private static final I18n i18n = I18n.get(AutoPayListForm.class);

    private AutoPayListView.Presenter presenter;

    public AutoPayListForm() {
        super(AutoPaySummaryDTO.class, new VistaViewersComponentFactory(), null, i18n.tr("Auto Pay"), ThemeColor.contrast4);
        setViewable(true);
    }

    public void setPresenter(AutoPayListView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nextAutoPayDate(), new CDateLabel()), 100).labelWidth(12).build());
        content.setBR(++row, 0, 1);
        content.setWidget(++row, 0, inject(proto().currentAutoPayments(), new PreauthorizedPaymentFolder()));

        return content;
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<AutoPayDTO> {

        public PreauthorizedPaymentFolder() {
            super(AutoPayDTO.class, true);
            setOrderable(false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof AutoPayDTO) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPreauthorizedPayment();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<AutoPayDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                    presenter.deletePreauthorizedPayment(item.getValue());
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<AutoPayDTO> {

            private final BasicFlexFormPanel expirationWarning = new BasicFlexFormPanel();

            public PreauthorizedPaymentEditor() {
                super(AutoPayDTO.class);

                setViewable(true);
                inheritViewable(false);

                Widget expirationWarningLabel = new HTML(i18n.tr("This Pre-Authorized Payment is expired - needs to be replaced with new one!"));
                expirationWarningLabel.setStyleName(VistaTheme.StyleName.warningMessage.name());
                expirationWarning.setWidget(0, 0, expirationWarningLabel);
                expirationWarning.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
                expirationWarning.setHR(1, 0, 1);
                expirationWarning.setBR(2, 0, 1);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, expirationWarning);
                content.setWidget(++row, 0, inject(proto().tenant(), new CEntityLabel<Tenant>()));

                content.setHR(++row, 0, 1);

                content.setWidget(row, 0, inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()));
                ((CEntityLabel<LeasePaymentMethod>) get(proto().paymentMethod())).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        presenter.viewPaymentMethod(getValue());
                    }
                });

                content.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));
                get(proto().coveredItems()).setViewable(true);
                get(proto().coveredItems()).inheritViewable(false);

                return content;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                expirationWarning.setVisible(!getValue().expiring().isNull());

                boolean isCurrentTenant = getValue().tenant().customer().user().getPrimaryKey().equals(ClientContext.getUserVisit().getPrincipalPrimaryKey());
                ((CEntityFolderItem<AutoPayDTO>) getParent()).setRemovable(isCurrentTenant);
            }
        }
    }
}
