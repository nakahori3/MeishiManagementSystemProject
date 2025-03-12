package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.MeishiEntity;
import com.example.demo.repository.MeishisRepository;

@Service
public class MeishiDataService {

    @Autowired
    private MeishisRepository meishisRepository;

    public MeishiEntity getMeishiById(int id, String pgpassword) {
        return meishisRepository.findDecryptedById(id, pgpassword);
    }
}

