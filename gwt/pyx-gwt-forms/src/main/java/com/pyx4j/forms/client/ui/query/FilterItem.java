/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 8, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.query;

import com.pyx4j.entity.core.query.ICondition;

public class FilterItem implements Comparable<FilterItem> {

    private final ICondition condition;

    private boolean editorShownOnAttach;

    public FilterItem(ICondition condition) {
        this.condition = condition;
    }

    public ICondition getCondition() {
        return condition;
    }

    @Override
    public int compareTo(FilterItem o) {
        return condition.displayOrder().getValue(Integer.MAX_VALUE) - o.condition.displayOrder().getValue(Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return condition.getMeta().getCaption();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        return condition == ((FilterItem) obj).condition;
    }

    @Override
    public int hashCode() {
        return 31 + ((condition == null) ? 0 : condition.hashCode());
    }

    public boolean isEditorShownOnAttach() {
        return editorShownOnAttach;
    }

    public void setEditorShownOnAttach(boolean editorShownOnAttach) {
        this.editorShownOnAttach = editorShownOnAttach;
    }
}
