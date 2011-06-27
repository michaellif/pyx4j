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
 * Created on 2011-06-27
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import java.util.Iterator;

/**
 * Allows gadget iteration for Dashboard and Report.
 * Supplies information about gadget column position.
 */
public interface IGadgetIterator extends Iterator<IGadget> {

    /**
     * Returns Gadget current column number;
     * 
     * In case of Report column values are:
     * -1 - for Full width gadget;
     * 0 - for left column gadget;
     * 1 - for right column one.
     */
    int getColumn();
}