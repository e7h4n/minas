package com.jinyufeili.minas.info.service;

import com.jinyufeili.minas.info.data.Information;
import com.jinyufeili.minas.info.storage.InformationStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by pw on 6/14/16.
 */
@Service
public class InformationService {

    @Autowired
    private InformationStorage informationStorage;

    @Cacheable("infos")
    public Information getByKey(String eventKey) {
        return informationStorage.getByKey(eventKey);
    }
}
