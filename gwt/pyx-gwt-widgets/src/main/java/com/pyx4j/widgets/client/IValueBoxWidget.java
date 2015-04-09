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
 * Created on Oct 21, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;

public interface IValueBoxWidget<E> extends IFocusWidget, IWatermarkWidget {

    HandlerRegistration addValueChangeHandler(ValueChangeHandler<E> handler);

    E getValue();

    void setValue(E value);

    void setParser(IParser<E> parser);

    void setFormatter(IFormatter<E, String> formatter);

    boolean isParsedOk();

    String getParseExceptionMessage();
}
