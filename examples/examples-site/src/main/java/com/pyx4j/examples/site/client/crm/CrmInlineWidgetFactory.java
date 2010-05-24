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
 * Created on Apr 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm;

import com.pyx4j.examples.site.client.ExamplesWidgets.ExamplesCrmWidgets;
import com.pyx4j.examples.site.client.crm.customer.CustomerEditorWidget;
import com.pyx4j.examples.site.client.crm.customer.CustomerListWidget;
import com.pyx4j.examples.site.client.crm.order.OrderEditorWidget;
import com.pyx4j.examples.site.client.crm.order.OrderListWidget;
import com.pyx4j.examples.site.client.crm.resource.ResourceEditorWidget;
import com.pyx4j.examples.site.client.crm.resource.ResourceListWidget;
import com.pyx4j.examples.site.client.crm.user.UserEditorWidget;
import com.pyx4j.examples.site.client.crm.user.UserListWidget;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.InlineWidgetFactory;

public class CrmInlineWidgetFactory implements InlineWidgetFactory {

    @Override
    public InlineWidget createWidget(String widgetId) {
        ExamplesCrmWidgets id;
        try {
            id = ExamplesCrmWidgets.valueOf(widgetId);
        } catch (Throwable e) {
            return null;
        }
        switch (id) {
        case crm$customerListWidget:
            return new CustomerListWidget();
        case crm$customerEditorWidget:
            return new CustomerEditorWidget();
        case crm$orderListWidget:
            return new OrderListWidget();
        case crm$orderEditorWidget:
            return new OrderEditorWidget();
        case crm$repListWidget:
            return new ResourceListWidget();
        case crm$repEditorWidget:
            return new ResourceEditorWidget();
        case crm$userListWidget:
            return new UserListWidget();
        case crm$userEditorWidget:
            return new UserEditorWidget();
        default:
            return null;
        }
    }
}
