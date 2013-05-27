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
package com.propertyvista.portal.client.ui.residents.payment.autopay;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsView.Presenter;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO;

public class PreauthorizedPaymentsForm extends CEntityDecoratableForm<PreauthorizedPaymentListDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private PreauthorizedPaymentsView.Presenter presenter;

    public PreauthorizedPaymentsForm() {
        super(PreauthorizedPaymentListDTO.class, new VistaViewersComponentFactory());
        setViewable(true);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        VerticalPanel container = new VerticalPanel();

        container.add(new DecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel()), 10).labelWidth(25).build());
        container.add(new HTML("&nbsp"));
        container.add(inject(proto().preauthorizedPayments(), new PreauthorizedPaymentFolder()));

        return container;
    }

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentListDTO.ListItemDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentListDTO.ListItemDTO.class, true);
            setOrderable(false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPaymentListDTO.ListItemDTO) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPreauthorizedPayment();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PreauthorizedPaymentListDTO.ListItemDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Preauthorized Payment?"), new Command() {
                @Override
                public void execute() {
                    PreauthorizedPaymentFolder.super.removeItem(item);
                    presenter.deletePreauthorizedPayment(item.getValue());
                }
            });
        }

        @Override
        public IFolderItemDecorator<PreauthorizedPaymentListDTO.ListItemDTO> createItemDecorator() {
            return new VistaBoxFolderItemDecorator<PreauthorizedPaymentListDTO.ListItemDTO>() {
                @Override
                public void setComponent(final CEntityFolderItem<PreauthorizedPaymentListDTO.ListItemDTO> folderItem) {
                    super.setComponent(folderItem);

                    final PreauthorizedPaymentEditor editor = (PreauthorizedPaymentEditor) getContent();
                    editor.addPropertyChangeHandler(new PropertyChangeHandler() {
                        @Override
                        public void onPropertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName() == PropertyName.repopulated) {

                                boolean isCurrentTenant = folderItem.getValue().tenant().customer().user().getPrimaryKey()
                                        .equals(ClientContext.getUserVisit().getPrincipalPrimaryKey());

                                folderItem.getItemActionsBar().setDefaultActionsState(isCurrentTenant, false, false);
                            }
                        }
                    });
                }
            };
        }

        private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPaymentListDTO.ListItemDTO> {

            private final FormFlexPanel expirationWarning = new FormFlexPanel();

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentListDTO.ListItemDTO.class);

                setViewable(true);
                inheritViewable(false);

                Widget expirationWarningLabel = new HTML(i18n.tr("This Preauthorized Payment is expired - needs to be replaced with new one!"));
                expirationWarningLabel.setStyleName(VistaTheme.StyleName.warningMessage.name());
                expirationWarning.setWidget(0, 0, expirationWarningLabel);
                expirationWarning.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
                expirationWarning.setHR(1, 0, 1);
                expirationWarning.setBR(2, 0, 1);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, expirationWarning);

                content.setWidget(++row, 0, inject(proto().tenant(), new CEntityLabel<Tenant>()));

                content.setHR(++row, 0, 1);

                content.setWidget(row, 0, inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()));
                get(proto().paymentMethod()).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        presenter.viewPaymentMethod(getValue());
                    }
                });

                content.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);

                setEditable(getValue().expiring().isNull());
                expirationWarning.setVisible(!getValue().expiring().isNull());
            }
        }
    }
}
