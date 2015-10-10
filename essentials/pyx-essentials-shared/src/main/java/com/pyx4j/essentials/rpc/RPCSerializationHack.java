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
 * Created on May 29, 2010
 * @author vlads
 */
package com.pyx4j.essentials.rpc;

/**
 * This is temporary class to find and fix GWT serialization stability problems.
 * e.g. different list .gwt.rpc created in hosted mode. or at with different builds.
 * 
 * at the GWT version 2.6.1 this is not used.
 * 
 */
//@SuppressWarnings("serial")
public class RPCSerializationHack /* implements java.io.Serializable */ {

    // removed in next GWT version 2.6.1, Loogs like works fine
    // public Criterion[] criterionArray;

}
