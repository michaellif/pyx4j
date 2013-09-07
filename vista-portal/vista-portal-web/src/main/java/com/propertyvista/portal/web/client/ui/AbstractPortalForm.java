/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

import com.propertyvista.portal.web.client.themes.BlockMixin;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;

public abstract class AbstractPortalForm<E extends IEntity> extends CEntityForm<E> {

    public AbstractPortalForm(Class<E> clazz) {
        super(clazz);
    }

    public AbstractPortalForm(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
    }

    protected class PortalContentHolder extends BasicContentHolder {

        private final SimplePanel contentPanel;

        public PortalContentHolder(ThemeColor themeColor) {
            contentPanel = new SimplePanel();
            contentPanel.setStyleName(EntityViewTheme.StyleName.EntityViewContent.name());
            contentPanel.addStyleName(BlockMixin.StyleName.PortalBlock.name());
            contentPanel.getElement().getStyle().setProperty("borderTopWidth", "5px");
            contentPanel.getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));
            setWidget(contentPanel);
        }

        @Override
        public void setWidget(IsWidget w) {
            contentPanel.setWidget(w);
        }

        @Override
        public Widget getWidget() {
            return contentPanel;
        }
    }
}
