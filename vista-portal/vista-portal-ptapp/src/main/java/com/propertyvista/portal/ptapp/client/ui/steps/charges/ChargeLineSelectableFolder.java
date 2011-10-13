/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 27, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.charges;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.folder.BoxReadOnlyFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderBoxEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.domain.charges.ChargeLineSelectable;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;

public class ChargeLineSelectableFolder extends CEntityFolder<ChargeLineSelectable> {

    private final boolean summaryViewMode;

    ChargeLineSelectableFolder(boolean summaryViewMode) {
        super(ChargeLineSelectable.class);
        this.summaryViewMode = summaryViewMode;
    }

    @Override
    protected IFolderDecorator<ChargeLineSelectable> createDecorator() {
        return new BoxReadOnlyFolderDecorator<ChargeLineSelectable>() {

            @Override
            public void setComponent(CEntityFolder w) {
                super.setComponent(w);
                this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            }
        };
    }

    @Override
    protected CEntityFolderBoxEditor<ChargeLineSelectable> createItem() {

        return new CEntityFolderBoxEditor<ChargeLineSelectable>(ChargeLineSelectable.class) {

            @Override
            public IFolderItemDecorator<ChargeLineSelectable> createDecorator() {
                return new BoxReadOnlyFolderItemDecorator<ChargeLineSelectable>();
            }

            @Override
            public IsWidget createContent() {

                FlowPanel main = new FlowPanel();

                String width = "300px";
                if (!summaryViewMode) {
                    main.add(DecorationUtils.inline(inject(proto().selected()), "25px"));
                    CEditableComponent<Boolean, ?> cb = get(proto().selected());
                    if (cb instanceof CCheckBox) {
                        //TODO this is hack for Misha to fix.
                        cb.asWidget().setStyleName(null);
                    }
                    width = "275px";
                }

                main.add(DecorationUtils.inline(inject(proto().label()), width));
                main.add(DecorationUtils.inline(inject(proto().charge()), "100px", "right"));

                return main;
            }
        };
    }
}
