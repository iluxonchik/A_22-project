package pt.upa.broker.ws;


import java.util.List;

public class BrokerPort implements pt.upa.broker.ws.BrokerPortType {
    @Override
    public String ping(String name) {
        return null;
    }

    @Override
    public String requestTransport(String origin, String destination, int price) throws pt.upa.broker.ws.InvalidPriceFault_Exception, pt.upa.broker.ws.UnavailableTransportFault_Exception, pt.upa.broker.ws.UnavailableTransportPriceFault_Exception, pt.upa.broker.ws.UnknownLocationFault_Exception {
        return null;
    }

    @Override
    public pt.upa.broker.ws.TransportView viewTransport(String id) throws pt.upa.broker.ws.UnknownTransportFault_Exception {
        return null;
    }

    @Override
    public List<pt.upa.broker.ws.TransportView> listTransports() {
        return null;
    }

    @Override
    public void clearTransports() {

    }

}
