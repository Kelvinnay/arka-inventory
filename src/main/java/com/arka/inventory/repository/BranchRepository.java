package com.arka.inventory.repository;

import com.arka.inventory.entity.Branch;
import com.arka.inventory.entity.Inventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BranchRepository extends ReactiveCrudRepository<Branch, Long> {
}
