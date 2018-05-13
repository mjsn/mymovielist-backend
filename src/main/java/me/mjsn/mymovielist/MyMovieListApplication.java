package me.mjsn.mymovielist;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyMovieListApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMovieListApplication.class, args);
	}


	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
		return server -> {
			if (server instanceof TomcatServletWebServerFactory) {
				server.addAdditionalTomcatConnectors(redirectConnector());
				server.addContextCustomizers((context) -> context.setUseHttpOnly(false));
				server.addContextCustomizers((context) -> context.setRequestCharacterEncoding("UTF-8"));
				server.addContextCustomizers((context) -> context.setResponseCharacterEncoding("UTF-8"));
			}
		};
	}

	private Connector redirectConnector() {
		Connector connector = new Connector("AJP/1.3");
		connector.setScheme("http");
		connector.setPort(9090);
		connector.setSecure(true);
		connector.setAllowTrace(false);
		connector.setURIEncoding("UTF-8");
		return connector;
	}

}