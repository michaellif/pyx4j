/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.validators;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.CEditableComponent;

public class RevalidationTrigger<E> implements ValueChangeHandler<E> {

    private final CEditableComponent<?, ?> targetComponent;

    public RevalidationTrigger(CEditableComponent<?, ?> targetComponent) {
        this.targetComponent = targetComponent;
    }

    @Override
    public void onValueChange(ValueChangeEvent<E> event) {
        if (targetComponent.isVisited() || (!targetComponent.isValueEmpty())) {
            targetComponent.onEditingStop();
        }
    }

}
