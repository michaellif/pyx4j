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
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.crud.IEntityEditorPanel;
import com.pyx4j.examples.domain.crm.Customer;

public class CustomerEditorWidget extends HorizontalPanel implements IEntityEditorPanel<Customer> {

    public CustomerEditorWidget() {
        VerticalPanel editorContent = new VerticalPanel();
        add(editorContent);

        CustomerEditorMapPanel maps = new CustomerEditorMapPanel();
        add(maps);
    }

}
