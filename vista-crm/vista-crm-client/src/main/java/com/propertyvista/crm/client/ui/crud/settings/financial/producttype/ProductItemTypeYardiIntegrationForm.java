/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.financial.producttype;

import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.ProductItemType;

public class ProductItemTypeYardiIntegrationForm extends Composite implements HasYardiIntegrationMode {

    private static final I18n i18n = I18n.get(ProductItemTypeYardiIntegrationForm.class);

    private FormFlexPanel yardiIntegrationPanel;

    private YardiChargeCodeFolder yardiChargeFolder;

    public ProductItemTypeYardiIntegrationForm() {
        initWidget(yardiIntegrationPanel = new FormFlexPanel());
    }

    public <P extends ProductItemType, F extends CEntityForm<P>> void bind(F form) {
        assert yardiChargeFolder == null : "already bound";
        int yrow = -1;
        yardiIntegrationPanel.setH1(++yrow, 0, 1, i18n.tr("Yardi Integration"));
        yardiIntegrationPanel.setWidget(++yrow, 0, form.inject(form.proto().yardiChargeCodes(), yardiChargeFolder = new YardiChargeCodeFolder()));
    }

    @Override
    public void setYardiIntegrationModeEnabled(boolean enabled) {
        yardiChargeFolder.setVisible(enabled); // this is to disable validation
        setVisible(enabled);
    }
}
