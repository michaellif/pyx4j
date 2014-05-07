/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class FeaturesFolder extends PortalBoxFolder<BillableItem> {

    public FeaturesFolder() {
        super(BillableItem.class, false);
    }

    @Override
    protected CForm<BillableItem> createItemForm(IObject<?> member) {
        return new FeatureForm();
    }

    class FeatureForm extends CForm<BillableItem> {

        public FeatureForm() {
            super(BillableItem.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0, inject(proto().item(), new CEntityLabel<ProductItem>(), new FieldDecoratorBuilder().build()));
            mainPanel.setWidget(++row, 0, inject(proto().agreedPrice(), new CMoneyLabel(), new FieldDecoratorBuilder().build()));
            mainPanel.setWidget(++row, 0, inject(proto().description(), new CLabel<String>(), new FieldDecoratorBuilder().build()));

            return mainPanel;
        }
    }
}
