package org.dlac.tubekits.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dlac.tubekits.Bot;

@SuppressWarnings("serial")
public class StatusServlet extends HttpServlet {
	
	private Bot chrome;
	private Bot coccoc;
	
	public StatusServlet(Bot chrome, Bot coccoc) {
		super();
		this.chrome = chrome;
		this.coccoc = coccoc;
	}

    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
                                                        IOException
    {
        response.setContentType("text/json");
        response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = response.getWriter();
		writer.println("{");
		if(chrome != null) {
			writer.print("\"chrome\": ");
			writer.print(chrome.getStatus());
			if (coccoc != null) {
				writer.print(",\n");
			}
		}
		if(coccoc != null) {
			writer.print("\"coccoc\": ");
			writer.print(coccoc.getStatus());
		}
		writer.println("}");
    }

}