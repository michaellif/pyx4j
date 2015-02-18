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
 * Created on Aug 15, 2014
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.propertyvista.biz.system.DNSResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xbill.DNS.Lookup;

public class JavaResolver implements DNSResolver {

    private List<String> dnsServers = new ArrayList<String>();

    public JavaResolver(List<String> dnsServers) {
        this.dnsServers = dnsServers;
    }

    @Override
    public String resolveHost(String targetHost) throws IOException, InterruptedException {

        if (targetHost == null) {
            return null;
        }

        String ipAddress = "";

        Lookup lookupObj = DNSLookup.getLookupObj(targetHost, dnsServers);

        lookupObj.run();

        int result = lookupObj.getResult();

        if (result == Lookup.SUCCESSFUL) {
            ipAddress = lookupObj.getAnswers()[0].rdataToString();
        } else {
            try {
                DNSLookup.analyzeLookupErrorForHost(targetHost, result);
            } catch (Throwable e) {
                throw e;
            }
        }

        return ipAddress;
    }

    public List<String> getDnsServers() {
        return dnsServers;
    }

    public void setDnsServers(List<String> dnsServers) {
        this.dnsServers = dnsServers;
    }

}
