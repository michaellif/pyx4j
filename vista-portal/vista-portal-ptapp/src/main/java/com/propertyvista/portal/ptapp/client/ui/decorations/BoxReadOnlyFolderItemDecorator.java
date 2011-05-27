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
package com.propertyvista.portal.ptapp.client.ui.decorations;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;

import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;

public class BoxReadOnlyFolderItemDecorator extends FlowPanel implements FolderItemDecorator {

    private final SimplePanel content;

    private Widget separator;

    public BoxReadOnlyFolderItemDecorator(boolean withLineSeparator) {
        content = new SimplePanel();

        if (withLineSeparator) {
            separator = new VistaLineSeparator(100, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            separator.getElement().getStyle().setPadding(0, Unit.EM);
            add(separator);
        }

        content.setWidth("100%");
        add(content);

        setWidth("100%");
    }

    public BoxReadOnlyFolderItemDecorator(boolean withLineSeparator, String width) {
        this(withLineSeparator);
        if (separator != null) {
            separator.setWidth(width);
        }
    }

    @Override
    public void setFolderItem(CEntityFolderItem<?> w) {
        content.setWidget(w.getContent());
    }

    @Override
    public HandlerRegistration addItemClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowCollapseClickHandler(ClickHandler handler) {
        return null;
    }

}
