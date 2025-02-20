package hr.tvz.productpricemonitoringtool.model;

import java.io.Serializable;

/**
 * Entity model.
 * Represents the entity in the model.
 * Abstract class for all entities.
 * Contains id and name.
 */
public abstract class Entity implements Serializable {

    Long id;
    String name;

    protected Entity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
