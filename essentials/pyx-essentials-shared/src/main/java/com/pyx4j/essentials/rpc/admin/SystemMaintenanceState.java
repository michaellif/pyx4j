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
 * Created on Sep 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.admin;

import java.sql.Time;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.rpc.SystemState;

@Transient
public interface SystemMaintenanceState extends IEntity {

    @ReadOnly
    IPrimitive<Boolean> inEffect();

    @NotNull
    IPrimitive<SystemState> type();

    @NotNull
    IPrimitive<SystemState> externalConnections();

    IPrimitive<LogicalDate> startDate();

    @Editor(type = Editor.EditorType.timepicker)
    @Format("HH:mm")
    IPrimitive<Time> startTime();

    @Caption(name = "Grace Period (min)")
    IPrimitive<Long> gracePeriod();

    @Caption(name = "Duration (min)")
    IPrimitive<Integer> duration();

    @Caption(name = "Message to Users")
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> message();

}
