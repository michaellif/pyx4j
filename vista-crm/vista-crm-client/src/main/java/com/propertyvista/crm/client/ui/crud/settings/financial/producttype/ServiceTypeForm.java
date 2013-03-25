/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.financial.producttype;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.components.boxes.GlCodeSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceTypeForm extends CrmEntityForm<ServiceItemType> implements HasYardiIntegrationMode {

    private static final I18n i18n = I18n.get(ServiceTypeForm.class);

    private ProductItemTypeYardiIntegrationForm yardiIntegrationMixin;

    public ServiceTypeForm(IForm<ServiceItemType> view) {
        super(ServiceItemType.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().serviceType()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().glCode(), new CEntitySelectorHyperlink<GlCode>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(GlCode.class).formViewerPlace(getValue().glCodeCategory().getPrimaryKey());
            }

            @Override
            protected EntitySelectorTableDialog<GlCode> getSelectorDialog() {
                return new GlCodeSelectorDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            get(ServiceTypeForm.this.proto().glCode()).setValue(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                };
            }
        }), 25).build());

        content.setWidget(++row, 0, yardiIntegrationMixin = new ProductItemTypeYardiIntegrationForm());
        yardiIntegrationMixin.bind(this);

        selectTab(addTab(content));

    }

    @Override
    public void setYardiIntegrationModeEnabled(boolean enabled) {
        yardiIntegrationMixin.setYardiIntegrationModeEnabled(enabled);
        get(proto().glCode()).setVisible(!enabled);
    }
}