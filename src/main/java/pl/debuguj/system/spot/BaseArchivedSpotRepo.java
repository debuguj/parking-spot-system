package pl.debuguj.system.spot;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface BaseArchivedSpotRepo<T, ID extends Serializable> extends Repository<T, ID> {

    Optional<T> save(T entity);

}
