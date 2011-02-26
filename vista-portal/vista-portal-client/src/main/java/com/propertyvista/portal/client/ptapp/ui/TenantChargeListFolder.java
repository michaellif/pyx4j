/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.pt.TenantCharge;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;

public class TenantChargeListFolder extends CEntityFolder<TenantCharge> {

    final CEntityForm<?> masterForm;

    @SuppressWarnings("rawtypes")
    final ValueChangeHandler valueChangeHandler;

    @SuppressWarnings("rawtypes")
    TenantChargeListFolder(CEntityForm<?> masterForm, ValueChangeHandler valueChangeHandler) {
        this.valueChangeHandler = valueChangeHandler;
        this.masterForm = masterForm;
    }

    @Override
    protected FolderDecorator<TenantCharge> createFolderDecorator() {
        return new BoxReadOnlyFolderDecorator<TenantCharge>();
    }

    @Override
    protected CEntityFolderItem<TenantCharge> createItem() {

        return new CEntityFolderItem<TenantCharge>(TenantCharge.class) {

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                return new BoxReadOnlyFolderItemDecorator(true);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void createContent() {

                FlowPanel main = new FlowPanel();
                main.add(DecorationUtils.inline(masterForm.create(proto().tenant(), this), "55%", null));
                main.add(DecorationUtils.inline(new HTML("%"), "5%", "right"));
                main.add(DecorationUtils.inline(masterForm.create(proto().percentage(), this), "5%", "right"));
                main.add(DecorationUtils.inline(masterForm.create(proto().charge(), this), "5%", "right"));
                get(proto().percentage()).addValueChangeHandler(valueChangeHandler);
                setWidget(main);
            }
        };
    }
}
