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
package com.propertyvista.portal.ptapp.client.ui.steps;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.ptapp.ChargeLineSelectable;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;

public class ChargeLineSelectableFolder extends CEntityFolderEditor<ChargeLineSelectable> {

    private final boolean summaryViewMode;

    ChargeLineSelectableFolder(boolean summaryViewMode) {
        super(ChargeLineSelectable.class);
        this.summaryViewMode = summaryViewMode;
    }

    @Override
    protected IFolderEditorDecorator<ChargeLineSelectable> createFolderDecorator() {
        return new BoxReadOnlyFolderDecorator<ChargeLineSelectable>() {

            @Override
            public void setFolder(CEntityFolderEditor<?> w) {
                super.setFolder(w);
                this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            }
        };
    }

    @Override
    protected CEntityFolderItemEditor<ChargeLineSelectable> createItem() {

        return new CEntityFolderItemEditor<ChargeLineSelectable>(ChargeLineSelectable.class) {

            @Override
            public IFolderItemEditorDecorator createFolderItemDecorator() {
                return new BoxReadOnlyFolderItemDecorator(!isFirst(), "400px");
            }

            @SuppressWarnings("unchecked")
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
