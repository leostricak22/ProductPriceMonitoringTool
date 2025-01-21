package hr.tvz.productpricemonitoringtool.repository;

import hr.tvz.productpricemonitoringtool.exception.RepositoryAccessException;
import hr.tvz.productpricemonitoringtool.model.Entity;

import java.util.Set;

public abstract class AbstractRepository <T extends Entity> {

    public abstract T findById(Long id) throws RepositoryAccessException;
    public abstract Set<T> findAll() throws RepositoryAccessException;
    public abstract void save(Set<T> entities) throws RepositoryAccessException;
    public void save(T entity) throws RepositoryAccessException {
        save(Set.of(entity));
    }
}