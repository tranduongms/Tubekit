package org.dlac.tubekits.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dlac.tubekits.Bot;

@SuppressWarnings("serial")
public class NewKeywordServlet extends HttpServlet {
	
	private Bot chrome;
	private Bot coccoc;
	
	public NewKeywordServlet(Bot chrome, Bot coccoc) {
		super();
		this.chrome = chrome;
		this.coccoc = coccoc;
	}

    protected void doPost( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException,
                                          IOException
    	{
    		String keyword = request.getParameter("keyword");
    		if (keyword == null) {
    			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    			return;    			
    		}
    		Boolean willLikeOrDislike = request.getParameter("willLikeOrDislike").equals("true");
    		String comment = request.getParameter("comment");
    		double r = Math.random();
    		if (r<0.5) {
        		chrome.addKeyword(keyword, willLikeOrDislike, comment);
        		coccoc.addKeyword(keyword, willLikeOrDislike, null);	
    		} else {
        		chrome.addKeyword(keyword, willLikeOrDislike, null);
        		coccoc.addKeyword(keyword, willLikeOrDislike, comment);
    		}
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("OK");
	}

}

