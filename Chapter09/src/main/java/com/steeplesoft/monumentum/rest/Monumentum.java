    package com.steeplesoft.monumentum.rest;

import com.steeplesoft.monumentum.rest.resource.AuthenticationResource;
import com.steeplesoft.monumentum.rest.resource.NoteResource;
import com.steeplesoft.monumentum.security.SecureFilter;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 *
 * @author jason
 */
@ApplicationPath("/api")
public class Monumentum extends javax.ws.rs.core.Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();
        s.add(NoteResource.class);
        s.add(AuthenticationResource.class);
        s.add(SecureFilter.class);
        return s;
    }
}
