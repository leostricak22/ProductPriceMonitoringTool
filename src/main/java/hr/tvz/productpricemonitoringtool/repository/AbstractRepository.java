package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.Entity;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractRepository <T extends Entity> {

    public abstract Optional<T> findById(Long id) throws RepositoryAccessException, DatabaseConnectionActiveException;
    public abstract Set<T> findAll() throws RepositoryAccessException, DatabaseConnectionActiveException;
    public abstract Set<T> save(Set<T> entities) throws RepositoryAccessException, DatabaseConnectionActiveException;

    public T save(T entity) throws RepositoryAccessException, DatabaseConnectionActiveException {
        Set<T> savedEntity = save(Set.of(entity));
        return savedEntity.stream().findFirst()
                .orElseThrow(() -> new RepositoryQueryException("Failed to save entity"));
    }
}