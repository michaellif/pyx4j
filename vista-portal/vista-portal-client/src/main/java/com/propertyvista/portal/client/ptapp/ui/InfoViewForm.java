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
import com.google.inject.Singleton;
import com.propertyvista.portal.domain.pt.PotentialTenant;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;

@Singleton
public class InfoViewForm extends CEntityForm<PotentialTenant> {

    public InfoViewForm() {
        super(PotentialTenant.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(new BasicWidgetDecorator(create(proto().firstName(), this)));
        main.add(new BasicWidgetDecorator(create(proto().middleName(), this)));
        main.add(new BasicWidgetDecorator(create(proto().lastName(), this)));
        main.add(new BasicWidgetDecorator(create(proto().homePhone(), this)));
        main.add(new BasicWidgetDecorator(create(proto().mobilePhone(), this)));
        main.add(new BasicWidgetDecorator(create(proto().email(), this)));
        setWidget(main);
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        return super.createMemberEditor(member);
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        return super.createMemberFolderEditor(member);
    }

}
