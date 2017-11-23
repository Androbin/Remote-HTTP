package de.androbin.remote.http.message;

import de.androbin.remote.http.message.Message.*;
import de.androbin.remote.http.message.Message.Request.*;
import de.androbin.util.*;
import java.io.*;

public final class MessageDecoder {
  private static final String CONTENT_LENGTH = "Content-Length";
  
  private MessageDecoder() {
  }
  
  private static String decodeBody( final BufferedReader input, final int length )
      throws IOException {
    final char[] chars = new char[ length ];
    input.read( chars, 0, length );
    return new String( chars );
  }
  
  private static Pair<String, String> decodeHeader( final String line ) throws IOException {
    final int index = line.indexOf( ':' );
    
    final String key = line.substring( 0, index );
    final String value = line.substring( index + 1 ).trim();
    
    return new Pair<>( key, value );
  }
  
  private static void decodeHeaders( final BufferedReader input, final Message.Builder message )
      throws IOException {
    String line;
    
    while ( !( line = input.readLine() ).isEmpty() ) {
      final Pair<String, String> header = decodeHeader( line );
      message.headers.add( header.key, header.value );
    }
  }
  
  public static Request decodeRequest( final BufferedReader input ) throws IOException {
    final String requestLine = input.readLine();
    
    if ( requestLine == null ) {
      return null;
    }
    
    final Request.Builder request = new Request.Builder();
    final String[] requestLineSplit = requestLine.split( " " );
    
    request.method = Method.valueOf( requestLineSplit[ 0 ] );
    request.target = requestLineSplit[ 1 ];
    request.version = requestLineSplit[ 2 ];
    
    decodeHeaders( input, request );
    
    if ( request.headers.contains( CONTENT_LENGTH ) ) {
      final int contentLength = Integer.parseInt( request.headers.getOne( CONTENT_LENGTH ) );
      request.body = decodeBody( input, contentLength );
    }
    
    return request.build();
  }
  
  public static Response decodeResponse( final BufferedReader input ) throws IOException {
    final String statusLine = input.readLine();
    
    if ( statusLine == null ) {
      return null;
    }
    
    final Response.Builder response = new Response.Builder();
    final String[] statusLineSplit = statusLine.split( " " );
    
    response.version = statusLineSplit[ 0 ];
    response.statusCode = Integer.parseInt( statusLineSplit[ 1 ] );
    response.statusText = statusLineSplit[ 2 ];
    
    decodeHeaders( input, response );
    
    if ( response.headers.contains( CONTENT_LENGTH ) ) {
      final int contentLength = Integer.parseInt( response.headers.getOne( CONTENT_LENGTH ) );
      response.body = decodeBody( input, contentLength );
    }
    
    return response.build();
  }
}