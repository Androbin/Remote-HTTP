package de.androbin.util;

public final class NoCaseString {
  public final String value;
  
  public NoCaseString( final String value ) {
    this.value = value;
  }
  
  @ Override
  public boolean equals( final Object obj ) {
    if ( !( obj instanceof NoCaseString ) ) {
      return false;
    }
    
    final NoCaseString string = (NoCaseString) obj;
    return string.value.equalsIgnoreCase( value );
  }
  
  @ Override
  public int hashCode() {
    return value.toLowerCase().hashCode();
  }
}