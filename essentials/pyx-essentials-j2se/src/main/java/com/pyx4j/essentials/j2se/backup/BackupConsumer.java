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
 * Created on 2010-08-12
 * @author vlads
 */
package com.pyx4j.essentials.j2se.backup;

import java.util.HashMap;
import java.util.Vector;

import com.pyx4j.essentials.rpc.admin.BackupEntityProperty;

public interface BackupConsumer {

    public void start();

    public void save(Vector<HashMap<String, BackupEntityProperty>> records);

    public void end();

}
