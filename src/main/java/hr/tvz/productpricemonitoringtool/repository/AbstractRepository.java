package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.exception.RepositoryQueryException;
import hr.tvz.productpricemonitoringtool.model.Entity;

import java.util.Set;

public abstract class AbstractRepository <T extends Entity> {

    public abstract T findById(Long id) throws RepositoryAccessException;
    public abstract Set<T> findAll() throws RepositoryAccessException;
    public abstract Set<T> save(Set<T> entities) throws RepositoryAccessException;

    public T save(T entity) throws RepositoryAccessException {
        Set<T> savedEntity = save(Set.of(entity));
        return savedEntity.stream().findFirst()
                .orElseThrow(() -> new RepositoryQueryException("Failed to save entity"));
    }
}