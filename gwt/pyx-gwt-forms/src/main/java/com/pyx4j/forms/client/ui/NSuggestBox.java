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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

import com.pyx4j.widgets.client.SuggestBox;

public class NSuggestBox<E> extends NTextFieldBase<E, SuggestBox, CAbstractSuggestBox<E>> {

    public NSuggestBox(final CAbstractSuggestBox<E> cSuggestBox) {
        super(cSuggestBox);
    }

    @Override
    protected SuggestBox createEditor() {
        return new SuggestBox();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(SelectionEvent event) {
                setFocus(true);
                getCComponent().onEditingStop();
            }
        });
        refreshOptions();
    }

    public void refreshOptions() {

        if (!isViewable()) {
            E currentSelection = getCComponent().getValue();
            ((MultiWordSuggestOracle) getEditor().getSuggestOracle()).clear();
            if (getCComponent().getOptions() != null) {
                for (E option : getCComponent().getOptions()) {
                    ((MultiWordSuggestOracle) getEditor().getSuggestOracle()).add(getCComponent().getOptionName(option));
                }
            }
            getCComponent().setValue(currentSelection);
        }
    }

    public void addItem(String optionName) {
        ((MultiWordSuggestOracle) getEditor().getSuggestOracle()).add(optionName);
    }

}
