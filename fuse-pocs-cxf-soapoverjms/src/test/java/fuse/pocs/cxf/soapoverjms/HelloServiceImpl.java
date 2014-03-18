package fuse.pocs.cxf.soapoverjms;

public class HelloServiceImpl implements HelloService {

    static final String RESPONSE = "Hello world!";

    @Override
    public String sayHello() {
        return RESPONSE;
    }

}