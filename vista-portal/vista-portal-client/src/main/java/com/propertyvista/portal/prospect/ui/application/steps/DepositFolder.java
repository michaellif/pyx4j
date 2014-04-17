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

import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class DepositFolder extends PortalBoxFolder<Deposit> {

    public DepositFolder() {
        super(Deposit.class, false);
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof Deposit) {
            return (T) new DepositEditor();
        }
        return super.create(member);
    }

    private class DepositEditor extends CEntityForm<Deposit> {

        public DepositEditor() {
            super(Deposit.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, inject(proto().description(), new CLabel<String>(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().amount(), new CMoneyLabel(), new FieldDecoratorBuilder().build()));

            return content;
        }
    }
}