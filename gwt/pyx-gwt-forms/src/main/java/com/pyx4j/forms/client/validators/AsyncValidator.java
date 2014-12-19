/*
 * Pyx4j framework
 * Copyright (C) 2008 pyx4j.com.
 *
 * Created on Oct 14, 2008
 * @author michaellif
 */
package com.pyx4j.forms.client.validators;

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

}
