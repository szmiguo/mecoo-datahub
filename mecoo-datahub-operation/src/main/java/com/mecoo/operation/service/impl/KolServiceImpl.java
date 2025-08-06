package com.mecoo.operation.service.impl;

import com.mecoo.operation.service.IKolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: lin
 * @date: 2025-06-26 14:56
 */
@Service
@Slf4j
public class KolServiceImpl implements IKolService {
    @Override
    public String hi() {

        log.info("hihihihihi................");

        return "hi operation";
    }
}
