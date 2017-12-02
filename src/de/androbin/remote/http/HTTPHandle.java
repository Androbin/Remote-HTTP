package de.androbin.remote.http;

import de.androbin.remote.*;
import de.androbin.remote.http.message.*;
import de.androbin.remote.http.message.Message.*;
import de.androbin.remote.http.message.Message.Request.*;
import de.androbin.remote.http.route.*;
import java.io.*;

public final class HTTPHandle implements Handle {
  private final Commander commander;
  
  private boolean running;
  private boolean terminate;
  
  public HTTPHandle( final RouteIndex routeIndex ) {
    commander = new Commander( routeIndex );
  }
  
  @ Override
  public void handleInput( final ServerContext server, final ClientContext client ) {
    final Request request;
    
    try {
      request = MessageDecoder.decodeRequest( client.input );
    } catch ( final IOException e ) {
      stop();
      return;
    }
    
    if ( request == null ) {
      stop();
      return;
    }
    
    server.log( "\t\t<request>" + request.method + " " + request.target + "</request>" );
    
    if ( request.method == Method.POST ) {
      switch ( request.target ) {
        case "/interrupt":
          commander.interrupt();
          return;
        case "/terminate":
          terminate = true;
        case "/disconnect":
          stop();
          return;
      }
    }
    
    commander.enqueue( request );
  }
  
  @ Override
  public void handleOutput( final ServerContext server, final ClientContext client ) {
    final Response response = commander.dequeue();
    
    try {
      MessageEncoder.encodeResponse( response, client.output );
    } catch ( final IOException e ) {
      stop();
    }
  }
  
  @ Override
  public boolean isRunning() {
    return running;
  }
  
  @ Override
  public boolean isTerminal() {
    return terminate;
  }
  
  @ Override
  public void start() {
    running = true;
    commander.start();
  }
  
  @ Override
  public void stop() {
    running = false;
    commander.stop();
  }
}