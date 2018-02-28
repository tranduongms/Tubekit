package org.dlac.tubekits.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dlac.tubekits.Bot;

@SuppressWarnings("serial")
public class NewPlaylistServlet extends HttpServlet {
	
	private Bot chrome;
	private Bot coccoc;
	
	public NewPlaylistServlet(Bot chrome, Bot coccoc) {
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
    		Boolean loop = request.getParameter("loop").equals("true");
    		Boolean shuffle = request.getParameter("shuffle").equals("true");
    		Boolean autoNext = request.getParameter("autoNext").equals("true");
    		
    		chrome.addPlaylist(url, loop, shuffle, autoNext);
    		coccoc.addPlaylist(url, loop, shuffle, autoNext);
    		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().print("OK");
	}

}