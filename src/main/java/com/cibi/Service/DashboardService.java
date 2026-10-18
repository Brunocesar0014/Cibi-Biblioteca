package com.cibi.Service;

import com.cibi.model.DashboardResumo;
import com.cibi.repository.DashboardRepository;

public class DashboardService {

    private final DashboardRepository repository;

    public DashboardService() {
        this(new DashboardRepository());
    }

    public DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public DashboardResumo buscarResumo() {
        return repository.buscarResumo();
    }
}
