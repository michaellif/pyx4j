/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.domain.pt.UnitSelection;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;
import com.pyx4j.widgets.client.Button;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    public ApartmentViewForm() {
        super(UnitSelection.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        HTML caption = new HTML("<h4>Available Units</h4>");
        caption.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        main.add(caption);
        main.add(new BasicWidgetDecorator(create(proto().availableFrom(), this), 40, 100));
        main.add(new BasicWidgetDecorator(create(proto().availableTo(), this), 40, 100));
        main.add(new Button("Change"));
        main.add(new HTML());
        main.add(new HTML("<h4>Lease Terms</h4>"));
        main.add(new HTML("<h4>Desired Move In Date</h4>"));
        main.add(new BasicWidgetDecorator(create(proto().moveInDate(), this), 40, 100));

        setWidget(main);
    }

}
