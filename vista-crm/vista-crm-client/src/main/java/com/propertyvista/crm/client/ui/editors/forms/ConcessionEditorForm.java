/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors.forms;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.domain.marketing.yield.Concession;

public class ConcessionEditorForm extends CrmEntityForm<Concession> {

    public ConcessionEditorForm() {
        super(Concession.class, new CrmEditorsComponentFactory());
    }

    public ConcessionEditorForm(IEditableComponentFactory factory) {
        super(Concession.class, factory);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        Widget header = new CrmHeaderDecorator(i18n.tr("Information"));
        header.getElement().getStyle().setMarginTop(0, Unit.EM);
        main.add(header);

        main.add(inject(proto().type()), 15);
        main.add(inject(proto().value()), 7);
        main.add(inject(proto().percentage()), 7);
        main.add(inject(proto().appliedTo()), 15);
        main.add(inject(proto().termType()), 15);
        main.add(inject(proto().numberOfTerms()), 7);
        main.add(inject(proto().description()), 15);
        main.add(inject(proto().status()), 15);
        main.add(inject(proto().approvedBy()), 15);
        main.add(inject(proto().start()), 8);
        main.add(inject(proto().end()), 8);

        main.setWidth("100%");
        return main;
    }
}
