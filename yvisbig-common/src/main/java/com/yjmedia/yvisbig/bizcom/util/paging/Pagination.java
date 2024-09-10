package com.yjmedia.yvisbig.bizcom.util.paging;


import lombok.Getter;

@Getter
public class Pagination {

  private int totalRecordCount;
  private int totalPageCount;
  private int page;
  private int pageSize;
  private boolean existPrevPage;
  private boolean existNextPage;

  public Pagination(int totalRecordCount, int page, int pageSize) {
    this.totalRecordCount = totalRecordCount;
    this.page = page;
    this.pageSize = pageSize;
    calculation(page, pageSize);
  }

  private void calculation(int page, int pageSize) {
    // 전체 페이지 수 계산
    totalPageCount = ((totalRecordCount - 1) / pageSize) + 1;

    // 현재 페이지 번호가 전체 페이지 수보다 큰 경우, 현재 페이지에 전체 페이지 수 저장
    if (page > totalPageCount) {
      page = totalPageCount;
    }
    
    // 이전 페이지 존재 여부 확인
    existPrevPage = page != 1;

    // 다음 페이지 존재 여부 확인
    existNextPage = page < totalPageCount;
  }
}
