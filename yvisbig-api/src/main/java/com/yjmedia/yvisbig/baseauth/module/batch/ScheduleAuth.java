package com.yjmedia.yvisbig.baseauth.module.batch;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 추가
public class ScheduleAuth {

  @Value("${batch.sts.newmbrs}")
  private boolean stsNewMbrs;
  @Autowired
  private SchAuthService schAuthService;

  /**
   * Cron 표현식을 사용한 작업 예약 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
   */
  //@Scheduled(cron = "0/5 * * * * ?")
  @Scheduled(cron = "14 14 3,5 * * ?")
  public void scheduleTaskUsingCronExpression() {
    if (stsNewMbrs) {

      long now = System.currentTimeMillis();
      log.info("schedule tasks using cron jobs - {}", now);

      schAuthService.updateStsNewMember();

      long end = System.currentTimeMillis();
      log.info("schedule tasks using cron jobs - {} : processing : {}", end, end - now);
    }
  }

  @Scheduled(cron = "0 0 * * * *")
  public void scheduledDeleteMembers() {
    long now = System.currentTimeMillis();
    log.info("delete members schedule tasks using cron jobs - {}", now);

    schAuthService.deleteMembers();

    long end = System.currentTimeMillis();
    log.info("delete members schedule tasks using cron jobs - {} : processing : {}", end,
        end - now);
  }
}
