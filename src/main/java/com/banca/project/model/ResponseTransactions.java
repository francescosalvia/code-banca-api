package com.banca.project.model;

import com.banca.project.dto.response.PayloadTransactionsResponse;
import lombok.Data;
import java.util.List;

@Data
public class ResponseTransactions {

  private List<PayloadTransactionsResponse> list;
}
