package org.dlac.tubekits;

import java.util.ArrayList;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.dlac.tubekits.servlets.ClearErrorsServlet;
import org.dlac.tubekits.servlets.ClearPlayedVideosServlet;
import org.dlac.tubekits.servlets.NewKeywordServlet;
import org.dlac.tubekits.servlets.NewLinkServlet;
import org.dlac.tubekits.servlets.NewPlaylistServlet;
import org.dlac.tubekits.servlets.StatusServlet;
import org.dlac.tubekits.servlets.UpdateSSEServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

public class App {

	private static Bot chrome;
	private static Bot coccoc;
	private static ArrayList<UpdateEventSource> updateEventSources;
	private static Server server;

	public App() throws Exception {

        updateEventSources = new ArrayList<UpdateEventSource>();
        chrome = new Bot("chrome", updateEventSources);
        coccoc = new Bot("coccoc", updateEventSources);
        server = new Server(8080);

        FilterHolder corsFilterHolder= new FilterHolder(CrossOriginFilter.class);
        corsFilterHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        corsFilterHolder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        corsFilterHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        corsFilterHolder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        corsFilterHolder.setName("cross-origin");
        
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addFilter(corsFilterHolder, "*", EnumSet.of(DispatcherType.REQUEST));
        
        context.addServlet(new ServletHolder(new UpdateSSEServlet(updateEventSources)),"/update");
        context.addServlet(new ServletHolder(new StatusServlet(chrome, coccoc)),"/status");
        context.addServlet(new ServletHolder(new NewKeywordServlet(chrome, coccoc)),"/keyword");
        context.addServlet(new ServletHolder(new NewLinkServlet(chrome, coccoc)),"/link");
        context.addServlet(new ServletHolder(new NewPlaylistServlet(chrome, coccoc)),"/playlist");
        context.addServlet(new ServletHolder(new ClearErrorsServlet(chrome, coccoc)),"/clearErrors");
        context.addServlet(new ServletHolder(new ClearPlayedVideosServlet(chrome, coccoc)),"/clearPlayedVideos");
        
        server.setHandler(context);
	}
	
	public void start() throws Exception {
        chrome.start();
        coccoc.start();
		server.start();
        server.join();
		System.out.println("\nServer is running!\n");
	}

    public static void main(String[] args) throws Exception {
    	
    		final App app = new App();
    		app.start();
    		
    		Runtime.getRuntime().addShutdownHook(new Thread()
    		{
                @Override
                public void run()
                {
                    System.out.println("Shutdown hook ran!");
                    if (App.chrome != null) App.chrome.quit();
                    if (App.coccoc != null) App.coccoc.quit();
                }
    		});
    }

}
