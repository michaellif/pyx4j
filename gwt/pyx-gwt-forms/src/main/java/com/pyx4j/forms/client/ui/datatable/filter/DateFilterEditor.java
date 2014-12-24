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
 * Created on Dec 22, 2014
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;

public class DateFilterEditor extends FilterEditorBase implements IFilterEditor {

    public DateFilterEditor(IObject<?> member) {
        super(member);
        initWidget(new FlowPanel());
    }

    @Override
    public PropertyCriterion getCriterion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCriterion(Criterion filterCriterion) {
        // TODO Auto-generated method stub

    }

}
