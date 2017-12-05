package de.androbin.remote.http.message;

import de.androbin.remote.http.message.Message.*;

public final class Messages {
  public static final String STATUS_TEXT_200 = "OK";
  public static final String STATUS_TEXT_204 = "No Content";
  public static final String STATUS_TEXT_404 = "Not Found";
  public static final String STATUS_TEXT_500 = "Internal Server Error";
  
  public static final Response RESPONSE_ERROR_204;
  public static final Response RESPONSE_ERROR_404;
  public static final Response RESPONSE_ERROR_500;
  
  static {
    final Response.Builder response = new Response.Builder();
    response.headers.set( "access-control-allow-origin", "*" );
    response.setContentLengthHeader();
    
    response.statusCode = 204;
    response.statusText = STATUS_TEXT_204;
    RESPONSE_ERROR_204 = response.build();
    
    response.statusCode = 404;
    response.statusText = STATUS_TEXT_404;
    RESPONSE_ERROR_404 = response.build();
    
    response.statusCode = 500;
    response.statusText = STATUS_TEXT_500;
    RESPONSE_ERROR_500 = response.build();
  }
  
  private Messages() {
  }
}