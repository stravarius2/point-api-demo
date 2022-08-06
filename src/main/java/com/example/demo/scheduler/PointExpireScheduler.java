package com.example.demo.scheduler;

import com.example.demo.provider.PointExpireProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointExpireScheduler {

    private final PointExpireProvider pointExpireProvider;

    @Scheduled(cron = "0 0 0 * * *")
    public void pointExpireSchedule(){
        pointExpireProvider.expire();
    }
}
