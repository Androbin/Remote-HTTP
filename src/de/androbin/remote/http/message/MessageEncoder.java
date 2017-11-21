package de.androbin.remote.http.message;

import de.androbin.remote.http.message.Message.*;
import de.androbin.util.*;
import java.io.*;
import java.util.*;

public final class MessageEncoder {
  private MessageEncoder() {
  }
  
  private static void encodeHeaders( final Message message, final BufferedWriter output )
      throws IOException {
    for ( final Map.Entry<NoCaseString, String> entry : message.headers ) {
      output.write( entry.getKey().value );
      output.write( ": " );
      output.write( entry.getValue() );
      output.write( "\r\n" );
    }
  }
  
  public static void encodeRequest( final Request request, final BufferedWriter output )
      throws IOException {
    output.write( request.method.name() );
    output.write( ' ' );
    output.write( request.target );
    output.write( ' ' );
    output.write( request.version );
    output.write( "\r\n" );
    
    encodeHeaders( request, output );
    
    output.write( "\r\n" );
    
    if ( request.body != null ) {
      output.write( request.body );
    }
    
    output.flush();
  }
  
  public static void encodeResponse( final Response response, final BufferedWriter output )
      throws IOException {
    output.write( response.version );
    output.write( ' ' );
    output.write( String.valueOf( response.statusCode ) );
    output.write( ' ' );
    output.write( response.statusText );
    output.write( "\r\n" );
    
    encodeHeaders( response, output );
    
    output.write( "\r\n" );
    
    if ( response.body != null ) {
      output.write( response.body );
    }
    
    output.flush();
  }
}