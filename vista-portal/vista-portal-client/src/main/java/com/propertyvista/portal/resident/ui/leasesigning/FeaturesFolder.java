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
package com.propertyvista.portal.resident.ui.leasesigning;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class FeaturesFolder extends PortalBoxFolder<BillableItem> {

    private static final I18n i18n = I18n.get(FeaturesFolder.class);

    public FeaturesFolder() {
        super(BillableItem.class, false);
    }

    @Override
    protected CForm<BillableItem> createItemForm(IObject<?> member) {
        return new FeatureForm();
    }

    @Override
    public BoxFolderItemDecorator<BillableItem> createItemDecorator() {
        BoxFolderItemDecorator<BillableItem> decor = super.createItemDecorator();

        decor.setCaptionFormatter(new IFormatter<BillableItem, SafeHtml>() {
            @Override
            public SafeHtml format(BillableItem value) {
                return SafeHtmlUtils.fromString(SimpleMessageFormat.format("{0}, Rent: ${1}",
                        (value.description().isNull() ? value.item().name() : value.description()), value.agreedPrice()));
            }
        });

        return decor;
    }

    private class FeatureForm extends CForm<BillableItem> {

        public FeatureForm() {
            super(BillableItem.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().item().name(), new CLabel<String>()).decorate();
            formPanel.append(Location.Left, proto().agreedPrice(), new CMoneyLabel()).decorate().customLabel(i18n.tr("Price"));
            formPanel.append(Location.Left, proto().description(), new CLabel<String>()).decorate();

            return formPanel;
        }
    }
}
