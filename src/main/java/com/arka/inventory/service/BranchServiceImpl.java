package com.arka.inventory.service;

import com.arka.inventory.dto.BranchDto;
import com.arka.inventory.entity.Branch;
import com.arka.inventory.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService{

    private final BranchRepository branchRepository;


    // Create a single branch
    public Mono<BranchDto> saveBranch(Branch branch) {
        return branchRepository.save(branch).map(BranchDto::fromEntity);
    }

    // Get a single branch by ID
    public Mono<BranchDto> getBranchById(Long id) {
        return branchRepository.findById(id).map(BranchDto::fromEntity);
    }

    // Get all branches
    public Flux<BranchDto> getAllBranches() {
        return branchRepository.findAll().map(BranchDto::fromEntity);
    }

    public Flux<BranchDto> saveAllBranch(Flux<Branch> branchFlux) {
        return branchRepository.saveAll(branchFlux).map(BranchDto::fromEntity);
    }

    // Update an existing branch
    public Mono<Branch> updateBranch(Long id, Branch updatedBranch) {
        return branchRepository.findById(id)
                .flatMap(branch -> {
                    branch.setBranchName(updatedBranch.getBranchName());
                    branch.setAddress(updatedBranch.getAddress());
                    branch.setCity(updatedBranch.getCity());
                    branch.setProvinceState(updatedBranch.getProvinceState());
                    branch.setCountry(updatedBranch.getCountry());
                    branch.setPhone(updatedBranch.getPhone());
                    branch.setEmail(updatedBranch.getEmail());
                    branch.setManagerName(updatedBranch.getManagerName());
                    branch.setActive(updatedBranch.isActive());
                    branch.setDateOpened(updatedBranch.getDateOpened());
                    branch.setWarehouseType(updatedBranch.getWarehouseType());
                    return branchRepository.save(branch);
                });
    }
}

