/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.dashboard.gadgets.type.base.CounterGadgetBaseMetadata;

public class CounterGadgetSetupForm<E extends CounterGadgetBaseMetadata> extends CEntityForm<E> {

    public CounterGadgetSetupForm(Class<E> clazz) {
        super(clazz);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;
        content.setWidget(++row, 0, inject(proto().refreshInterval(), new FormDecoratorBuilder().build()));
        return content;
    }

}
