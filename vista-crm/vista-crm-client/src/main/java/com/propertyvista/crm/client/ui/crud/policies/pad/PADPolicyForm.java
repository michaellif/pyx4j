/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.pad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.policy.dto.PADPolicyDTO;
import com.propertyvista.domain.policy.policies.PADPolicyItem;

public class PADPolicyForm extends PolicyDTOTabPanelBasedForm<PADPolicyDTO> {

    private final static I18n i18n = I18n.get(PADPolicyForm.class);

    public PADPolicyForm(IForm<PADPolicyDTO> view) {
        super(PADPolicyDTO.class, view);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(createItemsPanel());
    }

    private FormFlexPanel createItemsPanel() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Items"));
        int row = -1;

        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeType()), 20).build());
        panel.setWidget(++row, 0, inject(proto().debitBalanceTypes(), new PADPolicyItemEditorFolder()));

        return panel;
    }

    private static class PADPolicyItemEditorFolder extends VistaBoxFolder<PADPolicyItem> {

        public PADPolicyItemEditorFolder() {
            super(PADPolicyItem.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PADPolicyItem) {
                return new PADPolicyItemEditor();
            }
            return super.create(member);
        }

        private static class PADPolicyItemEditor extends CEntityDecoratableForm<PADPolicyItem> {

            public PADPolicyItemEditor() {
                super(PADPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();

                int row = -1;
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().debitType()), 20).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().owingBalanceType()), 20).build());

                // tweaks:
                get(proto().debitType()).inheritViewable(false);
                get(proto().debitType()).setViewable(true);

                return content;
            }
        }

        @Override
        protected void addItem() {
            final List<DebitType> alreadySelectedTypes = new ArrayList<DebitType>();
            for (PADPolicyItem di : getValue()) {
                if (!di.debitType().isNull()) {
                    alreadySelectedTypes.add(di.debitType().getValue());
                }
            }
            Collection<DebitType> selection = new ArrayList<DebitType>(EnumSet.allOf(DebitType.class));
            selection.removeAll(alreadySelectedTypes);

            new SelectEnumDialog<DebitType>(i18n.tr("Select Debit Type"), selection) {
                @Override
                public boolean onClickOk() {
                    PADPolicyItem newItem = EntityFactory.create(PADPolicyItem.class);
                    newItem.debitType().setValue(getSelectedType());
                    PADPolicyItemEditorFolder.this.addItem(newItem);
                    return true;
                }

                @Override
                public String defineWidth() {
                    return "20em";
                }
            }.show();
        }
    }
}
