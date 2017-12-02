package de.androbin.remote.http.message;

import de.androbin.remote.http.message.Message.*;

public final class Messages {
  public static final Response RESPONSE_ERROR_204;
  public static final Response RESPONSE_ERROR_404;
  public static final Response RESPONSE_ERROR_500;
  
  static {
    final Response.Builder response = new Response.Builder();
    response.headers.set( "access-control-allow-origin", "*" );
    
    response.statusCode = 204;
    response.statusText = "No Content";
    RESPONSE_ERROR_204 = response.build();
    
    response.statusCode = 404;
    response.statusText = "Not Found";
    RESPONSE_ERROR_404 = response.build();
    
    response.statusCode = 500;
    response.statusText = "Internal Server Error";
    RESPONSE_ERROR_500 = response.build();
  }
  
  private Messages() {
  }
}