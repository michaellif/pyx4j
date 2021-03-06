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
 * Created on Jul 25, 2013
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.wizard;

import com.google.gwt.user.client.ui.IsWidget;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.forms.client.validators.IValidatable;
import com.pyx4j.forms.client.validators.ValidatableWidget;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

public class WizardStep extends SimplePanel implements IValidatable {

    private static final I18n i18n = I18n.get(WizardStep.class);

    private WizardPanel parent;

    private IsWidget content;

    private String title;

    private String nextButtonCaption;

    private boolean complete = false;

    private boolean visited = false;

    private String warning = null;

    public WizardStep() {
        this(null, null);
    }

    public WizardStep(IsWidget content, String title) {
        this(content, title, i18n.tr("Next"));
    }

    public WizardStep(IsWidget content, String title, String nextButtonCaption) {
        setStepTitle(title);
        setStepContent(content);
        addStyleName(WizardDecoratorTheme.StyleName.WizardStep.name());
        this.nextButtonCaption = nextButtonCaption;
    }

    public void setParent(WizardPanel parent) {
        this.parent = parent;
    }

    protected void setStepContent(IsWidget content) {
        setWidget(this.content = content);
    }

    public String getStepTitle() {
        return title;
    }

    public void setStepTitle(String title) {
        this.title = title;
    }

    public String getNextButtonCaption() {
        return nextButtonCaption;
    }

    public void setNextButtonCaption(String nextButtonCaption) {
        this.nextButtonCaption = nextButtonCaption;
    }

    public boolean isStepVisible() {
        return parent.getVisibleStep() == this;
    }

    public void onStepVizible(boolean flag) {

    }

    public void setStepComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isStepComplete() {
        return complete;
    }

    public void setStepVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isStepVisited() {
        return visited;
    }

    public void setStepWarning(String message) {
        this.warning = message;
    }

    public String getStepWarning() {
        return warning;
    }

    @Override
    public ValidationResults getValidationResults() {
        return ValidatableWidget.getValidationResults(content);
    }

    @Override
    public void showErrors(boolean show) {
        ValidatableWidget.showErrors(content, show);
    }

}