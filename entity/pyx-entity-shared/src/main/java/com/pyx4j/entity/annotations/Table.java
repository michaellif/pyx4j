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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

	public static enum PrimaryKeyStrategy {
		
		ASSIGNED,
		
		AUTO
	}
	
    /**
     * The name of the table or Entity kind for GAE.
     * 
     * Defaults to the entity name.
     */
    String name() default "";

    /**
     * Name prefix.
     */
    String prefix() default "";

    /**
     * Disable the use of ServerSideConfiguration.persistenceNamePrefix()
     */
    boolean disableGlobalPrefix() default false;
    
    PrimaryKeyStrategy  primaryKeyStrategy() default PrimaryKeyStrategy.AUTO; 
}
