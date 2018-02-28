package org.dlac.tubekits.servlets;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.dlac.tubekits.UpdateEventSource;
import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;

@SuppressWarnings("serial")
public class UpdateSSEServlet extends EventSourceServlet {
	
	public ArrayList<UpdateEventSource> updateEventSources;
	
	public UpdateSSEServlet(ArrayList<UpdateEventSource> updateEventSources) {
		super();
		this.updateEventSources = updateEventSources;
	}
	
    @Override
    protected EventSource newEventSource(HttpServletRequest request)
    {
    	UpdateEventSource eventSource = new UpdateEventSource();
    	for (int i = 0; i < updateEventSources.size(); i++) {
			if (updateEventSources.get(i).closed) {
				updateEventSources.remove(i);
			}
		}
    	this.updateEventSources.add(eventSource);
        return eventSource;
    }
}

