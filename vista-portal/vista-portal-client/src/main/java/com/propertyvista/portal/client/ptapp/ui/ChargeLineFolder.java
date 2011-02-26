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
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.pt.ChargeLine;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.forms.client.ui.CCheckBox;

public class ChargeLineFolder extends CEntityFolder<ChargeLine> {

    final CEntityForm<?> masterForm;

    final ValueChangeHandler<Boolean> valueChangeHandler;

    ChargeLineFolder(CEntityForm<?> masterForm, ValueChangeHandler<Boolean> valueChangeHandler) {
        this.valueChangeHandler = valueChangeHandler;
        this.masterForm = masterForm;
    }

    @Override
    protected FolderDecorator<ChargeLine> createFolderDecorator() {
        return new BoxReadOnlyFolderDecorator<ChargeLine>();
    }

    @Override
    protected CEntityFolderItem<ChargeLine> createItem() {

        return new CEntityFolderItem<ChargeLine>(ChargeLine.class) {

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                return new BoxReadOnlyFolderItemDecorator(true);
            }

            @Override
            public void createContent() {

                FlowPanel main = new FlowPanel();

                String width = "60%";
                if (valueChangeHandler != null) {
                    CCheckBox cb = (CCheckBox) masterForm.create(proto().selected(), this);
                    //                        cb.addValueChangeHandler(valueChangeHandler);
                    //TODO this is hack for Misha to fix.
                    cb.asWidget().setStyleName(null);
                    main.add(DecorationUtils.inline(cb, "5%", null));
                    width = "55%";
                }

                main.add(DecorationUtils.inline(masterForm.create(proto().type(), this), width, null));
                main.add(DecorationUtils.inline(masterForm.create(proto().charge(), this), "10%", "right"));

                setWidget(main);
            }
        };
    }

}
