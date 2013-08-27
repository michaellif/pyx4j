/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jul 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

public interface ReportWidget extends IsWidget {

    /** has to accept <code>null</code>, which means that the widget must reset it's state, i.e. clear everything */
    void setData(Object data, Command onWidgetReady);

    /** this is to be used along with data to store information about visual representation, i.e. scroll bar position */
    Object getMemento();

    void setMemento(Object memento, Command onWidgetReady);
}
