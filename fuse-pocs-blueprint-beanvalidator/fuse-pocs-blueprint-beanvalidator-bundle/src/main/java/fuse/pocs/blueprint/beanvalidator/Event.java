package fuse.pocs.blueprint.beanvalidator;

import java.util.Arrays;
import java.util.List;

public class Event {

    @NoDuplicates("xxxx")
    private final List<String> attendees;

    public Event(String... attendees) {
        this.attendees = Arrays.asList(attendees);
    }

}