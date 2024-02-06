package tobyspring.config.autoConfig;

import org.springframework.stereotype.Component;
import tobyspring.config.MyConfigurationProperties;

//@Component
@MyConfigurationProperties(prefix="server") // 이 아래 property들에 대한 namespace 역할 (파키지 같은 역할)
public class ServerProperties {
    String contextPath;

    int port;

    public String getContextPath() {
        return contextPath;
    }

    public int getPort() {
        return port;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
