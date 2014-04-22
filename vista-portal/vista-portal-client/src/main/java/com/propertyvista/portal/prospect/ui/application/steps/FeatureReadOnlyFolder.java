/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 21, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class FeatureReadOnlyFolder extends PortalBoxFolder<BillableItem> {

    public FeatureReadOnlyFolder() {
        super(BillableItem.class, false);
        setNoDataNotificationWidget(null);
    }

    @Override
    protected CForm<BillableItem> createItemForm(IObject<?> member) {
        return new FeatureItemViewer();
    }

    class FeatureItemViewer extends CForm<BillableItem> {

        public FeatureItemViewer() {
            super(BillableItem.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, inject(proto().item().name(), new CLabel<String>(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().agreedPrice(), new CMoneyLabel(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().description(), new CLabel<String>(), new FieldDecoratorBuilder().build()));

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().description()).setVisible(!getValue().description().isNull());
        }
    }
}