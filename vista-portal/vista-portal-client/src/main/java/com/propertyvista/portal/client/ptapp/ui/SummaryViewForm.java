/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.domain.pt.Summary;

@Singleton
public class SummaryViewForm extends BaseEntityForm<Summary> {

    private FlowPanel main;

    public SummaryViewForm() {
        super(Summary.class);
    }

    @Override
    public void createContent() {
        main = new FlowPanel();

        setWidget(main);
    }

    @Override
    public void populate(Summary value) {
        super.populate(value);
        main.add(new ViewHeaderDecorator(new HTML("<h4>Sample: main applicant email: " + getValue().tenants().tenants().get(0).email() + "</h4>")));

    }
}
