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
package com.propertyvista.common.client.ui.components.folders;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;

public abstract class PreauthorizedPaymentFolder extends VistaBoxFolder<PreauthorizedPayment> {

    private static final I18n i18n = I18n.get(PreauthorizedPaymentFolder.class);

    public PreauthorizedPaymentFolder() {
        super(PreauthorizedPayment.class);
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PreauthorizedPayment) {
            return new PreauthorizedPaymentEditor();
        }
        return super.create(member);
    }

    @Override
    protected void removeItem(final CEntityFolderItem<PreauthorizedPayment> item) {
        MessageDialog.confirm(i18n.tr("Please confirm"), i18n.tr("Do you really want to delete the Preauthorized Payment?"), new Command() {
            @Override
            public void execute() {
                PreauthorizedPaymentFolder.super.removeItem(item);
            }
        });
    }

    /**
     * Implement in caller to get PaymentMethods to select.
     * 
     * @return list of the PaymentMethods.
     */
    public abstract List<LeasePaymentMethod> getAvailablePaymentMethods();

    private class PreauthorizedPaymentEditor extends CEntityDecoratableForm<PreauthorizedPayment> {

        public PreauthorizedPaymentEditor() {
            super(PreauthorizedPayment.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;

            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentMethod(), new CEntitySelectorHyperlink<LeasePaymentMethod>() {
                @Override
                protected AppPlace getTargetPlace() {
                    return null; // not intended to navigate - just edit mode!
                }

                @Override
                protected AbstractEntitySelectorDialog<LeasePaymentMethod> getSelectorDialog() {
                    return new EntitySelectorListDialog<LeasePaymentMethod>(i18n.tr("Select Payment Method"), false, getAvailablePaymentMethods()) {
                        @Override
                        public boolean onClickOk() {
                            get(proto().paymentMethod()).setValue(getSelectedItems().iterator().next());
                            return true;
                        }
                    };
                }
            }), 38, 10).build());

            content.setBR(++row, 0, 1);

            content.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder(isEditable())));

            return content;
        }
    }
}
