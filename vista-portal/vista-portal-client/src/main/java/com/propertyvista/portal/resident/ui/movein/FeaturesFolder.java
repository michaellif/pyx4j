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

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class FeaturesFolder extends PortalBoxFolder<BillableItem> {

    public FeaturesFolder() {
        super(BillableItem.class, null);
        setOrderable(false);
        setAddable(false);
        setRemovable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof BillableItem) {
            return new FeatureForm();
        } else {
            return super.create(member);
        }
    }

    class FeatureForm extends CEntityForm<BillableItem> {

        public FeatureForm() {
            super(BillableItem.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description(), new CLabel<String>())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().agreedPrice(), new CLabel<BigDecimal>())).build());

            return mainPanel;
        }
    }
}
