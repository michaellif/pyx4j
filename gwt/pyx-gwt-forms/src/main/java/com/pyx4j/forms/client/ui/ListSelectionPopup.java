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

import java.util.Comparator;
import java.util.List;

import com.pyx4j.forms.client.ui.CListBox.ListBoxDisplayProperties;
import com.pyx4j.forms.client.validators.HasRequiredValueValidationMessage;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

public abstract class ListSelectionPopup<E> extends Dialog {

    private AvailableSelectedBox selectedBox;

    public ListSelectionPopup(String title, OkCancelOption callback) {
        super(title == null ? "Selection Dialog" : title, callback);
        ListBoxDisplayProperties displayProperties = new ListBoxDisplayProperties();
        displayProperties.visibleItemCount = 4;
        setBody(selectedBox = new AvailableSelectedBox(displayProperties));
    }

    public void setComparator(Comparator<E> comparator) {
        selectedBox.setComparator(comparator);
    }

    public void setRequiredValues(List<E> requiredValues) {
        selectedBox.setRequiredValues(requiredValues);
    }

    public void setHasRequiredValueValidationMessage(HasRequiredValueValidationMessage<E> hasRequiredValueValidationMessage) {
        selectedBox.setHasRequiredValueValidationMessage(hasRequiredValueValidationMessage);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        //super.onEnsureDebugId(baseID);
        selectedBox.ensureDebugId(baseID);
    }

    public void setSelectedItems(List<E> selectedItems) {
        selectedBox.setNativeValue(selectedItems);
    }

    public List<E> getSelectedItems() {
        return selectedBox.getNativeValue();
    }

    public void setOptionalItems(List<E> optionalItems) {
        selectedBox.setOptions(optionalItems);
    }

    class AvailableSelectedBox extends NativeListSelectionComposite<E> {

        public AvailableSelectedBox(ListBoxDisplayProperties properties) {
            super(properties);
            setListBoxWidth("250");
            setListBoxHeight("300");
        }

        @Override
        public String getItemName(E item) {
            return ListSelectionPopup.this.getItemName(item);
        }

        @Override
        public void onNativeValueChange(List<E> values) {
            // No immediate propagation
        }

        @Override
        public CComponent<?, ?> getCComponent() {
            return null;
        }

        @Override
        public void setValid(boolean valid) {
        }

        @Override
        public void installStyles(String stylePrefix) {
            // TODO Auto-generated method stub

        }
    }

    public abstract String getItemName(E item);

}
