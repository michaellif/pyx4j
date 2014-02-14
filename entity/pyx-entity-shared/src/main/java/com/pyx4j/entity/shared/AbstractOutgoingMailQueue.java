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
 * Created on Feb 13, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared;

import java.io.Serializable;
import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AbstractOutgoingMailQueue extends IEntity {

    public static enum MailQueueStatus implements Serializable {

        Queued,

        Success,

        Cancelled,

        GiveUp
    }

    IPrimitive<MailQueueStatus> status();

    IPrimitive<String> namespace();

    IPrimitive<String> configurationId();

    IPrimitive<String> statusCallbackClass();

    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();

    /// last delivery attempt
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    IPrimitive<Integer> attempts();

    @Length(15 * 1024 * 1024)
    @RpcTransient
    IPrimitive<byte[]> data();

}
