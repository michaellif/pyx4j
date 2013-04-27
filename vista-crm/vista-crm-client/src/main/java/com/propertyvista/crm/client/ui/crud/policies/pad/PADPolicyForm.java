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
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.crm.rpc.services.selections.SelectProductCodeListService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.PADPolicyDTO;
import com.propertyvista.domain.policy.policies.PADCreditPolicyItem;
import com.propertyvista.domain.policy.policies.PADDebitPolicyItem;
import com.propertyvista.domain.policy.policies.PADPolicy.PADChargeType;

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
        FormFlexPanel leftPanel = new FormFlexPanel();
        leftPanel.setWidth("98%");
        int row = -1;
        leftPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeType()), 20).build());
        leftPanel.setWidget(++row, 0, inject(proto().debitBalanceTypes(), new PADDebitPolicyItemEditorFolder()));
        get(proto().chargeType()).addValueChangeHandler(new ValueChangeHandler<PADChargeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<PADChargeType> event) {
                get(proto().debitBalanceTypes()).setVisible(!PADChargeType.FixedAmount.equals(event.getValue()));
            }
        });
        FormFlexPanel rightPanel = new FormFlexPanel();
        rightPanel.setWidth("98%");
        row = -1;
        rightPanel.setWidget(++row, 0, inject(proto().creditBalanceTypes(), new PADCreditPolicyItemEditorFolder()));

        panel.setH1(0, 0, 1, i18n.tr("Debits"));
        panel.setWidget(1, 0, leftPanel);
        panel.setH1(0, 1, 1, i18n.tr("Credits"));
        panel.setWidget(1, 1, rightPanel);
        panel.getColumnFormatter().setWidth(0, "50%");
        panel.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        panel.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().debitBalanceTypes()).setVisible(getValue().isNull() || !PADChargeType.FixedAmount.equals(getValue().chargeType().getValue()));
    }

    private static class PADCreditPolicyItemEditorFolder extends VistaBoxFolder<PADCreditPolicyItem> {

        public PADCreditPolicyItemEditorFolder() {
            super(PADCreditPolicyItem.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PADCreditPolicyItem) {
                return new PADCreditPolicyItemEditor();
            }
            return super.create(member);
        }

        private static class PADCreditPolicyItemEditor extends CEntityDecoratableForm<PADCreditPolicyItem> {

            public PADCreditPolicyItemEditor() {
                super(PADCreditPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();

                content.setWidget(0, 0, new DecoratorBuilder(inject(proto().arCode()), 20).build());
                return content;
            }
        }
    }

    private static class PADDebitPolicyItemEditorFolder extends VistaBoxFolder<PADDebitPolicyItem> {

        public PADDebitPolicyItemEditorFolder() {
            super(PADDebitPolicyItem.class);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof PADDebitPolicyItem) {
                return new PADDebitPolicyItemEditor();
            }
            return super.create(member);
        }

        private static class PADDebitPolicyItemEditor extends CEntityDecoratableForm<PADDebitPolicyItem> {

            public PADDebitPolicyItemEditor() {
                super(PADDebitPolicyItem.class);
            }

            @Override
            public IsWidget createContent() {
                FormFlexPanel content = new FormFlexPanel();

                int row = -1;
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().arCode()), 20).build());
                content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().owingBalanceType()), 20).build());

                // tweaks:
                get(proto().arCode()).inheritViewable(false);
                get(proto().arCode()).setViewable(true);

                return content;
            }
        }

        @Override
        protected void addItem() {
            final List<ARCode> alreadySelectedTypes = new ArrayList<ARCode>();
            for (PADDebitPolicyItem di : getValue()) {
                if (!di.arCode().isNull()) {
                    alreadySelectedTypes.add(di.arCode());
                }
            }

            new EntitySelectorTableDialog<ARCode>(ARCode.class, false, alreadySelectedTypes, i18n.tr("Select Debit Type")) {
                @Override
                public boolean onClickOk() {
                    PADDebitPolicyItem newItem = EntityFactory.create(PADDebitPolicyItem.class);
                    newItem.arCode().set(getSelectedItems().get(0));
                    PADDebitPolicyItemEditorFolder.this.addItem(newItem);
                    return true;
                }

                @Override
                protected List<ColumnDescriptor> defineColumnDescriptors() {
                    return Arrays.asList(//@formatter:off
                            new MemberColumnDescriptor.Builder(proto().name(), true).build(),
                            new MemberColumnDescriptor.Builder(proto().type(), true).build()
                    ); //@formatter:on
                }

                @Override
                public List<Sort> getDefaultSorting() {
                    return Arrays.asList(new Sort(proto().type().getPath().toString(), false), new Sort(proto().name().getPath().toString(), false));
                }

                @Override
                protected AbstractListService<ARCode> getSelectService() {
                    return GWT.<AbstractListService<ARCode>> create(SelectProductCodeListService.class);
                }
            }.show();
        }
    }
}
