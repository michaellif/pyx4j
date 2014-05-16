/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;

/**
 * This validator is set by external source asynchronously. It resets on editing stop event.
 */
public class AsyncValidator<DATA_TYPE> extends AbstractComponentValidator<DATA_TYPE> {

    private AbstractValidationError error;

    public AsyncValidator() {
    }

    public void setValidationError(AbstractValidationError error) {
        this.error = error;
    }

    @Override
    public AbstractValidationError isValid() {
        return error;
    }

    @Override
    public void setComponent(CComponent<?, DATA_TYPE, ?> component) {
        super.setComponent(component);
        component.addPropertyChangeHandler(new PropertyChangeHandler() {

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.editingInProgress) {
                    error = null;
                }
            }
        });
    }

}
