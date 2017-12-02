package de.androbin.remote.http.message;

public abstract class Message {
  public final Headers headers;
  public final String body;
  
  private Message( final Builder builder ) {
    this.headers = new Headers( builder.headers );
    this.body = builder.body;
  }
  
  public static abstract class Builder {
    public final Headers headers;
    public String body;
    
    public Builder() {
      headers = new Headers();
    }
    
    public void setContentLengthHeader() {
      if ( body == null ) {
        headers.remove( "content-length" );
      } else {
        headers.set( "content-length", String.valueOf( body.length() ) );
      }
    }
    
    public abstract Message build();
  }
  
  public static final class Request extends Message {
    public final Method method;
    public final String target;
    public final String version;
    
    private Request( final Builder builder ) {
      super( builder );
      
      this.method = builder.method;
      this.target = builder.target;
      this.version = builder.version;
    }
    
    public enum Method {
      GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH;
    }
    
    public static final class Builder extends Message.Builder {
      public Method method = Method.GET;
      public String target = "/";
      public String version = "HTTP/1.1";
      
      @ Override
      public Request build() {
        sanitize();
        return new Request( this );
      }
      
      private void sanitize() {
        if ( method == null ) {
          throw new IllegalStateException( "method must be non-null" );
        }
        
        if ( target == null || target.isEmpty() ) {
          throw new IllegalStateException( "target must be non-null and non-empty" );
        }
        
        if ( version == null || version.isEmpty() ) {
          throw new IllegalStateException( "version must be non-null and non-empty" );
        }
      }
    }
  }
  
  public static final class Response extends Message {
    public final String version;
    public final int statusCode;
    public final String statusText;
    
    private Response( final Builder builder ) {
      super( builder );
      
      this.version = builder.version;
      this.statusCode = builder.statusCode;
      this.statusText = builder.statusText;
    }
    
    public static final class Builder extends Message.Builder {
      public String version = "HTTP/1.1";
      public int statusCode = 200;
      public String statusText = "OK";
      
      @ Override
      public Response build() {
        sanitize();
        return new Response( this );
      }
      
      private void sanitize() {
        if ( version == null || version.isEmpty() ) {
          throw new IllegalStateException( "version must be non-null and non-empty" );
        }
        
        if ( statusCode < 100 || statusCode > 999 ) {
          throw new IllegalStateException( "status code must be positive three-digit number" );
        }
        
        if ( statusText == null || statusText.isEmpty() ) {
          throw new IllegalStateException( "status text must be non-null and non-empty" );
        }
      }
    }
  }
}