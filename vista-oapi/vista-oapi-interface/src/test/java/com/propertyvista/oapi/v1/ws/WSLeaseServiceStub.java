package com.propertyvista.oapi.v1.ws;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

import com.propertyvista.oapi.v1.Version;

@WebServiceClient(name = "WSLeaseServiceImplService", targetNamespace = "http://ws." + Version.VERSION_NAME + ".oapi.propertyvista.com/", wsdlLocation = "http://localhost:8888/WS/WSLeaseService?wsdl")
public class WSLeaseServiceStub extends Service {

    private final static URL IMPL_SERVICE_WSDL_LOCATION;

    private final static WebServiceException IMPL_SERVICE_EXCEPTION;

    private final static QName IMPL_SERVICE_QNAME = new QName("http://ws." + Version.VERSION_NAME + ".oapi.propertyvista.com/", "WSLeaseServiceImplService");

    private final static QName IMPL_PORT_QNAME = new QName("http://ws." + Version.VERSION_NAME + ".oapi.propertyvista.com/", "WSLeaseServiceImplPort");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8888/WS/LeaseService?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        IMPL_SERVICE_WSDL_LOCATION = url;
        IMPL_SERVICE_EXCEPTION = e;
    }

    public WSLeaseServiceStub() {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME);
    }

    public WSLeaseServiceStub(WebServiceFeature... features) {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME, features);
    }

    public WSLeaseServiceStub(URL wsdlLocation) {
        super(wsdlLocation, IMPL_SERVICE_QNAME);
    }

    public WSLeaseServiceStub(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, IMPL_SERVICE_QNAME, features);
    }

    public WSLeaseServiceStub(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public WSLeaseServiceStub(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "WSLeaseServiceImplPort")
    public WSLeaseService getLeaseServicePort() {
        return super.getPort(IMPL_PORT_QNAME, WSLeaseService.class);
    }

    @WebEndpoint(name = "WSLeaseServiceImplPort")
    public WSLeaseService getLeaseServicePort(WebServiceFeature... features) {
        return super.getPort(IMPL_PORT_QNAME, WSLeaseService.class, features);
    }

    private static URL getWsdlLocation() {
        if (IMPL_SERVICE_EXCEPTION != null) {
            throw IMPL_SERVICE_EXCEPTION;
        }
        return IMPL_SERVICE_WSDL_LOCATION;
    }

}
