package com.practice.admin.service;

import com.practice.admin.dto.DashboardDTO;

public class AdminService {

    public DashboardDTO getDashboard() {

        DashboardDTO dto = new DashboardDTO();

        dto.setTotalUsers(100);
        dto.setTotalProducts(50);
        dto.setTotalOrders(25);

        return dto;
    }
}