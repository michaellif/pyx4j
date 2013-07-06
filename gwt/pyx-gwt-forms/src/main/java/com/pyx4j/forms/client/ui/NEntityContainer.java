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
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.widgets.client.Button;

public class NEntityContainer<E extends IObject<?>> extends FlowPanel implements INativeComponent<E>, RequiresResize, ProvidesResize {

    private static final Logger log = LoggerFactory.getLogger(NEntityContainer.class);

    private final CEntityContainer<E> container;

    private final SimplePanel contentHolder;

    private boolean viewable;

    private boolean editable = true;

    private boolean enabled = true;

    public NEntityContainer(final CEntityContainer<E> container) {
        this.container = container;

        if (false) {
            Button debugButton = new Button("Debug", new Command() {

                @Override
                public void execute() {
                    log.info(container.getValue().toString());
                    new EntityViewerDialog(container.getValue()).show();
                }
            });
            debugButton.getElement().getStyle().setProperty("display", "inline-block");
            add(debugButton);

            getElement().getStyle().setProperty("border", "red solid 1px");
        }

        contentHolder = new SimplePanel();
        contentHolder.getElement().getStyle().setProperty("display", "inline");
        add(contentHolder);

    }

    @Override
    public CComponent<?, ?> getCComponent() {
        return container;
    }

    public void setWidget(IsWidget widget) {
        contentHolder.setWidget(widget);
    }

    @Override
    public void onResize() {
        if (contentHolder.getWidget() instanceof RequiresResize) {
            ((RequiresResize) contentHolder.getWidget()).onResize();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        if (editable) {
            asWidget().removeStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        } else {
            asWidget().addStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            asWidget().removeStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        } else {
            asWidget().addStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }

    @Override
    public boolean isViewable() {
        return viewable;
    }

    @Override
    public void setNativeValue(E value) {
    }

    @Override
    public E getNativeValue() {
        return null;
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNavigationCommand(Command navigationCommand) {
        // TODO Auto-generated method stub

    }

}
