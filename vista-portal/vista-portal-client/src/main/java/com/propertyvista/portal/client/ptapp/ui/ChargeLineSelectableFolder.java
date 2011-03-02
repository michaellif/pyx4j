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
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class ChargeLineSelectableFolder extends CEntityFolder<ChargeLineSelectable> {

    @SuppressWarnings("rawtypes")
    final ValueChangeHandler valueChangeHandler;

    ChargeLineSelectableFolder(@SuppressWarnings("rawtypes") ValueChangeHandler valueChangeHandler) {
        this.valueChangeHandler = valueChangeHandler;
    }

    @Override
    protected FolderDecorator<ChargeLineSelectable> createFolderDecorator() {
        return new BoxReadOnlyFolderDecorator<ChargeLineSelectable>() {

            @Override
            public void setFolder(CEntityFolder<?> w) {
                super.setFolder(w);
                this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            }
        };
    }

    @Override
    protected CEntityFolderItem<ChargeLineSelectable> createItem() {

        return new CEntityFolderItem<ChargeLineSelectable>(ChargeLineSelectable.class) {

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                return new BoxReadOnlyFolderItemDecorator(!isFirst());
            }

            @SuppressWarnings("unchecked")
            @Override
            public IsWidget createContent() {

                FlowPanel main = new FlowPanel();

                String width = "60%";
                if (valueChangeHandler != null) {
                    main.add(DecorationUtils.inline(inject(proto().selected()), "3%"));
                    CEditableComponent<Boolean, ?> cb = get(proto().selected());
                    if (cb instanceof CCheckBox) {
                        cb.addValueChangeHandler(valueChangeHandler);

                        //TODO this is hack for Misha to fix.
                        cb.asWidget().setStyleName(null);
                    }
                    width = "57%";
                }

                main.add(DecorationUtils.inline(inject(proto().type()), width));
                main.add(DecorationUtils.inline(inject(proto().charge()), "10%", "right"));

                return main;
            }
        };
    }
}
