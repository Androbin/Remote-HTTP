package de.androbin.remote.http.route;

import de.androbin.remote.http.message.Message.*;
import java.util.function.*;

public interface Route {
  Supplier<Response> handle( Request request, String[] target );
}