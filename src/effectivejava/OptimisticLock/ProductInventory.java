package effectivejava.OptimisticLock;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Entity using Optimistic Locking via JPA @Version annotation.
 * JPA automatically increments the version column upon every UPDATE.
 * If another transaction changed the version in the meantime, an OptimisticLockException is thrown.
 */
@Entity
@Table(name = "product_inventory")
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Integer stock;

    /**
     * @Version tells JPA / Hibernate to track entity versioning.
     * When Hibernate updates this row, it adds 'WHERE id=? AND version=?' to the SQL.
     */
    @Version
    private Long version;

    public ProductInventory() {
    }

    public ProductInventory(String productName, Integer stock) {
        this.productName = productName;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ProductInventory{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", stock=" + stock +
                ", version=" + version +
                '}';
    }
}
