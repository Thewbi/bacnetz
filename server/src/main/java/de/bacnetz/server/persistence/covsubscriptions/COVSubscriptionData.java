package de.bacnetz.server.persistence.covsubscriptions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * https://spring.io/guides/gs/accessing-data-jpa/
 * 
 * JPA for DefaultCOVSubscription.
 *
 */
@Entity
public class COVSubscriptionData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Protected ctor for JPA.
     */
    public COVSubscriptionData() {
    }

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "COVSubscriptionData [id=" + id + ", ip=" + ip + "]";
    }

}
