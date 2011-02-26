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

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;

public abstract class CFormFolder<E> extends CFormContainer<List<E>, NativeFormFolder<E>> {

    private final LinkedHashMap<E, CForm> formsMap;

    private final FormFactory factory;

    private String itemCaption;

    private boolean addable = true;

    private boolean removable = true;

    private boolean movable = true;

    protected int currentRowDebugId = 0;

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
    protected NativeFormFolder<E> createWidget() {
        return new NativeFormFolder<E>(this);
    }

    public boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void makeUnchangeable() {
        setAddable(false);
        setRemovable(false);
        setMovable(false);
    }

    public abstract void addItem();

    public abstract void removeItem(CForm cForm);

    public abstract void moveItem(CForm cForm, boolean up);

    @Override
    public boolean validate() {
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

    public IDebugId getCurrentRowDebugId() {
        return new CompositeDebugId(this.getDebugId(), new StringDebugId(currentRowDebugId));
    }

}
