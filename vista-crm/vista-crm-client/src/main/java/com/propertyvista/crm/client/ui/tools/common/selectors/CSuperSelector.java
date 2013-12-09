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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.crm.client.ui.tools.common.widgets.superselector.SuperSelector;
import com.propertyvista.crm.rpc.dto.selections.BuildingForSelectionDTO;

/**
 * This class wrapping a native component in CComponent interface, so that a common form decorator could be used.<br>
 * Right now I don't have the time to implement it properly.<br>
 * Use {link {@link #getSelectorWidget()} to access the wrapped widget
 */
@Deprecated
public class CSuperSelector<E extends IEntity> extends CComponent<IList<E>> {

    private final SuperSelector<E> superSelector;

    private IList<BuildingForSelectionDTO> list;

    public CSuperSelector(SuperSelector<E> superSelector) {
        this.superSelector = superSelector;
    }

    @Override
    public Widget asWidget() {
        return superSelector;
    }

    public SuperSelector<E> getSelectorWidget() {
        return superSelector;
    }

    @Override
    protected void setDebugId(IDebugId debugId) {
        // TODO Auto-generated method stub        
    }

    @Override
    protected void setEditorValue(IList<E> value) {
        // TODO Auto-generated method stub

    }

    @Override
    protected IList<E> getEditorValue() throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

}
