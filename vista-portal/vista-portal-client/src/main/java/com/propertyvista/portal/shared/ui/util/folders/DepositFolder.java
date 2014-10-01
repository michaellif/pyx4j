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
package com.propertyvista.portal.shared.ui.util.folders;

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

import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class DepositFolder extends PortalBoxFolder<Deposit> {

    public DepositFolder() {
        super(Deposit.class, false);
    }

    @Override
    public BoxFolderItemDecorator<Deposit> createItemDecorator() {
        BoxFolderItemDecorator<Deposit> decor = super.createItemDecorator();

        decor.setCaptionFormatter(new IFormatter<Deposit, SafeHtml>() {
            @Override
            public SafeHtml format(Deposit value) {
                return SafeHtmlUtils.fromString(SimpleMessageFormat.format("{0}: ${1}", value.type(), value.amount()));
            }
        });

        return decor;
    }

    @Override
    protected CForm<Deposit> createItemForm(IObject<?> member) {
        return new DepositEditor();
    }

    private class DepositEditor extends CForm<Deposit> {

        public DepositEditor() {
            super(Deposit.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().description(), new CLabel<String>()).decorate();
            formPanel.append(Location.Left, proto().amount(), new CMoneyLabel()).decorate();

            return formPanel;
        }
    }
}