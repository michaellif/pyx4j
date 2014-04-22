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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorLabel;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.PapCoveredItemDtoFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.dto.tenant.PreauthorizedPaymentsDTO;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.dto.PreauthorizedPaymentDTO;

public class PreauthorizedPaymentsForm extends CForm<PreauthorizedPaymentsDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private final PreauthorizedPaymentsVisorView visor;

    public PreauthorizedPaymentsForm(PreauthorizedPaymentsVisorView visor) {
        super(PreauthorizedPaymentsDTO.class);
        this.visor = visor;
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = -1;

        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, inject(proto().tenantInfo(), new CEntityLabel<PreauthorizedPaymentsDTO.TenantInfo>()));
        main.getWidget(row, 0).getElement().getStyle().setFontWeight(FontWeight.BOLD);
        main.getWidget(row, 0).getElement().getStyle().setFontSize(1.2, Unit.EM);
        main.getWidget(row, 0).setWidth("25em");

        main.setWidget(row, 1, inject(proto().nextPaymentDate(), new CDateLabel(), new FieldDecoratorBuilder().labelWidth(20).build()));

        main.setH3(++row, 0, 1, proto().preauthorizedPayments().getMeta().getCaption());
        main.getFlexCellFormatter().setColSpan(row, 0, 2);
        main.setWidget(++row, 0, inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentDTO.class, true);
            setOrderable(false);
        }

        @Override
        protected CForm<PreauthorizedPaymentDTO> createItemForm(IObject<?> member) {
            return new PreauthorizedPaymentEditor();
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
        protected void removeItem(final CFolderItem<PreauthorizedPaymentDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Pre-Authorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CForm<PreauthorizedPaymentDTO> {

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().id(), new CNumberLabel(), new FieldDecoratorBuilder(10).build()));

                content.setWidget(++row, 0, inject(proto().creationDate(), new FieldDecoratorBuilder(15).build()));
                content.setWidget(row, 1, inject(proto().createdBy(), new CEntityLabel<AbstractPmcUser>(), new FieldDecoratorBuilder(22).build()));

                content.setWidget(++row, 0, inject(proto().updated(), new FieldDecoratorBuilder(15).build()));

                content.setWidget(++row, 0, inject(proto().paymentMethod(), new CEntitySelectorLabel<LeasePaymentMethod>() {
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
                }, new FieldDecoratorBuilder(35).build()));

                content.setBR(++row, 0, 2);

                content.setWidget(++row, 0, 2, inject(proto().coveredItemsDTO(), new PapCoveredItemDtoFolder()));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                get(proto().id()).setVisible(!getValue().id().isNull());
                get(proto().creationDate()).setVisible(!getValue().creationDate().isNull());
                get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
                get(proto().updated()).setVisible(!getValue().updated().isNull());
            }
        }
    }
}
