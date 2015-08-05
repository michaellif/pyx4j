/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jun 10, 2015
 * @author vlads
 */
package com.pyx4j.config.server.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleRegistry {

    private List<IModule> registry = new ArrayList<>();

    private static class SingletonHolder {
        public static final ModuleRegistry INSTANCE = new ModuleRegistry();
    }

    static ModuleRegistry instance() {
        return SingletonHolder.INSTANCE;
    }

    private ModuleRegistry() {
    }

    public static void register(IModule module) {
        instance().registry.add(module);
        module.register();
    }

    public static synchronized void init() {
        for (IModule module : instance().registry) {
            module.init();
        }
    }

    public static synchronized void shutdown() {
        List<IModule> reverseList = new ArrayList<>(instance().registry);
        Collections.reverse(reverseList);
        for (IModule module : reverseList) {
            module.shutdown();
        }
    }
}
