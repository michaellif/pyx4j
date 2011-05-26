/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.editors.CrmEditorsComponentFactory;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorEditorForm extends CrmEntityForm<ElevatorDTO> {

    public ElevatorEditorForm() {
        super(ElevatorDTO.class, new CrmEditorsComponentFactory());
    }

    public ElevatorEditorForm(IEditableComponentFactory factory) {
        super(ElevatorDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        // TODO Auto-generated method stub
        return null;
    }
}