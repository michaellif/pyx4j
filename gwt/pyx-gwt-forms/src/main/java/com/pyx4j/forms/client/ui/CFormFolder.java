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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.LinkedHashMap;
import java.util.List;

import com.pyx4j.forms.client.gwt.NativeFormFolder;

public abstract class CFormFolder<E> extends CFormContainer<List<E>, NativeFormFolder<E>> {

    private final LinkedHashMap<E, CForm> formsMap;

    private final FormFactory factory;

    private String itemCaption;

    public CFormFolder(FormFactory factory) {
        super();
        this.factory = factory;
        formsMap = new LinkedHashMap<E, CForm>();
    }

    public final CForm createForm() {
        CForm form = factory.createForm();
        form.setParentContainer(this);
        return form;
    }

    public LinkedHashMap<E, CForm> getFormsMap() {
        return formsMap;
    }

    public void setItemCaption(String itemCaption) {
        this.itemCaption = itemCaption;
    }

    public String getItemCaption() {
        return itemCaption;
    }

    @Override
    public NativeFormFolder<E> initWidget() {
        NativeFormFolder<E> nativeFormFolder = new NativeFormFolder<E>(this);
        setNativeComponentValue(getValue());
        return nativeFormFolder;
    }

    public abstract void addItem();

    public abstract void removeItem(CForm cForm);

    public abstract void moveItem(CForm cForm, boolean up);

    @Override
    public boolean isValid() {
        for (CForm form : formsMap.values()) {
            if (!form.isValid()) {
                return false;
            }
        }
        return true;
    }

    public ValidationResults getValidationResults() {
        ValidationResults results = new ValidationResults();
        for (CForm form : formsMap.values()) {
            if (!form.isValid()) {
                results.appendValidationErrors(form.getValidationResults());
            }
        }
        return results;
    }

}
