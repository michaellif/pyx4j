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
 * Created on Jan 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.IWidget;

public abstract class NComponent<DATA, WIDGET extends IWidget, CCOMP extends CComponent<DATA, ?>, VIEWER extends Widget> extends SimplePanel implements
        INativeComponent<DATA> {

    private WIDGET editor;

    private VIEWER viewer;

    private final CCOMP cComponent;

    private boolean viewable;

    private TriggerPanel triggerPanel;

    private ImageResource triggerImageResource;

    public NComponent(CCOMP cComponent) {
        this(cComponent, null);
    }

    public NComponent(CCOMP cComponent, ImageResource triggerImageResource) {
        super();
        this.cComponent = cComponent;
        this.triggerImageResource = triggerImageResource;

        setViewable(cComponent.isViewable());

    }

    public WIDGET getEditor() {
        return editor;
    }

    public Button getTriggerButton() {
        return triggerPanel == null ? null : triggerPanel.getTriggerButton();
    }

    public VIEWER getViewer() {
        return viewer;
    }

    @Override
    public CCOMP getCComponent() {
        return cComponent;
    }

    protected abstract WIDGET createEditor();

    protected abstract VIEWER createViewer();

    protected void onEditorCreate() {
        editor.asWidget().setWidth(getCComponent().getWidth());
        setDebugId(getCComponent().getDebugId());
    }

    protected void onEditorInit() {
        setNativeValue(cComponent.getValue());
    }

    protected void onViewerCreate() {
        setDebugId(getCComponent().getDebugId());
    }

    protected void onViewerInit() {
        setNativeValue(cComponent.getValue());
    }

    @Override
    public void setViewable(boolean viewable) {
        this.viewable = viewable;
        if (viewable) {
            if (viewer == null) {
                viewer = createViewer();
                onViewerCreate();
            }
            onViewerInit();
            setWidget(viewer);
        } else {
            if (editor == null) {
                editor = createEditor();
                onEditorCreate();
                if (triggerImageResource != null) {
                    triggerPanel = new TriggerPanel(this, triggerImageResource);
                }
            }
            onEditorInit();
            if (triggerImageResource == null) {
                setWidget(editor);
            } else {
                setWidget(triggerPanel);
            }
        }
    }

    @Override
    public boolean isViewable() {
        return viewable;
    }

    public void onToggle() {
    }

    protected void setToggleOn(boolean flag) {
        triggerPanel.toggleOn(flag);
    }

    protected boolean isToggledOn() {
        return triggerPanel.isToggledOn();
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        if (triggerPanel != null) {
            return triggerPanel.getGroupFocusHandler();
        } else {
            return null;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        Button triggerButton = getTriggerButton();
        if (triggerButton != null) {
            triggerButton.setEnabled(isEditable() && enabled);
        }

        if (getEditor() != null) {
            getEditor().setEnabled(enabled);
            if (enabled) {
                getEditor().removeStyleDependentName(DefaultCComponentsTheme.StyleDependent.disabled.name());
            } else {
                getEditor().addStyleDependentName(DefaultCComponentsTheme.StyleDependent.disabled.name());
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (isViewable()) {
            return false;
        } else {
            return getEditor().isEnabled();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        Button triggerButton = getTriggerButton();
        if (triggerButton != null) {
            triggerButton.setEnabled(isEnabled() && editable);
        }

        if (getEditor() != null) {
            getEditor().setEditable(editable);
            if (editable) {
                getEditor().removeStyleDependentName(DefaultCComponentsTheme.StyleDependent.readonly.name());
            } else {
                getEditor().addStyleDependentName(DefaultCComponentsTheme.StyleDependent.readonly.name());
            }
        }
    }

    @Override
    public boolean isEditable() {
        if (isViewable()) {
            return false;
        } else {
            return getEditor().isEditable();
        }
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        // TODO remove this, or make configurable
        if (debugId == null) {
            return;
        }

        assert (debugId != null) : "Unassigned DebugId in native component of " + getCComponent().shortDebugInfo();
        if (getEditor() != null) {
            getEditor().ensureDebugId(debugId.debugId());

            if (triggerPanel != null)
                triggerPanel.ensureDebugId(debugId.debugId() + "-triggerPanel");

        }
        if (getViewer() != null) {
            getViewer().ensureDebugId(debugId.debugId());
        }
    }

}
