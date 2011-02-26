/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.decorations;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public class BoxReadOnlyFolderDecorator<E extends IEntity> extends SimplePanel implements FolderDecorator<E> {

    @Override
    public void setWidget(IsWidget w) {
        super.setWidget(w);
        this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
    }

    @Override
    public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
        return null;
    }

}
