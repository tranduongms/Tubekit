package org.dlac.tubekits.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dlac.tubekits.Bot;

@SuppressWarnings("serial")
public class ClearErrorsServlet extends HttpServlet {
	
	private Bot chrome;
	private Bot coccoc;
	
	public ClearErrorsServlet(Bot chrome, Bot coccoc) {
		super();
		this.chrome = chrome;
		this.coccoc = coccoc;
	}

    protected void doPost( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException,
                                          IOException
    	{
    		chrome.clearErrors();
    		chrome.updateStatus();
    		coccoc.clearErrors();
    		coccoc.updateStatus();
    		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("OK");
	}

}