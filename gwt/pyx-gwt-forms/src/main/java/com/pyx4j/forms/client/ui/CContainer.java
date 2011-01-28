/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Collection;

import com.google.gwt.user.client.ui.Widget;

public abstract class CContainer<WIDGET_TYPE extends Widget & INativeComponent> extends CComponent<WIDGET_TYPE> {

    private final IAccessAdapter aggregatingAccessAdapter;

    public CContainer() {
        this(null);
    }

    public CContainer(String title) {
        super(title);
        aggregatingAccessAdapter = new ContainerAccessAdapter(this);
    }

    public abstract Collection<CComponent<?>> getComponents();

    public abstract void addComponent(CComponent<?> component);

    public boolean isValid() {
        if (!isEditable() || !isEnabled()) {
            return true;
        }
        for (CComponent<?> ccomponent : getComponents()) {
            if (ccomponent instanceof CEditableComponent<?, ?> && !((CEditableComponent<?, ?>) ccomponent).isValid()) {
                return false;
            } else if (ccomponent instanceof CContainer && !((CContainer) ccomponent).isValid()) {
                return false;
            }
        }
        return true;
    }

    public ValidationResults getValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        for (CComponent<?> ccomponent : getComponents()) {
            if (ccomponent instanceof CFormFolder && !((CFormFolder) ccomponent).isValid()) {
                validationResults.appendValidationErrors(((CFormFolder) ccomponent).getValidationResults());
            } else if (ccomponent instanceof CEditableComponent<?, ?> && !((CEditableComponent<?, ?>) ccomponent).isValid()) {
                validationResults.appendValidationError("Field '" + ccomponent.getTitle() + "'  is not valid. "
                        + ((CEditableComponent<?, ?>) ccomponent).getValidationMessage());
            } else if (ccomponent instanceof CContainer && !((CContainer) ccomponent).isValid()) {
                validationResults.appendValidationErrors(((CContainer) ccomponent).getValidationResults());
            }
        }
        return validationResults;
    }

    public IAccessAdapter getContainerAccessAdapter() {
        return aggregatingAccessAdapter;
    }

    public boolean isEditable() {
        for (IAccessAdapter adapter : getAccessAdapters()) {
            if (!adapter.isEditable(this)) {
                return false;
            }
        }
        return true;
    }

    public void setEditable(boolean editable) {
        defaultAccessAdapter.setEditable(editable);
        applyEditabilityRules();
    }

    @Override
    protected void applyVisibilityRules() {
        super.applyVisibilityRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyVisibilityRules();
            }
        }
    }

    @Override
    protected void applyEnablingRules() {
        super.applyEnablingRules();
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                component.applyEnablingRules();
            }
        }
    }

    protected void applyEditabilityRules() {
        if (getComponents() != null) {
            for (CComponent<?> component : getComponents()) {
                if (component instanceof CEditableComponent<?, ?>) {
                    ((CEditableComponent<?, ?>) component).applyEditabilityRules();
                } else if (component instanceof CContainer) {
                    ((CContainer) component).applyEditabilityRules();
                }
            }
        }
    }

    @Override
    protected void applyAccessibilityRules() {
        super.applyAccessibilityRules();
        applyEditabilityRules();
    }
}
