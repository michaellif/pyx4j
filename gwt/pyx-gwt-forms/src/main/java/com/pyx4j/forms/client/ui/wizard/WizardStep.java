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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.wizard;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.validators.IValidatable;
import com.pyx4j.forms.client.validators.ValidationResults;

public class WizardStep extends SimplePanel implements IValidatable {

    private WizardPanel parent;

    private IsWidget content;

    private String title;

    private boolean complete = false;

    private boolean visited = false;

    private String warning = null;

    public WizardStep() {
        this(null, null);
    }

    public WizardStep(IsWidget content, String title) {
        setStepTitle(title);
        setStepContent(content);
        addStyleName(WizardDecoratorTheme.StyleName.WizardStep.name());
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
        if (content instanceof IValidatable) {
            return ((IValidatable) content).getValidationResults();
        } else {
            return new ValidationResults();
        }
    }

    @Override
    public void showErrors(boolean show) {
        if (content instanceof IValidatable) {
            ((IValidatable) content).showErrors(show);
        }
    }

}