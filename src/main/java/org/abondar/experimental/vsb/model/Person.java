package org.abondar.experimental.vsb.model;

import java.util.UUID;

public class Person {

    private String name;
    private UUID uuid;
    private boolean active;

    public Person(String name, UUID uuid) {

        this.name = name;
        this.uuid = uuid;
        this.active =true;
    }

    public String getName() {

        return name;
    }
    public void setName(String name) {

        this.name = name;
    }
    public UUID getUuid() {

        return uuid;
    }
    public void setUuid(UUID uuid) {

        this.uuid = uuid;
    }

    public boolean isActive() {

        return active;
    }
    public void setActive(boolean active) {

        this.active = active;
    }

    @Override
    public String toString() {

        return "Person{"+
                "name='"+name+'\''+
                ", uuid="+uuid+
                ", active="+active+
                '}';
    }
}
