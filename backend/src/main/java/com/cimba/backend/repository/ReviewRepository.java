package com.cimba.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cimba.backend.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
