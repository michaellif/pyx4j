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
package com.propertyvista.portal.ptapp.client.ui.steps;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;

public class ChargeLineFolder extends CEntityFolderEditor<ChargeLine> {

    public ChargeLineFolder() {
        super(ChargeLine.class);
    }

    @Override
    protected IFolderEditorDecorator<ChargeLine> createFolderDecorator() {
        return new BoxReadOnlyFolderDecorator<ChargeLine>() {

            @Override
            public void setFolder(CEntityFolderEditor<?> w) {
                super.setFolder(w);
                this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            }
        };
    }

    @Override
    protected CEntityFolderItemEditor<ChargeLine> createItem() {

        return new CEntityFolderItemEditor<ChargeLine>(ChargeLine.class) {

            @Override
            public IFolderItemEditorDecorator<ChargeLine> createFolderItemDecorator() {
                return new BoxReadOnlyFolderItemDecorator<ChargeLine>(!isFirst(), "400px");
            }

            @Override
            public IsWidget createContent() {
                FlowPanel main = new FlowPanel();
                main.add(DecorationUtils.inline(inject(proto().label()), "300px", null));
                main.add(DecorationUtils.inline(inject(proto().charge()), "100px", "right"));
                return main;
            }
        };
    }

}
