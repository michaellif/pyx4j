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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsView.Presenter;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentItemDTO;
import com.propertyvista.portal.rpc.portal.dto.PreauthorizedPaymentListDTO;

public class PreauthorizedPaymentsForm extends CEntityDecoratableForm<PreauthorizedPaymentListDTO> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentsForm.class);

    private PreauthorizedPaymentsView.Presenter presenter;

    public PreauthorizedPaymentsForm() {
        super(PreauthorizedPaymentListDTO.class, new VistaViewersComponentFactory());
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

    private class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPaymentItemDTO> {

        public PreauthorizedPaymentFolder() {
            super(PreauthorizedPaymentItemDTO.class, true);
            setOrderable(false);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PreauthorizedPaymentItemDTO) {
                return new PreauthorizedPaymentEditor();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            presenter.addPreauthorizedPayment();
        }

        @Override
        protected void removeItem(final CEntityFolderItem<PreauthorizedPaymentItemDTO> item) {
            MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Preauthorized Payment?"), new Command() {
                @Override
                public void execute() {
                    presenter.deletePreauthorizedPayment(item.getValue());
                    PreauthorizedPaymentFolder.super.removeItem(item);
                }
            });
        }

        private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPaymentItemDTO> {

            public PreauthorizedPaymentEditor() {
                super(PreauthorizedPaymentItemDTO.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().tenant(), new CEntityLabel<Tenant>()));

                content.setHR(++row, 0, 1);

                content.setWidget(row, 0, inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()));
                get(proto().paymentMethod()).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        presenter.viewPaymentMethod(getValue());
                    }
                });

                content.setWidget(++row, 0, inject(proto().coveredItems(), new CoveredItemFolder()));

                return content;
            }

            private class CoveredItemFolder extends VistaTableFolder<PreauthorizedPayment.CoveredItem> {

                public CoveredItemFolder() {
                    super(PreauthorizedPayment.CoveredItem.class, false);
                }

                @Override
                public List<EntityFolderColumnDescriptor> columns() {
                    return Arrays.asList(//@formatter:off
                            new EntityFolderColumnDescriptor(proto().billableItem(),"40em"),
                            new EntityFolderColumnDescriptor(proto().percent(), "5em"));
                      //@formatter:on                
                }
            }
        }
    }
}
