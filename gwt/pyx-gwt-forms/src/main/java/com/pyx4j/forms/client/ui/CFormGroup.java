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
 * Created on Apr 23, 2010
 * @author Misha
 * @version $Id: CFormFolder.java 6669 2010-08-03 21:45:51Z michaellif $
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.forms.client.gwt.NativeFormGroup;

public abstract class CFormGroup<E> extends CFormContainer<E> {

    private CForm form;

    private final FormFactory factory;

    private NativeFormGroup<E> nativeFormFolder;

    private String itemCaption;

    public CFormGroup(FormFactory factory) {
        super();
        this.factory = factory;
    }

    public final CForm createForm() {
        CForm form = factory.createForm();
        form.setParentContainer(this);
        return form;
    }

    public void setItemCaption(String itemCaption) {
        this.itemCaption = itemCaption;
    }

    public String getItemCaption() {
        return itemCaption;
    }

    @Override
    public INativeEditableComponent<E> getNativeComponent() {
        return nativeFormFolder;
    }

    @Override
    public INativeEditableComponent<E> initNativeComponent() {
        if (nativeFormFolder == null) {
            nativeFormFolder = new NativeFormGroup<E>(this);
            setNativeComponentValue(getValue());
        }
        return nativeFormFolder;
    }

    @Override
    public boolean isValid() {
        return form.isValid();
    }

    public ValidationResults getValidationResults() {
        ValidationResults results = new ValidationResults();
        if (!form.isValid()) {
            results.appendValidationErrors(form.getValidationResults());
        }
        return results;
    }

}
