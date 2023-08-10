package tobyString.helloboot;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class MySpringApplication {

    public static void run(Class<?> applicationClass, String... args) {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext(){
            @Override
            protected void onRefresh() {
                super.onRefresh();
                ServletWebServerFactory serverFactory = this.getBean(ServletWebServerFactory.class) ;
                DispatcherServlet dispatcherServlet = this.getBean(DispatcherServlet.class) ;

                WebServer webServer = serverFactory
                        .getWebServer(servletContext -> {
                            servletContext.addServlet("dispatcherServlet", dispatcherServlet).addMapping("/*");  // addServlet
                        }) ; // getWebServer
                webServer.start();
            } // onRefresh
        };
        applicationContext.register(applicationClass);
        applicationContext.refresh(); // 템플릿 메서드  => 훅 메서드: onRefresh
    }
}
