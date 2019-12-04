package com.rideal.api.ridealBackend.repositories;

import com.rideal.api.ridealBackend.models.Line;
import com.rideal.api.ridealBackend.models.UsageLogs;
import com.rideal.api.ridealBackend.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.validation.constraints.Null;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RepositoryRestResource(collectionResourceRel = "logs", path = "logs")
public interface UsageLogsRepository extends CrudRepository<UsageLogs, String> {
    List<UsageLogs> findAll();
    Optional<UsageLogs> findByUser(User user);
    Optional<UsageLogs> findByLine(Line line);
}
