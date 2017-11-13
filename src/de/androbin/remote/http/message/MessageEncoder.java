package de.androbin.remote.http.message;

import de.androbin.remote.http.message.Message.*;
import java.io.*;

public final class MessageEncoder {
  private MessageEncoder() {
  }
  
  private static void encodeHeaders( final Message message, final BufferedWriter output )
      throws IOException {
    final StringBuilder headers = new StringBuilder();
    message.headers.forEach( ( key, value ) -> {
      headers.append( key );
      headers.append( ": " );
      headers.append( value );
      headers.append( "\r\n" );
    } );
    output.write( headers.toString() );
  }
  
  public static void encodeRequest( final Request request, final BufferedWriter output )
      throws IOException {
    output.write( request.method.name() );
    output.write( ' ' );
    output.write( request.target );
    output.write( ' ' );
    output.write( request.version );
    output.write( '\r' );
    output.write( '\n' );
    
    encodeHeaders( request, output );
    
    output.write( '\r' );
    output.write( '\n' );
    
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
    output.write( '\r' );
    output.write( '\n' );
    
    encodeHeaders( response, output );
    
    output.write( '\r' );
    output.write( '\n' );
    
    if ( response.body != null ) {
      output.write( response.body );
    }
    
    output.flush();
  }
}