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
package com.pyx4j.entity.client.ui.flex;

import java.util.Collection;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.ValidationResults;

public abstract class CEntityContainer<E extends IObject<?>> extends CEntityComponent<E, NativeEntityPanel<E>> {

    private static I18n i18n = I18nFactory.getI18n(CEntityContainer.class);

    private IDecorator decorator;

    private ImageResource icon;

    public abstract Collection<? extends CEditableComponent<?, ?>> getComponents();

    public abstract ValidationResults getValidationResults();

    @Override
    public boolean isValid() {

        if (!isEditable() || !isEnabled()) {
            return true;
        }

        if (getComponents() != null) {
            for (CEditableComponent<?, ?> ccomponent : getComponents()) {
                if (!ccomponent.isValid()) {
                    return false;
                }
            }
        }
        return super.isValid();
    }

    public ValidationResults getAllValidationResults() {
        ValidationResults validationResults = getContainerValidationResults();
        for (CEditableComponent<?, ?> component : this.getComponents()) {
            if (component.isValid()) {
                continue;
            }
            if (component instanceof CEntityContainer<?>) {
                validationResults.appendValidationErrors(((CEntityContainer<?>) component).getValidationResults());
            } else if (component.isVisited()) {
                validationResults.appendValidationError(i18n.tr("Field ''{0}'' is not valid. {1}", component.getTitle(), component.getValidationMessage()));
            }
        }

        return validationResults;
    }

    public ValidationResults getContainerValidationResults() {
        ValidationResults validationResults = new ValidationResults();
        String message = getValidationMessage();
        if (message != null) {
            if (CommonsStringUtils.isStringSet(getTitle())) {
                validationResults.appendValidationError(i18n.tr("''{0}'' is not valid. {1}", getTitle(), message));
            } else {
                validationResults.appendValidationError(message);
            }
        }
        return validationResults;
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (getComponents() != null) {
            for (CEditableComponent<?, ?> ccomponent : getComponents()) {
                ((CEditableComponent<?, ?>) ccomponent).setVisited(visited);
            }
        }
    }

    public abstract IsWidget createContent();

    protected IDecorator<?> createDecorator() {
        return null;
    }

    public Panel getContainer() {
        return asWidget();
    }

    @Override
    protected NativeEntityPanel<E> createWidget() {
        return new NativeEntityPanel<E>();
    }

    public void initContent() {
        decorator = createDecorator();
        if (decorator == null) {
            asWidget().setWidget(createContent());
        } else {
            asWidget().setWidget(decorator);
            decorator.setFolderItem(this);
        }

        addValidations();
    }

    public IDecorator<?> getDecorator() {
        return decorator;
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

    public ImageResource getIcon() {
        return icon;
    }
}
