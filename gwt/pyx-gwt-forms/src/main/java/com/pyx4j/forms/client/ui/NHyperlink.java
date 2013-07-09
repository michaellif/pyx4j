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

import java.text.ParseException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

public class NHyperlink<E> extends NField<E, Label, CHyperlink<E>, Anchor> implements INativeHyperlink<E> {

    public NHyperlink(final CHyperlink<E> hyperlink) {
        super(hyperlink);
    }

    @Override
    public void setNativeValue(E value) {
        String text = "";
        CHyperlink<E> comp = getCComponent();
        if (value != null) {
            if (comp.getFormat() != null) {
                text = comp.getFormat().format(value);
            } else {
                text = value.toString();
            }
        }

        if (getEditor() != null) {
            getEditor().setText(text);
        }
        if (getViewer() != null) {
            getViewer().setText(text);
        }
    }

    @Override
    public E getNativeValue() throws ParseException {
        assert false : "getNativeValue() shouldn't be called on Hyperlink";
        return null;
    }

    @Override
    protected Label createEditor() {
        return new Label();
    }

    @Override
    protected Anchor createViewer() {
        Anchor anchor = new Anchor("");
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (getCComponent().getCommand() != null) {
                    getCComponent().getCommand().execute();
                }
            }

        });
        return anchor;
    }

}
