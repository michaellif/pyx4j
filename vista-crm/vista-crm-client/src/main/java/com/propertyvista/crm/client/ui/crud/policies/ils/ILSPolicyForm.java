/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.ils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityListBox;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CListBox.SelectionMode;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ILSPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.ILSPolicyItem;
import com.propertyvista.domain.policy.policies.domain.ILSPolicyItem.ILSProvider;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class ILSPolicyForm extends PolicyDTOTabPanelBasedForm<ILSPolicyDTO> {

    private final static I18n i18n = I18n.get(ILSPolicyForm.class);

    public ILSPolicyForm(IForm<ILSPolicyDTO> view) {
        super(ILSPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createProvidersPanel());
    }

    private TwoColumnFlexFormPanel createProvidersPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("ILS Providers"));
        int row = -1;

        panel.setWidget(++row, 0, 2, inject(proto().policyItems(), new ILSPolicyItemEditorFolder()));

        return panel;
    }

    private class ILSPolicyItemEditorFolder extends VistaBoxFolder<ILSPolicyItem> {

        public ILSPolicyItemEditorFolder() {
            super(ILSPolicyItem.class);
        }

        @Override
        protected void addItem() {
            // get unused providers
            EnumSet<ILSProvider> values = EnumSet.allOf(ILSProvider.class);
            List<ILSProvider> usedProviders = new ArrayList<ILSProvider>();
            for (ILSPolicyItem item : getValue()) {
                usedProviders.add(item.provider().getValue());
            }
            values.removeAll(usedProviders);
            // show selection dialog
            new SelectEnumDialog<ILSProvider>(i18n.tr("Select ILS Provider"), values) {
                @Override
                public boolean onClickOk() {
                    ILSPolicyItem item = EntityFactory.create(ILSPolicyItem.class);
                    item.provider().setValue(getSelectedType());
                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof ILSPolicyItem) {
                return new ILSPolicyItemEditor();
            } else {
                return super.create(member);
            }
        }

        private class ILSPolicyItemEditor extends CEntityDecoratableForm<ILSPolicyItem> {

            private final CEntityListBox<Province> provinceSelector;

            private final CEntityListBox<City> citySelector;

            public ILSPolicyItemEditor() {
                super(ILSPolicyItem.class);

                provinceSelector = new CEntityListBox<Province>(Province.class, SelectionMode.TWO_PANEL);
                citySelector = new CEntityListBox<City>(City.class, SelectionMode.TWO_PANEL);

                // set province option criteria
                if (!ILSPolicyForm.this.getValue().isNull()) {
                    Set<Country> countries = ILSPolicyForm.this.getValue().countries();
                    if (!countries.isEmpty()) {
                        provinceSelector.addCriterion(PropertyCriterion.in(provinceSelector.proto().country(), countries));
                    }
                }
                // update city option filter
                provinceSelector.addValueChangeHandler(new ValueChangeHandler<List<Province>>() {
                    @Override
                    public void onValueChange(final ValueChangeEvent<List<Province>> event) {
                        citySelector.setOptionsFilter(new OptionsFilter<City>() {
                            @Override
                            public boolean acceptOption(City entity) {
                                return event.getValue().contains(entity.province());
                            }
                        });
                        citySelector.refreshOptions();
                    }
                });
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().provider(), new CEnumLabel()), true).build());
                content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().allowedProvinces(), provinceSelector), true).build());
                content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().allowedCities(), citySelector), true).build());

                provinceSelector.setEmptyValueFormat(i18n.tr("All Available"));
                citySelector.setEmptyValueFormat(i18n.tr("All Available"));

                return content;
            }

            @Override
            protected void onValueSet(boolean populate) {
                final ILSPolicyItem policyItem = getValue();
                if (policyItem == null || policyItem.allowedProvinces().isEmpty()) {
                    citySelector.setOptionsFilter(new OptionsFilter<City>() {
                        @Override
                        public boolean acceptOption(City entity) {
                            return false;
                        }
                    });
                }
            }
        }
    }
}
