package com.arka.inventory.service;

import com.arka.inventory.dto.BranchDto;
import com.arka.inventory.entity.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchService {
    Mono<BranchDto> saveBranch(Branch branch);
    Mono<BranchDto> getBranchById(Long id);
    Flux<BranchDto> getAllBranches();
    Flux<BranchDto> saveAllBranch(Flux<Branch> branchFlux);
    Mono<Branch> updateBranch(Long id, Branch updatedBranch);
}
