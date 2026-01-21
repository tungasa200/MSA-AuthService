package com.yjmedia.yvisbig.baseauth.module.batch;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchAuthRepository {

  String callPROC_STS_3_5(String stsTp);

  List<Long> selectDeleteMemberSqs();

  void updateMbrInfoData(List<Long> deleteMemberSqs);

  void updateMbrDetailData(List<Long> deleteMemberSqs);
}
