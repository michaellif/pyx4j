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

public abstract class CFormFolder<E> extends CEditableComponent<List<E>> {

    private final LinkedHashMap<E, CForm> formsMap;

    private final FormFactory factory;

    private NativeFormFolder<E> nativeFormFolder;

    public CFormFolder(FormFactory factory) {
        super();
        this.factory = factory;
        formsMap = new LinkedHashMap<E, CForm>();
    }

    public final CForm createForm() {
        CForm form = factory.createForm();
        form.setFolder(this);
        return form;
    }

    public LinkedHashMap<E, CForm> getFormsMap() {
        return formsMap;
    }

    @Override
    public INativeEditableComponent<List<E>> getNativeComponent() {
        return nativeFormFolder;
    }

    @Override
    public INativeEditableComponent<List<E>> initNativeComponent() {
        if (nativeFormFolder == null) {
            nativeFormFolder = new NativeFormFolder<E>(this);
            setNativeComponentValue(getValue());
        }
        return nativeFormFolder;
    }

    public abstract void addItem();
}
