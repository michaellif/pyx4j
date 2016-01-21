/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Aug 18, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.DebugIds;

public class MessagePannel extends FlowPanel {

    public static enum Location {
        Top, Bottom
    }

    private final HTML validationLabel;

    private final Label noteLabel;

    private CComponent<?> component;

    public MessagePannel(Location location) {

        validationLabel = new HTML();
        validationLabel.setVisible(false);
        validationLabel.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());

        noteLabel = new Label();
        noteLabel.setVisible(false);
        noteLabel.setStyleName(CComponentTheme.StyleName.NoteLabel.name());

        switch (location) {
        case Top:
            add(noteLabel);
            add(validationLabel);
            break;
        case Bottom:
            add(validationLabel);
            add(noteLabel);
            break;

        }

    }

    public void init(CComponent<?> component) {
        this.component = component;
    }

    public void renderNote() {
        if (component == null) {//Not initiated yet
            return;
        }
        if (component.getNote() != null && component.getNote().trim().length() > 0) {
            noteLabel.setText(component.getNote());
            noteLabel.setVisible(true);
            noteLabel.addStyleDependentName(component.getNoteStyle().getStyle().toString());
        } else {
            noteLabel.setText(null);
            noteLabel.setVisible(false);
            for (CComponentTheme.StyleDependent style : CComponentTheme.StyleDependent.values()) {
                noteLabel.removeStyleDependentName(style.toString());
            }
        }
    }

    public void renderValidationMessage() {
        if (!component.isValid()) {
            validationLabel.setHTML(component.getValidationResults().getValidationMessage(true));
            component.asWidget().addStyleDependentName(WidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(true);
        } else {
            validationLabel.setText(null);
            component.asWidget().removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(false);
        }

        if (component.getDebugId() != null) {
            validationLabel.ensureDebugId(new CompositeDebugId(component.getDebugId(), DebugIds.ValidationLabel).debugId());
        }
    }
}
