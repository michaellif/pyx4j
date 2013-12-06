/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.selectors;

import java.text.ParseException;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;

public class CBuildingSelector extends CComponent<IList<BuildingForSelectionDTO>> {

    private final BuildingSelector buildingSelector;

    private IList<BuildingForSelectionDTO> list;

    public CBuildingSelector() {
        buildingSelector = new BuildingSelector();
    }

    @Override
    public Widget asWidget() {
        return buildingSelector;
    }

    @Override
    protected void setDebugId(IDebugId debugId) {
        // TODO Auto-generated method stub        
    }

    @Override
    protected void setEditorValue(IList<BuildingForSelectionDTO> list) {
        this.list = list;
        for (BuildingForSelectionDTO item : list) {
            buildingSelector.addItem(item);
        }
    }

    @Override
    protected IList<BuildingForSelectionDTO> getEditorValue() throws ParseException {
        this.list.clear();
        for (BuildingForSelectionDTO item : buildingSelector.getSelectedItems()) {
            this.list.add(item);
        }
        return this.list;
    }

}
