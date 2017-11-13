package de.androbin.remote.http;

import de.androbin.remote.http.message.*;
import de.androbin.remote.http.message.Message.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public final class NetworkHelper implements Runnable {
  private static final String IP_REGEX = "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
  
  private Thread thread;
  private volatile boolean running;
  
  private final BlockingQueue<Request> queue;
  
  private Socket socket;
  
  private BufferedReader inputStream;
  private BufferedWriter outputStream;
  
  public NetworkHelper() {
    this.queue = new LinkedBlockingQueue<>();
  }
  
  public static boolean checkAddress( final String ip, final int port ) {
    return ip.matches( IP_REGEX ) && port >= 1024 && port < 49152;
  }
  
  @ Override
  public void run() {
    while ( running ) {
      final Request request;
      
      try {
        request = queue.take();
      } catch ( final InterruptedException e ) {
        continue;
      }
      
      try {
        MessageEncoder.encodeRequest( request, outputStream );
      } catch ( final IOException ignore ) {
        running = false;
        continue;
      }
      
      try {
        MessageDecoder.decodeResponse( inputStream );
      } catch ( final IOException ignore ) {
        running = false;
      }
    }
  }
  
  public void send( final Request request, final boolean keep ) {
    if ( !running && !keep ) {
      return;
    }
    
    try {
      queue.put( request );
    } catch ( final InterruptedException ignore ) {
    }
  }
  
  public boolean start( final String ip, final int port ) {
    if ( running ) {
      return true;
    }
    
    try {
      socket = new Socket( ip, port );
      inputStream = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
      outputStream = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );
    } catch ( final IOException e ) {
      return false;
    }
    
    running = true;
    thread = new Thread( this, "NetworkHelper" );
    thread.setDaemon( true );
    thread.start();
    return true;
  }
  
  public boolean stop() {
    if ( !running ) {
      return true;
    }
    
    running = false;
    thread.interrupt();
    thread = null;
    
    try {
      socket.close();
    } catch ( final IOException e ) {
      return false;
    }
    
    return true;
  }
}