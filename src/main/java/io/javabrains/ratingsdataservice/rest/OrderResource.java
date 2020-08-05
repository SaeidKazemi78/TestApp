//package io.javabrains.ratingsdataservice.rest;
//
//import com.codahale.metrics.annotation.Timed;
//import io.github.jhipster.web.util.ResponseUtil;
//import io.swagger.annotations.ApiParam;
//import ir.donyapardaz.niopdc.order.domain.enumeration.*;
//import ir.donyapardaz.niopdc.order.security.AuthoritiesConstants;
//import ir.donyapardaz.niopdc.order.service.OrderService;
//import ir.donyapardaz.niopdc.order.service.client.dto.OrderProductWithSrcDTO;
//import ir.donyapardaz.niopdc.order.service.client.dto.PaymentBillDTO;
//import ir.donyapardaz.niopdc.order.service.client.dto.TimePeriodRangeDTO;
//import ir.donyapardaz.niopdc.order.service.dto.*;
//import ir.donyapardaz.niopdc.order.service.mapper.BoundarySellMapper;
//import ir.donyapardaz.niopdc.order.web.rest.errors.BadRequestAlertException;
//import ir.donyapardaz.niopdc.order.web.rest.util.HeaderUtil;
//import ir.donyapardaz.niopdc.order.web.rest.util.PaginationUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.time.ZonedDateTime;
//import java.util.*;
//
///**
// * REST controller for managing Order.
// */
//@RestController
//@RequestMapping("/api")
//public class OrderResource {
//
//    private static final String ENTITY_ORDER_NAME = "order";
//    private static final String ENTITY_FUEL_RECEIPT_NAME = "fuelReceipt";
//    private static final String ENTITY_BOUNDARY_SELL_NAME = "boundarySell";
//
//    private final Logger log = LoggerFactory.getLogger(OrderResource.class);
//    private final OrderService orderService;
//    private BoundarySellMapper boundarySellMapper;
//
//    public OrderResource(OrderService orderService, BoundarySellMapper boundarySellMapper) {
//        this.orderService = orderService;
//        this.boundarySellMapper = boundarySellMapper;
//    }
//
//    // region order
//
//    /**
//     * POST  /orders : Create a new order.
//     *
//     * @param orderDTO the orderDTO to create
//     * @return the ResponseEntity with status 201 (Created) and with body the new orderDTO, or with status 400 (Bad Request) if the order has already an ID
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PostMapping("/orders")
//    @Timed
//    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.CREATE_ORDER, AuthoritiesConstants.CREATE_ORDER_AIRPLANE})
//    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException, IOException {
//        log.debug("REST request to save Order : {}", orderDTO);
//        if (orderDTO.getId() != null) {
//            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_ORDER_NAME, "idexists");
//        }
//        OrderDTO result = orderService.saveOrder(orderDTO);
//
//        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert((result.getOrderType() == OrderType.AIRPLANE ? ENTITY_FUEL_RECEIPT_NAME :
//                result.getStatus().equals(OrderStatus.PAID) || result.getStatus().equals(OrderStatus.CREDIT_PAID) ? ENTITY_ORDER_NAME : ""), result.getOrderNo()))
//            .body(result);
//    }
//
//
//    /**
//     * PUT  /orders : Updates an existing order.
//     *
//     * @param orderDTO the orderDTO to update
//     * @return the ResponseEntity with status 200 (OK) and with body the updated orderDTO,
//     * or with status 400 (Bad Request) if the orderDTO is not valid,
//     * or with status 500 (Internal Server Error) if the orderDTO couldn't be updated
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PutMapping("/orders")
//    @Timed
//    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.EDIT_ORDER, AuthoritiesConstants.CREATE_ORDER_AIRPLANE})
//    public ResponseEntity<OrderDTO> updateOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException, IOException {
//        log.debug("REST request to update Order : {}", orderDTO);
//        if (orderDTO.getId() == null) {
//            return createOrder(orderDTO);
//        }
//        OrderDTO result = orderService.saveOrder(orderDTO);
//
//        if (result.getBuyGroup().equals(BuyGroup.CASH) && result.getOrderPrePays().size() == 1 && orderDTO.getCreateBankTransaction()) {
//            try {
//                result.setPayId(orderService.startBankTransactionOrder(result.getId()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        ResponseEntity.BodyBuilder created = ResponseEntity.created(new URI("/api/orders/" + result.getId()));
//        if (result.getStatus().equals(OrderStatus.PAID) || result.getStatus().equals(OrderStatus.CREDIT_PAID)) {
//            created = created.headers(HeaderUtil.createEntityCreationAlert((result.getOrderType() == OrderType.AIRPLANE ? ENTITY_FUEL_RECEIPT_NAME :
//                result.getStatus().equals(OrderStatus.PAID) || result.getStatus().equals(OrderStatus.CREDIT_PAID) ? ENTITY_ORDER_NAME : ""), result.getOrderNo()));
//        }
//        return created.body(result);
//    }
//
//    /**
//     * GET  /orders/:id : get the "id" order.
//     *
//     * @param id the id of the orderDTO to retrieve
//     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
//     */
//    @GetMapping("/orders/{id}")
//    @Timed
//    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
//        log.debug("REST request to get Order : {}", id);
//        OrderDTO orderDTO = orderService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//    /**
//     * GET  /orders/:id : get the "id" order.
//     *
//     * @param id the id of the orderDTO to retrieve
//     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
//     */
//    @GetMapping("/orders/{id}/for-edit")
//    @Timed
//    public ResponseEntity<OrderDTO> getOrderForEdit(@PathVariable Long id) {
//        log.debug("REST request to get Order : {}", id);
//        OrderDTO orderDTO = orderService.findOneForEdit(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//    /**
//     * *  لیست حواله ها یا سوخت های هوایی
//     * GET  /orders : get all the orders.
//     *
//     * @param pageable the pagination information
//     * @return the ResponseEntity with status 200 (OK) and the list of orders in body
//     */
//    @GetMapping("/orders/mode/{mode}")
//    @Timed
//    public ResponseEntity<List<OrderDTO>> getAllOrdersByMode(
//        @RequestParam(required = false) String query,
//        @RequestParam(required = false) Long productRateId,
//        @PathVariable String mode,
//        @ApiParam Pageable pageable
//    ) {
//        log.debug("REST request to get a page of Orders");
//        Page<OrderDTO> page = orderService.findAll(productRateId, query, pageable, mode);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }
//
//    @GetMapping("/orders/airplane")
//    @Timed
//    public ResponseEntity<List<AirplaneListDTO>> getAllOrdersAirplane(
//        @RequestParam(required = false) String orderNo,
//        @RequestParam(required = false) String personName,
//        @RequestParam(required = false) String customerName,
//        @RequestParam(required = false) Long amount,
//        @RequestParam(required = false) String status,
//        @RequestParam(required = false) String buyGroup,
//        @RequestParam(required = false) String name,
//        @RequestParam(required = false) String registerDate,
//        @RequestParam(required = false) String unitTitle,
//        @RequestParam(required = false) String productTitle,
//        @ApiParam Pageable pageable
//    ) {
//        log.debug("REST request to get a page of Orders");
//        Page<AirplaneListDTO> page = orderService.findAllAirplane(orderNo,
//            personName,
//            customerName,
//            amount,
//            status,
//            buyGroup,
//            name,
//            unitTitle,
//            productTitle,
//            ZonedDateTime.parse(registerDate),
//            pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }
//
//    /**
//     * حذف حواله
//     * هر نوع حواله ای
//     *
//     * @param id the id of the orderDTO to delete
//     * @return the ResponseEntity with status 200 (OK)
//     */
//    @DeleteMapping("/orders/{id}")
//    @Timed
//    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.DELETE_ORDER, AuthoritiesConstants.DELETE_ORDER, AuthoritiesConstants.DELETE_BOUNDARY_SELL})
//    public ResponseEntity<Void> deleteOrder(@PathVariable Long id, @RequestParam Boolean force) throws IOException {
//        log.debug("REST request to delete Order : {}", id);
//        OrderType orderType = orderService.delete(id, force != null && force);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(orderType == OrderType.AIRPLANE ? ENTITY_FUEL_RECEIPT_NAME : (orderType == OrderType.ORDER ? ENTITY_ORDER_NAME : ENTITY_BOUNDARY_SELL_NAME), id.toString())).build();
//    }
//
//    /**
//     * تایید حواله
//     * هر نوع حواله ای
//     *
//     * @param id the id of the orderDTO to confirm
//     * @return the ResponseEntity with status 200 (OK)
//     */
//    @GetMapping("/orders/{id}/confirm")
//    @Timed
//    public ResponseEntity<Void> confirm(@PathVariable Long id) {
//        log.debug("REST request to confirm Order : {}", id);
//        OrderType orderType = orderService.confirm(id);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityConfirmedAlert(orderType == OrderType.AIRPLANE ? ENTITY_FUEL_RECEIPT_NAME : (orderType == OrderType.ORDER ? ENTITY_ORDER_NAME : ENTITY_BOUNDARY_SELL_NAME), id.toString())).build();
//    }
//
//    @GetMapping("/orders/{refuelCenterId}/confirm-all")
//    @Timed
//    public ResponseEntity<Void> confirmAll(@PathVariable Long refuelCenterId) {
//        log.debug("REST request to confirm all Order : {}", refuelCenterId);
//        orderService.confirmAll(refuelCenterId);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityConfirmedAlert(ENTITY_FUEL_RECEIPT_NAME, refuelCenterId.toString())).build();
//    }
//
//    /**
//     * عدم تایید حواله
//     * هر نوع حواله ای
//     *
//     * @param id the id of the orderDTO to confirm
//     * @return the ResponseEntity with status 200 (OK)
//     */
//    @GetMapping("/orders/{id}/revert-confirm")
//    @Timed
//    public ResponseEntity<Void> revertConfirmOrder(@PathVariable Long id) {
//        log.debug("REST request to revert-confirm Order : {}", id);
//        OrderType orderType = orderService.revertConfirm(id);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeConfirmedAlert(ENTITY_ORDER_NAME, id.toString())).build();
//    }
//
//    /**
//     * ابطال حواله
//     * نوع حواله و رسید سوخت
//     *
//     * @param id the id of the orderDTO to confirm
//     * @return the ResponseEntity with status 200 (OK)
//     */
//    @GetMapping("/orders/{id}/revocation")
//    @Timed
//    public ResponseEntity<Void> revocationOrder(@PathVariable Long id, @RequestParam Boolean force) {
//        log.debug("REST request to delete Order : {}", id);
//        OrderType orderType = orderService.revocation(id, force != null && force, null);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityRevokedAlert(orderType == OrderType.AIRPLANE ? ENTITY_FUEL_RECEIPT_NAME : (orderType == OrderType.ORDER ? ENTITY_ORDER_NAME : ENTITY_BOUNDARY_SELL_NAME), id.toString())).build();
//    }
//
//    /**
//     * تعدیل نرخ
//     *
//     * @param id the id of the orderDTO to confirm
//     * @return the ResponseEntity with status 200 (OK)
//     */
//    @PutMapping("/orders/adjustment-rate/{id}")
//    @Timed
//    public ResponseEntity<Void> adjustmentOrder(
//        @PathVariable Long id,
//        @RequestBody List<Integer> orderIds
//    ) {
//        log.debug("REST request to adjustment Order : {}", id);
//        orderService.applyAdjustmentRate(id, orderIds);
//        return ResponseEntity.ok().build();
//    }
//
//    // endregion
////
////    @PostMapping("/orders/reserve")
////    @Timed
////    public ResponseEntity<OrderDTO> reserveOrder(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException, Exception {
////        log.debug("REST request to save Order : {}", orderDTO);
////        OrderDTO result = orderService.reserve(orderDTO);
////
////        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
////            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_ORDER_NAME, result.getFuelType() == null ? result.getId().toString() : result.getOrderNo()))
////            .body(result);
////    }
////
////    @PostMapping("/orders/e-payment")
////    @Timed
////    public ResponseEntity<String> createOrderEPayment(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException, IOException {
////        log.debug("REST request to save Order : {}", orderDTO);
////        if (orderDTO.getId() != null) {
////            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_ORDER_NAME, "idexists");
////        }
////        String result = orderService.saveForEPayment(orderDTO);
////
////        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
////    }
////
////    @PutMapping("/orders/e-payment")
////    @Timed
////    public ResponseEntity<String> updateOrderEPayment(@Valid @RequestBody OrderDTO orderDTO) throws URISyntaxException, IOException {
////        log.debug("REST request to save Order : {}", orderDTO);
////        if (orderDTO.getId() == null) {
////            createOrderEPayment(orderDTO);
////        }
////        String result = orderService.saveForEPayment(orderDTO);
////
////        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
////    }
////
////
////    @PutMapping("/orders/payment")
////    @Timed
////    public ResponseEntity<OrderDTO> paymentOrder(@RequestBody OrderDTO orderDTO) throws URISyntaxException, IOException {
////        log.debug("REST request to update Order : {}", orderDTO);
////        if (orderDTO.getId() == null) {
////            return null;
////        }
////        OrderDTO result = orderService.payment(orderDTO);
////        return ResponseEntity.ok()
////            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_ORDER_NAME, orderDTO.getFuelType() == null ? orderDTO.getId().toString() : orderDTO.getOrderNo()))
////            .body(result);
////    }
////
//
////
////    /**
////     * GET  /orders : get all the orders.
////     *
////     * @param pageable the pagination information
////     * @return the ResponseEntity with status 200 (OK) and the list of orders in body
////     */
////    @GetMapping("/orders")
////    @Timed
////    public ResponseEntity<List<OrderDTO>> getAllOrders(
////        @RequestParam(required = false) String query,
////        @ApiParam Pageable pageable
////    ) {
////        log.debug("REST request to get a page of Orders");
////        Page<OrderDTO> page = orderService.findAll(query, pageable);
////        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
////        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
////    }
////
//
//    //
//    @GetMapping("/orders/credit-not-deposited")
//    @Timed
//    public ResponseEntity<List<OrderDTO>> getAllOrdersByCreditType(@RequestParam(required = false) String query, @ApiParam Pageable pageable) {
//        log.debug("REST request to get a page of Orders");
//        Page<OrderDTO> page = orderService.findAllOrderByCreditType(query, pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }
////
////    @GetMapping("/orders/has-credit-not-deposited-in-time")
////    @Timed
////    public ResponseEntity<CreditNotDepositedInTime> hasCreditNotDepositedInTime(@RequestParam(required = false) Long customerId, @RequestParam Long personId) {
////        log.debug("REST request to get a page of Orders");
////        CreditNotDepositedInTime page = orderService.hasCreditNotDepositedInTime(customerId, personId);
////        return ResponseEntity.ok(page);
////    }
////
////
////    /**
////     * DELETE  /orders/:id : delete the "id" order.
////     *
////     * @param id the id of the orderDTO to delete
////     * @return the ResponseEntity with status 200 (OK)
////     */
////    @DeleteMapping("/orders/{id}")
////    @Timed
////    @Secured({AuthoritiesConstants.ROLE_ADMIN, AuthoritiesConstants.DELETE_ORDER, AuthoritiesConstants.DELETE_BOUNDARY_SELL})
////    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) throws IOException {
////        log.debug("REST request to delete Order : {}", id);
////        orderService.delete(id);
////        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_ORDER_NAME, id.toString())).build();
////    }
////
//
////
////    @PostMapping("/orders/payment-done")
////    public ResponseEntity<Boolean> paymentDone(@Valid @RequestBody PaymentDTO paymentDTO) {
////        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(true));
////    }
//
//
////    /**
////     * GET  /orders : get all the orders.
////     *
////     * @return the ResponseEntity with status 200 (OK) and the list of orders in body
////     */
///*//    @GetMapping("/orders/{id}/report")
////    @Timed
////    public ResponseEntity<List<OrderReportDTO>> getAllOrderReports(
////        @PathVariable("id") Long id) {
////        log.debug("REST request to get a page of Orders");
////        List<OrderReportDTO> list = orderService.report(id);
////        return new ResponseEntity<>(list, HttpStatus.OK);
////    }*/
//
//    // region boundary sell
//
//    /**
//     * لیست حواله های فروش مرزی
//     *
//     * @param carRfId
//     * @param plaque
//     * @param query
//     * @param pageable
//     * @return
//     * @throws URISyntaxException
//     */
//    @GetMapping("/order/boundary-sells")
//    @Timed
//    public ResponseEntity<List<OrderDTO>> getAllBoundaries(
//        @RequestParam(required = false) String carRfId,
//        @RequestParam(required = false) String plaque,
//        @RequestParam(required = false) String query,
//        @ApiParam Pageable pageable) throws URISyntaxException {
//        log.debug("REST request to get all boundaries");
//        Page<OrderDTO> result = orderService.getAllBoundaries(query, pageable, carRfId, plaque);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(result, "/api/orders");
//        return new ResponseEntity<>(result.getContent(), headers, HttpStatus.OK);
//    }
//
//    @PostMapping("/order/boundary-sells")
//    @Secured({AuthoritiesConstants.ROLE_ADMIN,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSIT_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSIT_WEB_SELL})
//    @Timed
//    public ResponseEntity<BoundarySellDTO> createBoundary(@Valid @RequestBody BoundarySellDTO boundarySellDTO) throws URISyntaxException {
//        log.debug("REST request to save Order : {}", boundarySellDTO);
//
//        BoundarySellDTO result = orderService.saveBoundarySell(boundarySellDTO, OrderCreationMethod.WEB_SITE);
//
//        if (boundarySellDTO.getCreateBankTransaction() && result.getOrderPrePays().size() == 1) {
//            OrderDTO orderDTO = boundarySellMapper.toOrderDTO(result);
//            try {
//                result.setPayId(orderService.startBankTransactionOrders(Collections.singletonList(orderDTO)));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return ResponseEntity.created(new URI("/api/orders/boundary-sell/" + result.getId()))
//            .headers(
//                boundarySellDTO.getId() == null ?
//                    HeaderUtil.createEntityCreationAlert(ENTITY_BOUNDARY_SELL_NAME, result.getOrderNo()) :
//                    HeaderUtil.createEntityUpdateAlert(ENTITY_BOUNDARY_SELL_NAME, result.getOrderNo()))
//            .body(result);
//    }
//
//    @GetMapping("/order/boundary-sells/{id}")
//    @Secured({AuthoritiesConstants.ROLE_ADMIN,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSIT_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSIT_WEB_SELL})
//    @Timed
//    public ResponseEntity<BoundarySellDTO> getBoundarySell(@PathVariable Long id) {
//        log.debug("REST request to get Order : {}", id);
//        BoundarySellDTO orderDTO = orderService.findOneBoundarySell(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//    @GetMapping("/order/boundary-sells/{id}/for-edit")
//    @Timed
//    public ResponseEntity<BoundarySellDTO> getBoundarySellWithEagerRelationships(@PathVariable Long id) {
//        log.debug("REST request to get Order : {}", id);
//        BoundarySellDTO orderDTO = orderService.findOneBoundarySellWithEagerRelationships(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//    @GetMapping("/order/boundary-sells/customer/{customerId}/driver")
//    @Secured({AuthoritiesConstants.ROLE_ADMIN,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSIT_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSIT_WEB_SELL})
//    @Timed
//    public ResponseEntity<BoundarySellDriverDTO> getBoundarySellDriver(@PathVariable Long customerId) {
//        log.debug("REST request to get Order customerId: {}", customerId);
//        BoundarySellDriverDTO orderDTO = orderService.findLastBoundarySellByCustomer(customerId);
//        return ResponseEntity.ok(orderDTO);
//    }
//
//    @GetMapping("/order/boundary-sells/customer/{customerId}")
//    @Secured({AuthoritiesConstants.ROLE_ADMIN,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSIT_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSHIP_WEB_SELL,
//        AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSIT_WEB_SELL})
//    @Timed
//    public ResponseEntity<List<BoundarySellDTO>> getBoundarySellCustomer(@PathVariable Long customerId) {
//        log.debug("REST request to get Order customerId: {}", customerId);
//        List<BoundarySellDTO> orderDTOs = orderService.findBoundarySellByCustomer(customerId);
//        return ResponseEntity.ok(orderDTOs);
//    }
//
//    // endregion
//
//    // region order/boundary sell payment
//
//    /**
//     * GET  لیست اطلاعات پیش پرداخت
//     * برای هر نوع حواله
//     *
//     * @param id the id of the orderDTO to retrieve
//     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
//     */
//    @GetMapping("/orders/{id}/payment-list")
//    @Timed
//    public ResponseEntity<OrderDTO> getOrderPaymentList(@PathVariable Long id) {
//        log.debug("REST request to get Order : {}", id);
//        OrderDTO orderDTO = orderService.findOneForPaymentList(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//
//    /**
//     * GET  گرفتن شماره تراکنش برای پرداخت یک پیش پرداخت
//     * برای هر نوع حواله
//     *
//     * @param id the id of the orderDTO to retrieve
//     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
//     */
//    @GetMapping("/orders/pre-pay/{id}/start-bank-transaction")
//    @Timed
//    public ResponseEntity<String> startBankTransactionPrePay(@PathVariable Long id) {
//        log.debug("REST request to get Order : {}", id);
//        String orderDTO = orderService.startBankTransactionPrePay(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//    /**
//     * GET گرفتن شماره تراکنش برای چندین پیش پرداخت یک حواله
//     * برای هر نوع حواله
//     *
//     * @param id the id of the orderDTO to retrieve
//     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
//     */
//    @GetMapping("/orders/{id}/start-bank-transaction")
//    @Timed
//    public ResponseEntity<String> startBankTransactionOrder(@PathVariable Long id) {
//        log.debug("REST request to get Order : {}", id);
//        String orderDTO = orderService.startBankTransactionOrder(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//
//    /**
//     * GET گرفتن شماره تراکنش برای چندین پیش پرداخت یک حواله
//     * برای هر نوع حواله
//     *
//     * @param ids the id of the orderDTO to retrieve
//     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO, or with status 404 (Not Found)
//     */
//    @PostMapping("/orders/start-bank-transaction")
//    @Timed
//    public ResponseEntity<String> startBankTransaction(@RequestBody Set<Long> ids) {
//        log.debug("REST request to get Order : {}", ids);
//        String orderDTO = orderService.startBankTransactionOrders(ids, false);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//    @PostMapping("/orders/single-pay/start-bank-transaction")
//    @Timed
//    public ResponseEntity<String> startBankTransactionForSinglePay(@RequestBody Set<Long> ids) {
//        log.debug("REST request to get Order : {}", ids);
//        String orderDTO = orderService.startBankTransactionOrders(ids, true);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderDTO));
//    }
//
//    @PostMapping("/payments/complete/{identifier}")
//    @Timed
//    public ResponseEntity<List<PaymentBillDTO>> completePayment(@PathVariable("identifier") String identifier, @RequestBody List<OrderPaymentDTO> orderPaymentDTOS) {
//        log.debug("REST request to update Order : {}, {}", identifier, orderPaymentDTOS);
//        List<PaymentBillDTO> result = orderService.payment(identifier, orderPaymentDTOS);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_ORDER_NAME, identifier))
//            .body(result);
//    }
//
//    @PostMapping("/wallet-payments/complete/{identifier}")
//    @Timed
//    public ResponseEntity<Boolean> completeWalletPayment(@PathVariable("identifier") String identifier, @RequestBody List<OrderWalletDTO> orderWalletDTOS) {
//        log.debug("REST request to update Order : {}, {}", identifier, orderWalletDTOS);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_ORDER_NAME, identifier))
//            .body(orderService.walletPayment(identifier, orderWalletDTOS));
//    }
//
//    // endregion
//
////    @GetMapping("/orders/{id}/boundary-increase-print")
////    @Timed
////    public ResponseEntity<Boolean> boundaryIncreasePrintNumber(
////        @PathVariable("id") Long id,
////        @RequestParam Integer count
////    ) {
////        log.debug("REST request to get a page of Orders");
////        Boolean result = orderService.boundaryIncreasePrintNumber(id, count);
////        return new ResponseEntity<>(result, HttpStatus.OK);
////    }
//
//    // region extra
//
//    /**
//     * محاسبه مجموع لیتراژ به اضای هر دوره زمانی برای محاسبه هزینه
//     *
//     * @param timePeriodRangeDTO
//     * @return
//     */
//    @PostMapping("/orders/totalLiter")
//    public ResponseEntity<Map<PaymentPeriod, Long>> totalLiter(@RequestBody TimePeriodRangeDTO timePeriodRangeDTO) {
//        return ResponseEntity.ok(orderService.totalLiter(timePeriodRangeDTO));
//    }
//
//    /**
//     * آیا پرداختی های یک دوره زمانی همه به صورت نقدی پرداخت شده است؟
//     * برای محاسبه هزینه
//     *
//     * @param timePeriodRangeDTO
//     * @return
//     */
//    @PostMapping("/orders/is-cash")
//    public ResponseEntity<Map<PaymentPeriod, Boolean>> allOrderIsCash(@RequestBody TimePeriodRangeDTO timePeriodRangeDTO) {
//        return ResponseEntity.ok(orderService.allOrderIsCash(timePeriodRangeDTO));
//    }
//
//    /**
//     * تاریخ آخرین زمانی که برای یک شخص حواله ثبت شده است
//     * برای حذف و ایجاد و ویرایش کردن قرارداد های شخص
//     *
//     * @param sellContractId
//     * @param personId
//     * @return
//     */
//    @GetMapping("/orders/max-date/{sellContractId}/{personId}")
//    public ResponseEntity<ZonedDateTime> getMaxDateBySellContractAndPerson(@PathVariable("sellContractId") Long sellContractId, @PathVariable("personId") Long personId) {
//        return ResponseEntity.ok(orderService.getMaxDateBySellContractAndPerson(sellContractId, personId));
//    }
//
//    /**
//     * حواله ای بین بازه زمانی برای این منطقه وجود دارد
//     * برای حذف شیفت
//     *
//     * @param locationId
//     * @param fromDate
//     * @param toDate
//     * @return
//     */
//    @GetMapping("/orders/exist/{locationId}/{fromDate}/{toDate}")
//    @Timed
//    public ResponseEntity<Boolean> existOrderBetween(@PathVariable Long locationId,
//                                                     @PathVariable @DateTimeFormat ZonedDateTime fromDate, @PathVariable @DateTimeFormat ZonedDateTime toDate) {
//        log.debug("REST request to get a page of Orders");
//        Boolean result = orderService.existOrderBetween(locationId, fromDate, toDate);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
//
//    /**
//     * آیا حواله وجود دارد
//     * برای حذف لاگ بوک
//     *
//     * @param orderId
//     * @return
//     */
//    @GetMapping("/orders/{orderId}/exits")
//    public Boolean isExistOrder(@PathVariable Long orderId) {
//        return orderService.isExistOrder(orderId);
//    }
//
//    /**
//     * حواله ای برای مشتری ایجاد شده است؟
//     * برای حذف مشتری
//     *
//     * @param customerId
//     * @return
//     */
//    @GetMapping("/orders/{customerId}/exist-customer")
//    public Boolean isExistCustomer(@PathVariable Long customerId) {
//        return orderService.isExistCustomer(customerId);
//    }
//
//    @GetMapping("/orders/customer/{customerId}/date/{date}/exist-customer")
//    public Boolean isExistCustomer(@PathVariable Long customerId, @PathVariable ZonedDateTime date) {
//        return orderService.isExistCustomerAfterDate(customerId, date);
//    }
//
//    @GetMapping("/orders/confirm-way-bill/{id}/{orderStatus}")
//    @Timed
//    public ResponseEntity<Boolean> confirmWayBill(@PathVariable Long id, @PathVariable OrderStatus orderStatus) {
//        log.debug("REST request to revert-confirm Order : {}", id);
//        Boolean orderType = orderService.confirmWayBill(id, orderStatus);
//        return ResponseEntity.ok().body(orderType);
//    }
//
//    // endregion
//
//    // region webservice
//    @GetMapping("/orders/order-product-with-src")
//    @Timed
//    public ResponseEntity<List<OrderProductWithSrcDTO>> confirmWayBill(@RequestParam String startDate, @RequestParam String finishDate, @RequestParam(required = false) String mode) {
//        log.debug("REST request to revert-confirm Order : {}");
//        List<OrderProductWithSrcDTO> orderProductWithSrcs = orderService.getAllOrderProductWithSRC(startDate, finishDate, mode);
//        return ResponseEntity.ok().body(orderProductWithSrcs);
//    }
//    // endregion
//
//    @GetMapping("/orders/update-sell-contract/{oldId}/{newId}")
//    @Timed
//    public ResponseEntity<Boolean> editSellContractId(@PathVariable("oldId") Long oldId,
//                                                      @PathVariable("newId") Long newId) {
//        log.debug("REST request to Update Sell Contract Id : {}");
//        Boolean success = orderService.editSellContractId(oldId, newId);
//        return ResponseEntity.ok().body(success);
//    }
//}
