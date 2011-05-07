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

import java.text.ParseException;

public interface IFormat<E> {

    public String format(E value);

    /**
     * Note on triple logic return/except behaviour:
     * 
     * @param string
     *            input string to parse
     * @return null - if input string is empty (null or ""); parsed E-type data if ok and
     * @throws ParseException
     *             if parsing is failed!..
     */
    public E parse(String string) throws ParseException;
}
