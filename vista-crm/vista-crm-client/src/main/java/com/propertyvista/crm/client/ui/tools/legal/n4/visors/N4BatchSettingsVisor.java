/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.n4.visors;

import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.visor.AbstractVisorForm;
import com.pyx4j.site.client.ui.visor.IVisorEditor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.tools.legal.n4.forms.N4BatchRequestForm;
import com.propertyvista.crm.rpc.dto.legal.n4.N4BatchRequestDTO;
import com.propertyvista.domain.company.Employee;

public class N4BatchSettingsVisor extends AbstractVisorForm<N4BatchRequestDTO> {

    private static final I18n i18n = I18n.get(N4BatchSettingsVisor.class);

    public N4BatchSettingsVisor(com.pyx4j.site.client.ui.visor.IVisorEditor.Controller controller) {
        super(controller);
        setForm(new N4BatchRequestForm());
        Button createBatch = new Button(i18n.tr("Create Batch"), new Command() {
            @Override
            public void execute() {
                getForm().setUnconditionalValidationErrorRendering(true);
                if (getForm().isValid()) {
                    getController().save();
                }
            }
        });
        addFooterToolbarItem(createBatch);

        Button cancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                getController().hide();
            }
        });
        addFooterToolbarItem(cancel);
    }

    public void setAgents(List<Employee> agents) {
        ((N4BatchRequestForm) getForm()).setAgents(agents);
    }

    @Override
    public IVisorEditor.Controller getController() {
        return (IVisorEditor.Controller) super.getController();
    }

}
