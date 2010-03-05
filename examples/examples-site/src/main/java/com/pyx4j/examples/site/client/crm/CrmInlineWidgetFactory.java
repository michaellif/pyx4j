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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm;

import com.pyx4j.examples.rpc.Widgets;
import com.pyx4j.examples.site.client.ExamplesInlineWidgetFactory;
import com.pyx4j.examples.site.client.crm.customer.CustomerEditorWidget;
import com.pyx4j.examples.site.client.crm.customer.CustomerListWidget;
import com.pyx4j.examples.site.client.crm.dashboard.DashboardWidget;
import com.pyx4j.examples.site.client.crm.order.OrderEditorWidget;
import com.pyx4j.examples.site.client.crm.order.OrderListWidget;
import com.pyx4j.examples.site.client.crm.resource.ResourceEditorWidget;
import com.pyx4j.examples.site.client.crm.resource.ResourceListWidget;
import com.pyx4j.site.client.InlineWidget;

public class CrmInlineWidgetFactory extends ExamplesInlineWidgetFactory {

    @Override
    public InlineWidget createWidget(String widgetId) {
        if (Widgets.crm$dashboardWidget.name().equals(widgetId)) {
            return new DashboardWidget();
        } else if (Widgets.crm$customerListWidget.name().equals(widgetId)) {
            return new CustomerListWidget();
        } else if (Widgets.crm$customerEditorWidget.name().equals(widgetId)) {
            return new CustomerEditorWidget();
        } else if (Widgets.crm$orderListWidget.name().equals(widgetId)) {
            return new OrderListWidget();
        } else if (Widgets.crm$orderEditorWidget.name().equals(widgetId)) {
            return new OrderEditorWidget();
        } else if (Widgets.crm$resourceListWidget.name().equals(widgetId)) {
            return new ResourceListWidget();
        } else if (Widgets.crm$resourceEditorWidget.name().equals(widgetId)) {
            return new ResourceEditorWidget();
        } else {
            return null;
        }
    }

}
