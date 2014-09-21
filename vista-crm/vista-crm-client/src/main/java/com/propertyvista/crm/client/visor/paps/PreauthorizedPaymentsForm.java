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
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorLabel;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
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
        FormPanel formPanel = new FormPanel(this);
        formPanel.br();
        formPanel.append(Location.Left, proto().tenantInfo(), new CEntityLabel<PreauthorizedPaymentsDTO.TenantInfo>()).decorate();
        get(proto().tenantInfo()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        get(proto().tenantInfo()).asWidget().getElement().getStyle().setFontSize(1.2, Unit.EM);

        formPanel.append(Location.Right, proto().nextPaymentDate(), new CDateLabel()).decorate().labelWidth(200);

        formPanel.h3(proto().preauthorizedPayments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().preauthorizedPayments(), new PreauthorizedPaymentFolder());
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        this.setEditable(SecurityController.check(getValue(), DataModelPermission.permissionUpdate(PreauthorizedPaymentsDTO.class)));
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentDTO.class, true);
            setOrderable(false);
            setNoDataLabel(i18n.tr("No AutoPay payments are setup"));
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
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().creationDate()).decorate().componentWidth(180);
                formPanel.append(Location.Right, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate().componentWidth(200);
                formPanel.append(Location.Left, proto().updated()).decorate().componentWidth(180);
                formPanel.append(Location.Left, proto().paymentMethod(), new CEntitySelectorLabel<LeasePaymentMethod>() {
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
                }).decorate().componentWidth(300);

                formPanel.br();
                formPanel.append(Location.Dual, proto().coveredItemsDTO(), new PapCoveredItemDtoFolder());

                return formPanel;
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
