package de.androbin.remote.http;

import de.androbin.remote.*;
import de.androbin.remote.http.message.*;
import de.androbin.remote.http.message.Message.*;
import de.androbin.remote.http.message.Message.Request.*;
import de.androbin.remote.http.route.*;
import java.io.*;

public final class HTTPHandle implements Handle {
  private final Commander commander;
  private boolean terminate;
  
  public HTTPHandle( final RouteIndex routeIndex ) {
    commander = new Commander( routeIndex );
  }
  
  @ Override
  public boolean handle( final ServerContext server, final ClientContext client ) {
    final Request request;
    
    try {
      request = MessageDecoder.decodeRequest( client.input );
    } catch ( final IOException e ) {
      return false;
    }
    
    if ( request == null ) {
      return false;
    }
    
    server.log( "\t\t<request>" + request.method + " " + request.target + "</request>" );
    
    if ( request.method == Method.POST ) {
      switch ( request.target ) {
        case "/interrupt":
          commander.interrupt();
          return true;
        case "/terminate":
          stop();
        case "/disconnect":
          return false;
      }
    }
    
    commander.enqueue( request );
    
    final Response response = commander.dequeue();
    
    try {
      MessageEncoder.encodeResponse( response, client.output );
    } catch ( final IOException e ) {
      return false;
    }
    
    return true;
  }
  
  @ Override
  public boolean isTerminal() {
    return terminate;
  }
  
  @ Override
  public void start() {
    terminate = false;
    commander.start();
  }
  
  @ Override
  public void stop() {
    terminate = true;
    commander.stop();
  }
}