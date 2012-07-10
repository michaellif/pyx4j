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
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanel;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelActionWidget;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH1;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH1Label;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH2;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH2Label;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH3;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH3Label;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH4;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelH4Label;
import static com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName.FormFlexPanelHR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.events.HasPropertyChangeHandlers;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeComponent;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme.StyleName;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

public class FormFlexPanel extends FlexTable implements PropertyChangeHandler, HasPropertyChangeHandlers {

    private static final I18n i18n = I18n.get(FormFlexPanel.class);

    private final String title;

    private final List<CComponent<?, ?>> components = new ArrayList<CComponent<?, ?>>();

    public FormFlexPanel(String title) {
        this.title = title;
        setStyleName(FormFlexPanel.name());

    }

    public FormFlexPanel() {
        this(null);
    }

    public void setHR(int row, int col, int span) {
        setBR(row, col, span);
        super.getWidget(row, col).setStyleName(FormFlexPanelHR.name());
    }

    public void setBR(int row, int col, int span) {
        getFlexCellFormatter().setColSpan(row, col, span);
        super.setWidget(row, col, new HTML("&nbsp;"));
    }

    public void setH1(int row, int col, int span, String caption) {
        setH1(row, col, span, caption, null);
    }

    public void setH1(int row, int col, int span, String caption, Widget actionWidget) {
        setHX(row, col, span, caption, actionWidget, FormFlexPanelH1Label, FormFlexPanelH1);
    }

    public void setH2(int row, int col, int span, String caption) {
        setH2(row, col, span, caption, null);
    }

    public void setH2(int row, int col, int span, String caption, Widget actionWidget) {
        setHX(row, col, span, caption, actionWidget, FormFlexPanelH2Label, FormFlexPanelH2);
    }

    public void setH3(int row, int col, int span, String caption) {
        setH3(row, col, span, caption, null);
    }

    public void setH3(int row, int col, int span, String caption, Widget actionWidget) {
        setHX(row, col, span, caption, actionWidget, FormFlexPanelH3Label, FormFlexPanelH3);
    }

    public void setH4(int row, int col, int span, String caption) {
        setH4(row, col, span, caption, null);
    }

    public void setH4(int row, int col, int span, String caption, Widget actionWidget) {
        setHX(row, col, span, caption, actionWidget, FormFlexPanelH4Label, FormFlexPanelH4);
    }

    private void setHX(int row, int col, int span, String caption, Widget actionWidget, StyleName labelStyle, StyleName headerStyle) {
        getFlexCellFormatter().setColSpan(row, col, span);
        HorizontalPanel header = new HorizontalPanel();
        header.setStyleName(headerStyle.name());

        Label label = new Label(caption);
        label.setStyleName(labelStyle.name());
        header.add(label);

        if (actionWidget != null) {
            SimplePanel actionWidgetHolder = new SimplePanel();
            actionWidgetHolder.setWidget(actionWidget);
            actionWidgetHolder.setStyleName(FormFlexPanelActionWidget.name());
            header.add(actionWidgetHolder);
        }

        super.setWidget(row, col, header);
    }

    @Override
    public void setWidget(int row, int column, Widget widget) {
        locateCComponents(widget);
        super.setWidget(row, column, widget);
    }

    private void locateCComponents(Widget widget) {
        if (widget instanceof INativeComponent) {
            CComponent<?, ?> comp = ((INativeComponent<?>) widget).getCComponent();
            components.add(comp);
            comp.addPropertyChangeHandler(this);
            comp.setLocationHint(title);
        } else if (widget instanceof HasWidgets) {
            for (Iterator<Widget> iterator = ((HasWidgets) widget).iterator(); iterator.hasNext();) {
                locateCComponents(iterator.next());
            }
        }
    }

    @Override
    public void setWidget(int row, int column, IsWidget widget) {
        setWidget(row, column, asWidgetOrNull(widget));
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        PropertyChangeEvent.fire(this, event.getPropertyName());
    }

    @Override
    public HandlerRegistration addPropertyChangeHandler(PropertyChangeHandler handler) {
        return addHandler(handler, PropertyChangeEvent.getType());
    }

    public boolean isContentValid() {
        boolean valid = true;
        for (CComponent<?, ?> component : components) {
            if ((component.isUnconditionalValidationErrorRendering() || component.isVisited()) && !component.isValid()) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    public String getValidationMessage() {
        ValidationResults validationResults = new ValidationResults();
        for (CComponent<?, ?> component : components) {
            if ((component.isUnconditionalValidationErrorRendering() || component.isVisited()) && !component.isValid()) {
                validationResults.appendValidationErrors(component.getValidationResults());
            }
        }
        return getMessagesText(validationResults);
    }

    public String getMessagesText(ValidationResults validationResults) {
        StringBuilder messagesBuffer = new StringBuilder();
        ArrayList<ValidationError> validationErrors = validationResults.getValidationErrors();

        if (validationErrors.size() > 1) {
            messagesBuffer.append(i18n.tr("error 1 of {0}", (validationErrors.size()))).append(" - ");
        }

        if (validationErrors.size() > 0) {
            messagesBuffer.append(validationErrors.get(0).getMessageString(false));
        }

        return messagesBuffer.toString();
    }

}
