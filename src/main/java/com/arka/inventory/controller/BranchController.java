package com.arka.inventory.controller;

import com.arka.inventory.dto.BranchDto;
import com.arka.inventory.entity.Branch;
import com.arka.inventory.service.BranchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/branch")
@RequiredArgsConstructor
public class BranchController {
    private final BranchServiceImpl branchServiceImpl;

    // CREATE (single)
    @PostMapping
    public Mono<BranchDto> createBranch(@RequestBody Branch branch) {
        return branchServiceImpl.saveBranch(branch);
    }

    // READ (all)
    @GetMapping
    public Flux<BranchDto> getAllBranches() {
        return branchServiceImpl.getAllBranches();
    }

    // READ (by ID)
    @GetMapping("/{id}")
    public Mono<BranchDto> getBranchById(@PathVariable Long id) {
        return branchServiceImpl.getBranchById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Mono<Branch> updateBranch(@PathVariable Long id, @RequestBody Branch updatedBranch) {
        return branchServiceImpl.updateBranch(id, updatedBranch);
    }

    // CREATE (batch) - Already implemented in your file
    @PostMapping(value = "/batch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BranchDto> createBranchBatch(@RequestBody Flux<Branch> branchFlux) {
        return branchServiceImpl.saveAllBranch(branchFlux).delayElements(Duration.ofSeconds(2)).doOnNext(branch -> System.out.println("productDto onNext -> "+branch.getBranchName())).log();
    }
}
