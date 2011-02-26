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


public class CCheckBox extends CEditableComponent<Boolean, NativeCheckBox> {

    private Alignment alignment = Alignment.left;

    public enum Alignment {
        left, center, right
    }

    public CCheckBox() {
        this(null);
    }

    public CCheckBox(String title) {
        super(title);
        populate(false);
    }

    @Override
    protected NativeCheckBox createWidget() {
        return new NativeCheckBox(this);
    }

    @Override
    public void setValue(Boolean value) {
        super.setValue(value == null ? false : value);
    }

    @Override
    public void populate(Boolean value) {
        super.populate(value == null ? false : value);
    }

    @Override
    public Boolean getValue() {
        return super.getValue() != null && (super.getValue());
    }

    public void setAlignmet(Alignment alignment) {
        this.alignment = alignment;
        if (isWidgetCreated()) {
            asWidget().setAlignmet(alignment);
        }
    }

    public Alignment getAlignmet() {
        return alignment;
    }
}
