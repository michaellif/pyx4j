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
package com.propertyvista.portal.ptapp.client.ui.steps.charges;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderBoxEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;

import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;

public class ChargeLineFolder extends CEntityFolder<ChargeLine> {

    public ChargeLineFolder() {
        super(ChargeLine.class);
    }

    @Override
    protected IFolderDecorator<ChargeLine> createDecorator() {
        return new BoxReadOnlyFolderDecorator<ChargeLine>() {

            @Override
            public void setComponent(CEntityFolder w) {
                super.setComponent(w);
                this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            }
        };
    }

    @Override
    protected CEntityFolderBoxEditor<ChargeLine> createItem() {

        return new CEntityFolderBoxEditor<ChargeLine>(ChargeLine.class) {

            @Override
            public IFolderItemDecorator<ChargeLine> createDecorator() {
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
