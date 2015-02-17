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
 * Created on Aug 14, 2014
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.propertyvista.biz.system.DNSResolver;

import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSLookup {

    static Logger log = Logger.getLogger(DNSLookup.class.getName());

    public Lookup getLookupObj(String host, List<String> dnsServers) {
        Lookup lookupObj = null;

        try {
            lookupObj = new Lookup(host, Type.A, DClass.IN);
        } catch (TextParseException e) {
            // TODO Auto-generated catch block
            log.error("Error", e);
        }

        if (dnsServers != null && dnsServers.size() > 0) {
            ExtendedResolver extResolver = null;
            try {
                extResolver = new ExtendedResolver(dnsServers.toArray(new String[dnsServers.size()]));
            } catch (UnknownHostException e) {
                log.error(String.format("Some of DNS servers '%s' seems not to be up. ", dnsServers.toString()), e);
            }

            for (String dns : dnsServers) {
                //log.debug(String.format("Using '%s' DNS server resolving '%s'", dns, host.getHostName()));
            }

            lookupObj.setResolver(extResolver);

        } else
            log.debug(String.format("Using default DNS server resolving '%s'", host));

        return lookupObj;

    }

    public void analyzeLookupError(String host, int result) throws UnknownHostException {
        switch (result) {
        case Lookup.HOST_NOT_FOUND:
            throw new UnknownHostException(String.format("Host '%s' not found", host));
        case Lookup.TYPE_NOT_FOUND:
            throw new UnknownHostException(String.format("Host '%s' not found (no A record)", host));
        case Lookup.UNRECOVERABLE:
            throw new UnknownHostException(String.format("Cannot lookup host '%s'", host));
        case Lookup.TRY_AGAIN:
            throw new UnknownHostException(String.format("Temporary failure to lookup host '%s'", host));
        default:
            throw new UnknownHostException(String.format("Unknown error %s in host lookup of '%s'", result, host));
        }
    }
}
