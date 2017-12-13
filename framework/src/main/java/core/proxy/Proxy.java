package core.proxy;

/**
 * Created by ChaoChao on 13/12/2017.
 */
public interface Proxy {

    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
