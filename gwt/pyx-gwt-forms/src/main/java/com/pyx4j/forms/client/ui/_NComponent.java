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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.widgets.client.Button;

public abstract class _NComponent<DATA, WIDGET extends IsWidget, CCOMP extends CComponent<DATA, ?>> extends SimplePanel implements INativeComponent<DATA> {

    private WIDGET editor;

    private HTML viewer;

    private final CCOMP cComponent;

    private boolean viewable;

    private _TriggerPanel triggerPanel;

    private ImageResource triggerImage;

    public _NComponent(CCOMP cComponent) {
        this(cComponent, null);
    }

    public _NComponent(CCOMP cComponent, ImageResource triggerImage) {
        super();
        this.cComponent = cComponent;
        this.triggerImage = triggerImage;

        setViewable(cComponent.isViewable());

    }

    public WIDGET getEditor() {
        return editor;
    }

    public Button getTriggerButton() {
        return triggerPanel == null ? null : triggerPanel.getTriggerButton();
    }

    public HTML getViewer() {
        return viewer;
    }

    @Override
    public CCOMP getCComponent() {
        return cComponent;
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        if (event.isEventOfType(PropertyName.repopulated)) {
            removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited)) {
            if (getCComponent().isValid()) {
                removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            } else if (getCComponent().isVisited()) {
                addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            }
        }
    }

    protected abstract WIDGET createEditor();

    protected HTML createViewer() {
        return new HTML();
    }

    protected void onEditorCreate() {
        editor.asWidget().setWidth(getCComponent().getWidth());
    }

    protected void onEditorInit() {
        setNativeValue(cComponent.getValue());
    }

    protected void onViewerCreate() {

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
                if (triggerImage != null) {
                    triggerPanel = new _TriggerPanel(this, triggerImage);
                }
            }
            onEditorInit();
            if (triggerImage == null) {
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

    public void onTriggerOn() {
    }

    public void onTriggerOff() {
    }

    protected GroupFocusHandler getGroupFocusHandler() {
        if (triggerPanel != null) {
            return triggerPanel.getGroupFocusHandler();
        } else {
            return null;
        }
    }

}
