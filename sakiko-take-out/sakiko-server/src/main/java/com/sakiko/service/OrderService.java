package com.sakiko.service;

import com.sakiko.dto.*;
import com.sakiko.result.PageResult;
import com.sakiko.vo.OrderPaymentVO;
import com.sakiko.vo.OrderStatisticsVO;
import com.sakiko.vo.OrderSubmitVO;
import com.sakiko.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    PageResult pageQueryUser(int page, int pageSize, Integer status);

    OrderVO details(Long id);

    void userCancelById(Long id);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    void repetition(Long id);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO)throws Exception ;

    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    void complete(Long id);
}