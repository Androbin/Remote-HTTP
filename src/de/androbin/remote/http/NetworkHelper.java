package de.androbin.remote.http;

import de.androbin.remote.http.message.*;
import de.androbin.remote.http.message.Message.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

public final class NetworkHelper {
  private static final String IP_REGEX = "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
  
  private Thread senderThread;
  private Thread receiverThread;
  private volatile boolean running;
  
  private Socket socket;
  
  private BufferedWriter outputStream;
  private BufferedReader inputStream;
  
  private final BlockingQueue<Supplier<Request>> output;
  private final BlockingQueue<Response> input;
  
  public NetworkHelper() {
    this.input = new LinkedBlockingQueue<>();
    this.output = new LinkedBlockingQueue<>();
  }
  
  public static boolean checkAddress( final String ip, final int port ) {
    return ip.matches( IP_REGEX ) && port >= 1024 && port < 49152;
  }
  
  public Response receive() {
    try {
      return input.take();
    } catch ( final InterruptedException e ) {
      return null;
    }
  }
  
  public void runReceiver() {
    while ( running ) {
      final Response response;
      
      try {
        response = MessageDecoder.decodeResponse( inputStream );
      } catch ( final IOException ignore ) {
        running = false;
        continue;
      }
      
      try {
        input.put( response );
      } catch ( final InterruptedException e ) {
        running = false;
      }
    }
  }
  
  public void runSender() {
    while ( running ) {
      final Supplier<Request> request;
      
      try {
        request = output.take();
      } catch ( final InterruptedException e ) {
        continue;
      }
      
      try {
        MessageEncoder.encodeRequest( request.get(), outputStream );
      } catch ( final IOException e ) {
        running = false;
      }
    }
  }
  
  public void send( final Supplier<Request> request ) {
    try {
      output.put( request );
    } catch ( final InterruptedException ignore ) {
    }
  }
  
  public Response sendAndReceive( final Lock lock, final Supplier<Request> request ) {
    lock.lock();
    send( request );
    
    synchronized ( this ) {
      lock.unlock();
      return receive();
    }
  }
  
  public boolean start( final String ip, final int port ) {
    if ( running ) {
      return true;
    }
    
    try {
      socket = new Socket( ip, port );
      outputStream = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() ) );
      inputStream = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
    } catch ( final IOException e ) {
      return false;
    }
    
    running = true;
    
    senderThread = new Thread( this::runSender, "NetworkHelper Sender" );
    senderThread.setDaemon( true );
    senderThread.start();
    
    receiverThread = new Thread( this::runReceiver, "NetworkHelper Receiver" );
    receiverThread.setDaemon( true );
    receiverThread.start();
    
    return true;
  }
  
  public boolean stop() {
    if ( !running ) {
      return true;
    }
    
    running = false;
    
    senderThread.interrupt();
    senderThread = null;
    
    receiverThread.interrupt();
    receiverThread = null;
    
    try {
      socket.close();
    } catch ( final IOException e ) {
      return false;
    }
    
    return true;
  }
}