package com.yjmedia.yvisbig.baseauth.module.batch;

import com.yjmedia.yvisbig.bizcom.exception.ErrorType;
import com.yjmedia.yvisbig.bizcom.exception.ServerBizException;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchAuthService {

  @Autowired
  private SchAuthRepository schAuthRepository;

  @Transactional
  public String updateStsNewMember() {
    try {

      return schAuthRepository.callPROC_STS_3_5("A");
    } catch (BadSqlGrammarException se) {
      log.error("{}|{}|{}", "AuthSchedule", "Statistic updateStsNewMember", se.getMessage());
      throw new ServerBizException(ErrorType.SQL_GRAMMER_EXCEPTION, se.getMessage());
    } catch (Exception e) {
      log.error("{}|{}|{}", "AuthSchedule", "Statistic updateStsNewMember", e.getMessage());
      throw new ServerBizException(ErrorType.SERVER_INTERNAL_EXCEPTION, e.getMessage());
    }
  }

  @Transactional
  public void deleteMembers() {
    try {
      List<Long> deleteMemberSqs = schAuthRepository.selectDeleteMemberSqs();
      
      if (deleteMemberSqs.size() > 0) {
        schAuthRepository.updateMbrInfoData(deleteMemberSqs);
        schAuthRepository.updateMbrDetailData(deleteMemberSqs);
      }

    } catch (BadSqlGrammarException se) {
      log.error("{}|{}|{}", "AuthSchedule", "Delete Members", se.getMessage());
      throw new ServerBizException(ErrorType.SQL_GRAMMER_EXCEPTION, se.getMessage());
    } catch (Exception e) {
      log.error("{}|{}|{}", "AuthSchedule", "Delete Members", e.getMessage());
      throw new ServerBizException(ErrorType.SERVER_INTERNAL_EXCEPTION, e.getMessage());
    }
  }
}
