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
 * Created on May 27, 2014
 * @author vlads
 */
package com.pyx4j.commons;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Added to make class GWT Serializable. of filed is set to not final
 * 
 * Marker for Hacks we need to do to make GWT serialization work
 * http://code.google.com/p/google-web-toolkit/issues/detail?id=1054
 * 
 * Should be fixed very soon in GWT 2.7.
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR })
public @interface GWTSerializable {

}
