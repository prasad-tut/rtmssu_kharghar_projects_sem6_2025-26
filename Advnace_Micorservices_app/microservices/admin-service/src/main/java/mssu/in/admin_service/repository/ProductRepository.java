package mssu.in.admin_service.repository;

import mssu.in.admin_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByCreatedAtDesc();

    List<Product> findByAdminIdOrderByCreatedAtDesc(Long adminId);

    /**
     * Lightweight query for customer catalog - only selects essential fields,
     * excluding aiOverviewJson and manualOverview to reduce database transfer time.
     * This query returns partial Product entities that should only be used with
     * ProductSummaryResponse.
     */
    @Query("SELECT new mssu.in.admin_service.entity.Product(p.id, p.productType, p.modelName, p.mainColor, p.otherColors, p.maxPrice, p.imageUrl) FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findAllForCatalog();
}
