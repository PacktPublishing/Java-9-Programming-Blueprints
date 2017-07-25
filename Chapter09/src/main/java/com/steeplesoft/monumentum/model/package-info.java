@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type = LocalDateTime.class, 
                        value = LocalDateTimeAdapter.class)
})
package com.steeplesoft.monumentum.model;

import com.steeplesoft.monumentum.rest.LocalDateTimeAdapter;
import java.time.LocalDateTime;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

