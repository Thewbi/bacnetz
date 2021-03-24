package de.bacnetz.server.persistence.covsubscriptions;

import org.springframework.data.repository.CrudRepository;

public interface COVSubscriptionRepository extends CrudRepository<COVSubscriptionData, Long> {

    COVSubscriptionData findById(long id);

}
