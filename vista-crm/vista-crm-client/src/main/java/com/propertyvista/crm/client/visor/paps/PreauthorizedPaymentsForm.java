/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.paps;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorLabel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemDtoFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.misc.PapExpirationWarning;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsForm extends CEntityDecoratableForm<PreauthorizedPaymentsDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private static String cutOffDateWarning = i18n.tr("All changes will take effect after this date!");

    private final PreauthorizedPaymentsVisorView visor;

    public PreauthorizedPaymentsForm(PreauthorizedPaymentsVisorView visor) {
        super(PreauthorizedPaymentsDTO.class);
        this.visor = visor;
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = -1;

        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, inject(proto().tenantInfo(), new CEntityLabel<PreauthorizedPaymentsDTO.TenantInfo>()));
        main.getWidget(row, 0).getElement().getStyle().setFontWeight(FontWeight.BOLD);
        main.getWidget(row, 0).getElement().getStyle().setFontSize(1.2, Unit.EM);
        main.getWidget(row, 0).setWidth("25em");

        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel())).labelWidth(20).build());

        main.setH3(++row, 0, 1, proto().preauthorizedPayments().getMeta().getCaption());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);
        main.setWidget(++row, 0, inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        LogicalDate today = new LogicalDate(ClientContext.getServerDate());
        if (!today.before(getValue().paymentCutOffDate().getValue()) && !today.after(getValue().nextScheduledPaymentDate().getValue())) {
            get(proto().nextScheduledPaymentDate()).setNote(cutOffDateWarning, NoteStyle.Warn);
        } else {
            get(proto().nextScheduledPaymentDate()).setNote(null);
        }
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentDTO.class, true);
            setOrderable(false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPaymentDTO) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void createNewEntity(final AsyncCallback<PreauthorizedPaymentDTO> callback) {
            visor.getController().create(new DefaultAsyncCallback<PreauthorizedPaymentDTO>() {
                @Override
                public void onSuccess(PreauthorizedPaymentDTO result) {
                    callback.onSuccess(result);
                }
            });
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PreauthorizedPaymentDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPaymentDTO> {

            private final PapExpirationWarning expirationWarning = new PapExpirationWarning();

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentDTO.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, 2, expirationWarning.getExpirationWarningPanel());

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creationDate()), 9).build());
                content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().creator(), new CEntityLabel<AbstractPmcUser>()), 22).build());

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntitySelectorLabel<LeasePaymentMethod>() {
                    @Override
                    protected AbstractEntitySelectorDialog<LeasePaymentMethod> getSelectorDialog() {
                        return new EntitySelectorListDialog<LeasePaymentMethod>(i18n.tr("Select Payment Method"), false, PreauthorizedPaymentsForm.this
                                .getValue().availablePaymentMethods()) {
                            @Override
                            public boolean onClickOk() {
                                get(proto().paymentMethod()).setValue(getSelectedItems().iterator().next());
                                return true;
                            }
                        };
                    }
                })).componentWidth("35em").build());

                content.setBR(++row, 0, 2);

                content.setWidget(++row, 0, 2, inject(proto().coveredItemsDTO(), new PapCoveredItemDtoFolder()));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                expirationWarning.prepareView(getValue().expiring());
                setEditable(getValue().expiring().isNull());

                get(proto().creator()).setVisible(!getValue().creator().isNull());
            }
        }
    }
}
