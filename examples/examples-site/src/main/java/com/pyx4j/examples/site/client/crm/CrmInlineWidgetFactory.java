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
import com.pyx4j.examples.site.client.crm.customer.CustomersWidget;
import com.pyx4j.examples.site.client.crm.dashboard.DashboardWidget;
import com.pyx4j.examples.site.client.crm.order.OrdersWidget;
import com.pyx4j.examples.site.client.crm.resource.ResourcesWidget;
import com.pyx4j.site.client.InlineWidget;
import com.pyx4j.site.client.InlineWidgetFactory;

public class CrmInlineWidgetFactory extends InlineWidgetFactory {

    @Override
    public InlineWidget createWidget(String widgetId) {
        if (Widgets.crm$dashboardWidget.name().equals(widgetId)) {
            return new DashboardWidget();
        } else if (Widgets.crm$customersWidget.name().equals(widgetId)) {
            return new CustomersWidget();
        } else if (Widgets.crm$ordersWidget.name().equals(widgetId)) {
            return new OrdersWidget();
        } else if (Widgets.crm$resourcesWidget.name().equals(widgetId)) {
            return new ResourcesWidget();
        } else {
            return null;
        }
    }

}
