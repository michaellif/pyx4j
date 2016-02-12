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
 * Created on Feb 12, 2016
 * @author vlads
 */
package com.pyx4j.junitcategories;

/**
 * JUnit Category is excluded from running tests in default maven build.
 * - Excluded in WAR build
 * - Excluded in CI build
 * - Excluded in -P !dev build (b-all-tests.cmd)
 *
 * Intended for:
 * - functionality that needs to be tested before production
 * - slow and time consuming tests that can't be optimized
 * - testing timeouts
 * - testing realtime flows
 */
public interface LongTest {

}
