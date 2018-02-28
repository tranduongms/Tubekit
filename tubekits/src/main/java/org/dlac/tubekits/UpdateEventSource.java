package org.dlac.tubekits;

import java.io.IOException;

import org.eclipse.jetty.servlets.EventSource;

public class UpdateEventSource implements EventSource{
	
	private Emitter emitter;
	public Boolean closed = false;

	public void onClose() {
		this.closed = true;
	}
	
	public void send(String data) {
		if(this.closed || (this.emitter == null)) return;
		try {
			this.emitter.data(data);
		} catch (IOException e) {
		}
	}

	public void onOpen(Emitter emitter) throws IOException {
		this.closed = false;
		this.emitter = emitter;
	}
}
