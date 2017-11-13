package de.androbin.remote.http;

import de.androbin.remote.http.message.*;
import de.androbin.remote.http.message.Message.*;
import de.androbin.remote.http.route.*;
import java.util.concurrent.*;
import java.util.function.*;

public final class Commander implements Runnable {
  public final RouteIndex routeIndex;
  
  private Thread thread;
  private volatile boolean running;
  
  private final BlockingQueue<Request> input;
  private final BlockingQueue<Supplier<Response>> output;
  
  public Commander( final RouteIndex routeIndex ) {
    this.routeIndex = routeIndex;
    
    this.input = new LinkedBlockingQueue<>();
    this.output = new LinkedBlockingQueue<>();
  }
  
  public Response dequeue() {
    final Supplier<Response> response;
    
    try {
      response = output.take();
    } catch ( final InterruptedException e ) {
      return Messages.RESPONSE_ERROR_500;
    }
    
    return response.get();
  }
  
  public void enqueue( final Request request ) {
    try {
      input.put( request );
    } catch ( final InterruptedException ignore ) {
    }
  }
  
  private void handle( final Request request ) {
    final String[] target = request.target.substring( 1 ).split( "/", -1 );
    output.add( routeIndex.handle( request, target ) );
  }
  
  public void interrupt() {
    if ( !running ) {
      return;
    }
    
    thread.interrupt();
  }
  
  @ Override
  public void run() {
    while ( running ) {
      final Request request;
      
      try {
        request = input.take();
      } catch ( final InterruptedException e ) {
        continue;
      }
      
      handle( request );
    }
  }
  
  public void start() {
    if ( running ) {
      return;
    }
    
    running = true;
    thread = new Thread( this, "Commander" );
    thread.setDaemon( true );
    thread.start();
  }
  
  public void stop() {
    if ( !running ) {
      return;
    }
    
    running = false;
    thread.interrupt();
    thread = null;
  }
}