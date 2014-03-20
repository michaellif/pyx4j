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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class FeatureReadOnlyFolder extends PortalBoxFolder<BillableItem> {

    private static final I18n i18n = I18n.get(FeatureReadOnlyFolder.class);

    public FeatureReadOnlyFolder() {
        super(BillableItem.class, false);
        setNoDataNotificationWidget(null);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof BillableItem) {
            return new FeatureItemViewer();
        }
        return super.create(member);
    }

    class FeatureItemViewer extends CEntityForm<BillableItem> {

        public FeatureItemViewer() {
            super(BillableItem.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().item().name(), new CLabel<String>())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().agreedPrice(), new CMoneyLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().description(), new CLabel<String>())).build());

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().description()).setVisible(!getValue().description().isNull());
        }
    }
}