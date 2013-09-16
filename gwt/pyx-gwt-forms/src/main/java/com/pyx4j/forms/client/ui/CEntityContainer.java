/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on May 31, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.widgets.client.Button;

public abstract class CEntityContainer<E extends IObject<?>> extends CContainer<E> implements IEditableComponentFactory {

    private static final Logger log = LoggerFactory.getLogger(CEntityContainer.class);

    private ImageResource icon;

    private boolean initiated = false;

    private final SimplePanel contentHolder;

    private final ContainerPanel containerPanel;

    public CEntityContainer() {
        containerPanel = new ContainerPanel();
        if (false) {
            Button debugButton = new Button("Debug", new Command() {

                @Override
                public void execute() {
                    log.info(CEntityContainer.this.getValue().toString());
                    new EntityViewerDialog(CEntityContainer.this.getValue()).show();
                }
            });
            debugButton.getElement().getStyle().setProperty("display", "inline-block");
            containerPanel.add(debugButton);
            containerPanel.getElement().getStyle().setProperty("border", "red solid 1px");
        }

        contentHolder = new SimplePanel();
        contentHolder.asWidget().getElement().getStyle().setProperty("display", "inline-block");
        containerPanel.add(contentHolder);
    }

    @Override
    public Widget asWidget() {
        return containerPanel;
    }

    public abstract IsWidget createContent();

    protected IDecorator<?> createDecorator() {
        return null;
    }

    @Override
    public void setDecorator(IDecorator decorator) {
        throw new Error("Use createDecorator() instead");
    }

    public final void initContent() {
        assert initiated == false;
        if (!initiated) {
            asWidget();
            IDecorator<?> decorator = createDecorator();
            if (decorator == null) {
                contentHolder.setWidget(createContent());
            } else {
                super.setDecorator(decorator);
                contentHolder.setWidget(getDecorator());
            }
            addValidations();
            if (ApplicationMode.isDevelopment()) {
                DevelopmentShortcutUtil.attachDevelopmentShortcuts(asWidget(), this);
            }
            initiated = true;
        }
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getIcon() {
        return icon;
    }

    @Override
    public boolean isVisited() {
        return true;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        assert (getParent() != null) : "Flex Component " + this.getClass().getName() + "is not bound";
        return ((CEntityContainer<?>) getParent()).create(member);
    }

    @Override
    public void onAdopt(CContainer<?> parent) {
        super.onAdopt(parent);
        if (!initiated) {
            initContent();
        }
    }

    @Override
    public void onAbandon() {
        super.onAbandon();
    }

    public void addValidations() {

    }

    public final HandlerRegistration addDevShortcutHandler(DevShortcutHandler handler) {
        return addHandler(handler, DevShortcutEvent.getType());
    }

    class ContainerPanel extends FlowPanel implements RequiresResize, ProvidesResize {

        public ContainerPanel() {
        }

        @Override
        public void onResize() {
            if (contentHolder.getWidget() instanceof RequiresResize) {
                ((RequiresResize) contentHolder.getWidget()).onResize();
            }
        }
    }

}
