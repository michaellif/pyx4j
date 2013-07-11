/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.web.client.themes.DashboardTheme;

public abstract class AbstractGadget<E extends IEntity, T extends GadgetViewer<?>> extends CEntityContainer<E> {

    private final ImageResource imageResource;

    private final String title;

    private Toolbar actionsToolbar;

    private ThemeColor themeColor;

    private T viewer;

    public AbstractGadget(T viewer, ImageResource imageResource, String title, ThemeColor themeColor) {
        this.viewer = viewer;
        this.imageResource = imageResource;
        this.title = title;
        this.themeColor = themeColor;
        asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
    }

    public AbstractGadget(T viewer, ThemeColor themeColor) {
        this(viewer, null, null, themeColor);
    }

    @Override
    public Collection<? extends CComponent<?>> getComponents() {
        return null;
    }

    @Override
    public ValidationResults getValidationResults() {
        return new ValidationResults();
    }

    @Override
    protected IDecorator<?> createDecorator() {
        return new GadgetDecorator();
    }

    public T getGadgetViewer() {
        return viewer;
    }

    protected void setActionsToolbar(Toolbar actionsToolbar) {
        this.actionsToolbar = actionsToolbar;
    }

    class GadgetDecorator extends SimplePanel implements IDecorator<CEntityContainer<?>> {

        private final FlowPanel mainPanel;

        private final SimplePanel contentPanel;

        public GadgetDecorator() {
            asWidget().setStyleName(DashboardTheme.StyleName.GadgetDecorator.name());

            mainPanel = new FlowPanel();
            mainPanel.setStyleName(DashboardTheme.StyleName.GadgetContent.name());
            mainPanel.getElement().getStyle().setProperty("borderTopWidth", "5px");
            mainPanel.getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

            FlowPanel containerPanel = new FlowPanel();
            containerPanel.setWidth("100%");
            containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            if (imageResource != null && title != null) {
                FlowPanel headerPanel = new FlowPanel();
                headerPanel.setStyleName(DashboardTheme.StyleName.GadgetHeader.name());
                headerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

                if (imageResource != null) {
                    Image icon = new Image(imageResource);
                    icon.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                    icon.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                    icon.getElement().getStyle().setMarginRight(10, Unit.PX);
                    headerPanel.add(icon);
                }
                if (title != null) {
                    HTML titleLabel = new HTML(title);
                    titleLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                    titleLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                    headerPanel.add(titleLabel);
                }
                containerPanel.add(headerPanel);
            }

            contentPanel = new SimplePanel();
            containerPanel.add(contentPanel);

            mainPanel.add(containerPanel);
            if (actionsToolbar != null) {
                mainPanel.add(actionsToolbar);
            }

            add(mainPanel);
        }

        @Override
        public void setComponent(CEntityContainer<?> viewer) {
            contentPanel.setWidget(viewer.createContent().asWidget());
        }

        @Override
        public void onSetDebugId(IDebugId parentDebugId) {
            // TODO Auto-generated method stub

        }

    }
}
