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

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class FeatureReadOnlyFolder extends PortalBoxFolder<BillableItem> {

    public FeatureReadOnlyFolder() {
        super(BillableItem.class, false);
        setNoDataNotificationWidget(null);
    }

    @Override
    protected CForm<BillableItem> createItemForm(IObject<?> member) {
        return new FeatureItemViewer();
    }

    @Override
    public BoxFolderItemDecorator<BillableItem> createItemDecorator() {
        BoxFolderItemDecorator<BillableItem> decor = super.createItemDecorator();
        decor.setCaptionFormatter(new IFormatter<BillableItem, String>() {
            @Override
            public String format(BillableItem value) {
                return SimpleMessageFormat.format("{0}, Rent ${1}", (value.description().isNull() ? value.item().name() : value.description()),
                        value.agreedPrice());
            }
        });

        return decor;
    }

    class FeatureItemViewer extends CForm<BillableItem> {

        public FeatureItemViewer() {
            super(BillableItem.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().item().name(), new CLabel<String>()).decorate();
            formPanel.append(Location.Left, proto().agreedPrice(), new CMoneyLabel()).decorate();
            formPanel.append(Location.Left, proto().description(), new CLabel<String>()).decorate();

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().description()).setVisible(!getValue().description().isNull());
        }
    }
}