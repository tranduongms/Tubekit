package org.dlac.tubekits.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dlac.tubekits.Bot;

@SuppressWarnings("serial")
public class NewLinkServlet extends HttpServlet {
	
	private Bot chrome;
	private Bot coccoc;
	
	public NewLinkServlet(Bot chrome, Bot coccoc) {
		super();
		this.chrome = chrome;
		this.coccoc = coccoc;
	}

    protected void doPost( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException,
                                          IOException
    {
    		String url = request.getParameter("url");
    		if ((url == null) || (!url.startsWith("https://www.youtube.com/watch?"))) {
    			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    			return;    			
    		}
    		Boolean willLikeOrDislike = request.getParameter("willLikeOrDislike").equals("true");
    		String comment = request.getParameter("comment");
    		double r = Math.random();
    		if (r<0.5) {
    			chrome.addLink(url, willLikeOrDislike, comment);
    			coccoc.addLink(url, willLikeOrDislike, null);
    		} else {
    			chrome.addLink(url, willLikeOrDislike, null);
    			coccoc.addLink(url, willLikeOrDislike, comment);
    		}
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("OK");
	}

}