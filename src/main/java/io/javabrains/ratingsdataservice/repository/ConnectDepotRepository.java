//package io.javabrains.ratingsdataservice.repository;
//
//import ir.donyapardaz.niopdc.order.domain.ConnectDepot;
//import ir.donyapardaz.niopdc.order.domain.enumeration.DepotStatus;
//import ir.donyapardaz.niopdc.order.domain.projection.*;
//import org.javers.spring.annotation.JaversSpringDataAuditable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.ZonedDateTime;
//import java.util.List;
//
//
///**
// * Spring Data JPA repository for the Order entity.
// */
//@SuppressWarnings("unused")
//@Repository
//@JaversSpringDataAuditable
//public interface ConnectDepotRepository extends JpaRepository<ConnectDepot, Long> {
//
//    ConnectDepot findAllByDepotSendCode(Long id);
//
//    @Query("select max(connectDepot.depotSendCode) from ConnectDepot connectDepot")
//    Long maxDepotSendCode();
//
//    @Query("select new ir.donyapardaz.niopdc.order.domain.projection.DepotFileItem(order1.id," +
//        " customer1.identifyCode," +
//        " location1.code, " +
//        " '9840', " + // depot1.code
//        " order1.orderNo, " +
//        " product1.code, " +
//        " orderProduct1.amount, " +
//        " orderProduct1.productRatePrice, " +
//        " orderProduct1.basePrice, " +
//        " orderProduct1.totalPrice, " +
//        " sum(orderPayment1.decreasedPrice), " +
//        " order1.registerDate, " +
//        " order1.expires, " +
//        " consumption1.code, " +
//        " customer1.identifyCode, " +
//        " transportContract1.contractCode, " +
//        " '1', " +
//        " case when consumption1.code = '15' then '1' else '3' end, " +
//        " case when sellContractCustomer1.hasTransport = true then '4' else '2' end,  " +
//        " person1.economicCode, " +
//        " customer1.name, " +
//        " customer1.address, " +
//        " '687173'," +
//        " customer1.identifyCode," +
//        " customer1.postalCode," +
//        " customer1.active " +
//        ") " +
//        "from Order order1 " +
//        "inner join OrderProduct orderProduct1 on orderProduct1.order = order1  " +
//        "left join OrderPayment orderPayment1 on orderPayment1.order = order1 " +
//        "left join Customer customer1 on customer1.id = order1.customerId  " +
//        "left join customer1.type type1  " +
//        "inner join Location location1 on order1.locationId = location1.id  " +
//        "inner join Depot depot1 on depot1.id = order1.depotId " +
//        "inner join Product product1 on product1.id = orderProduct1.productId " +
//        "inner join SellContractProduct sellContractProduct1 on orderProduct1.sellContractProductId = sellContractProduct1.id " +
//        "inner join sellContractProduct1.consumption consumption1 " +
//        "inner join Person person1 on person1.id = order1.personId " +
//        "left join sellContractProduct1.sellContractCustomer sellContractCustomer1 " +
//        "left join order1.orderDepots orderDepots " +
//        "left join orderDepots.connectDepot connectDepot " +
//        "left join TransportContract transportContract1 on (transportContract1.customer = customer1 and transportContract1.startDate >= order1.registerDate and ( " +
//        "transportContract1.finishDate is null or transportContract1.finishDate <= order1.registerDate " +
//        "))" +
//        "where (" +
//        "   :id is null " +
//        "   and orderDepots.id is null" +
//        "   and order1.status in ('CONFIRM', 'SEND_TO_DEPOT')" +
//        "   and order1.depotId = :depotId" +
//        "   and (:productId is null or product1.id = :productId)" +
//        "   and (:endDate is null or order1.registerDate <= :endDate)" +
//        "   and (:startDate is null or order1.registerDate >= :startDate)" +
//        "   and (:endOrderNo is null or order1.orderNo <= :endOrderNo)" +
//        "   and (:startOrderNo is null or order1.orderNo >= :startOrderNo)" +
//        ")" +
//        "or (:id is not null and connectDepot.id = :id)" +
//        "group by order1.id, " +
//        "customer1.identifyCode, " +
//        "location1.code, " +
//        "depot1.code, " +
//        "order1.orderNo, " +
//        "product1.code, " +
//        "order1.registerDate, " +
//        "order1.expires, " +
//        "consumption1.code, " +
//        "orderProduct1.amount, " +
//        "orderProduct1.productRatePrice, " +
//        "orderProduct1.totalPrice, " +
//        "orderProduct1.basePrice, " +
//        "person1.economicCode, " +
//        "customer1.name, " +
//        "customer1.address, " +
//        " customer1.identifyCode," +
//        " customer1.postalCode," +
//        " customer1.active, " +
//        "transportContract1.contractCode, " +
//        "sellContractCustomer1.hasTransport")
//    List<DepotFileItem> findAllDepotFileItem(
//            @Param("depotId") Long depotId,
//            @Param("id") Long id,
//            @Param("productId") Long productId,
//            @Param("endDate") ZonedDateTime endDate,
//            @Param("startDate") ZonedDateTime startDate,
//            @Param("startOrderNo") String startOrderNo,
//            @Param("endOrderNo") String endOrderNo
//    );
//
//
//    /*@Query("select new ir.donyapardaz.niopdc.order.domain.projection.DepotFile(jorder.depotSendCode, jorder.depotSendDate, jorder.depotId)" +
//        " from Order jorder" +
//        " where jorder.depotId = :id and jorder.depotSendDate is not null" +
//        " group by jorder.depotSendDate, jorder.depotSendCode, jorder.depotId" +
//        " order by jorder.depotSendDate")*/
//    List<ConnectDepot> findAlByDepotId(Long id);
//
//    @Query(
//        "select distinct connectDepot from ConnectDepot connectDepot" +
//            " inner join connectDepot.orderDepots orderDepots" +
//            " where " +
//            "connectDepot.depotId = :depotId" +
//            " and orderDepots.depotStatus =:depotStatus" +
//            " and (:startDate is null or connectDepot.startDate>= :startDate)" +
//            " and (:endDate is null or connectDepot.endDate <= :endDate)" +
//            " and (:startOrderNo is null or connectDepot.startOrderNo>= :startOrderNo)" +
//            " and (:endOrderNo is null or connectDepot.endOrderNo<= :endOrderNo)" +
//            " and (:productId is null or connectDepot.productId = :productId)"
//    )
//    List<ConnectDepot> search(
//            @Param("depotId") Long depotId,
//            @Param("startDate") ZonedDateTime startDate,
//            @Param("endDate") ZonedDateTime endDate,
//            @Param("startOrderNo") Long startOrderNo,
//            @Param("endOrderNo") Long endOrderNo,
//            @Param("productId") Long productId,
//            @Param("depotStatus") DepotStatus depotStatus
//    );
//}
