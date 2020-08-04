package io.javabrains.ratingsdataservice.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import ir.donyapardaz.niopdc.order.config.Profiles;
import ir.donyapardaz.niopdc.order.domain.QOrder;
import ir.donyapardaz.niopdc.order.domain.QOrderCredit;
import ir.donyapardaz.niopdc.order.domain.QOrderDepot;
import ir.donyapardaz.niopdc.order.domain.QOrderProduct;
import ir.donyapardaz.niopdc.order.domain.enumeration.*;
import ir.donyapardaz.niopdc.order.domain.projection.*;
import ir.donyapardaz.niopdc.order.repository.custom.OrderRepositoryCustom;
import ir.donyapardaz.niopdc.order.repository.dsl.PredicatesBuilder;
import ir.donyapardaz.niopdc.order.security.AuthoritiesConstants;
import ir.donyapardaz.niopdc.order.security.SecurityUtils;
import ir.donyapardaz.niopdc.order.service.dto.AirplaneListDTO;
import ir.donyapardaz.niopdc.order.service.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OrderRepositoryImpl extends JdbcDaoSupport implements OrderRepositoryCustom {



    @PersistenceContext
    private EntityManager em;
    private NamedParameterJdbcTemplate jdbcTemplate;

    static List<ContractType> convertContractType(String contractType) {
        List<ContractType> contractTypes = new ArrayList<>();
        switch (contractType) {
            case "order":
                contractTypes.add(ContractType.SUPPLY_CHANNEL);
                contractTypes.add(ContractType.CONSUMER);
                contractTypes.add(ContractType.LIQUID_GAS);
                contractTypes.add(ContractType.BRAND);
                contractTypes.add(ContractType.EXCHANGE);
                contractTypes.add(ContractType.EXPORT);
                break;
            case "airplane":
            case "refuel-center":
                contractTypes.add(ContractType.AIRPLANE);
                contractTypes.add(ContractType.MILITARY);
                break;
            case "export":
                contractTypes.add(ContractType.EXPORT);
                break;
        }
        return contractTypes;
    }

    @Autowired
    public void setDs(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @PostConstruct
    private void postConstruct() {
        jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
    }

    public Page<OrderCustomerLocationPerson> findAllOrderWithCustomer(String query, Pageable pageable, @Param("username") String username) {

        QOrder o = new QOrder("o");
        QCustomer c = new QCustomer("c");
        QPerson p = new QPerson("p");
        QDepot d = new QDepot("d");

        QCustomerAccess cv = new QCustomerAccess("cv");
        
//        QPersonView pv = new QPersonView("pv");
        QLocation l = new QLocation("l");
        JPAQuery<OrderCustomerLocationPerson> jpaQuery = new JPAQuery<>(em);
        jpaQuery.from(o)
            .leftJoin(c).on(c.id.eq(o.customerId))
            .leftJoin(cv).on(c.id.eq(cv.id.id))
            .innerJoin(p).on(o.personId.eq(p.id))
//            .innerJoin(pv).on(p.id.eq(pv.id))
            .innerJoin(d).on(o.depotId.eq(d.id))
            .innerJoin(l).on(o.locationId.eq(l.id));
//            .innerJoin(lv).on(l.id.eq(lv.id));


        QOrderCustomerLocationPerson qOrderCustomerLocationPerson = new QOrderCustomerLocationPerson(o.as("order"), c.as("customer"), p.as("person"), d.as("depot"), l.as("location"));
        jpaQuery.select(qOrderCustomerLocationPerson);


        BooleanExpression where = cv.id.username.isNull().or(cv.id.username.eq(username));

        PathBuilder<Order> orderPathBuilder = new PathBuilder<>(Order.class, "o");

        Map<String, PathBuilder> map = new HashMap<>();
        map.put("customer", new PathBuilder<>(Customer.class, "customer"));
        map.put("person", new PathBuilder<>(Customer.class, "person"));

        BooleanExpression search = new PredicatesBuilder().build(query, orderPathBuilder, map);

        if (search != null)
            where = where.and(search);

        jpaQuery.where(where);

        long size = jpaQuery.fetch().size();

        jpaQuery.offset(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize());
        if (pageable.getSort() != null) {
            for (Sort.Order order : pageable.getSort()) {
                jpaQuery.orderBy(new OrderSpecifier(order.isAscending() ? Order.ASC
                    : Order.DESC, orderPathBuilder.get(order.getProperty())));
            }
        }
        return new PageImpl<>(jpaQuery.fetch(), pageable, size);
    }

    @Override
    public Page<OrderCustomerLocationPerson> findAllOrderWithCustomerAndBoundaryType(String query, Pageable pageable, String username, String carRfId, String plaque) {
        QOrder o = new QOrder("j_order");
        QCustomer c = new QCustomer("customer");
        QLocation l = new QLocation("location");
        QLocationAccess lv = new QLocationAccess("lv");
        JPAQuery<OrderCustomerLocationPerson> jpaQuery = new JPAQuery<>(em);
        jpaQuery.from(o)
            .innerJoin(c).on(c.id.eq(o.customerId))
            .innerJoin(l).on(o.locationId.eq(l.id))
            .innerJoin(lv).on(l.id.eq(lv.id.id));

        JPAQuery<OrderCustomerLocationPerson> jpaCountQuery = new JPAQuery<>(em);
        jpaCountQuery.from(o)
            .innerJoin(l).on(o.locationId.eq(l.id))
            .innerJoin(lv).on(l.id.eq(lv.id.id));


        QOrderCustomerLocationPerson qOrderCustomerLocationPerson = new QOrderCustomerLocationPerson(o.as("j_order"), c.as("customer"), l.as("location"));
        jpaQuery.select(qOrderCustomerLocationPerson);
        jpaCountQuery.select(o);

        List<OrderType> orderTypes = new ArrayList<>();

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSHIP_WEB_SELL) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSHIP_WEB_SELL) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ROLE_BOUNDARY_TRANSHIP_LIST) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ROLE_ADMIN)) {
            orderTypes.add(OrderType.BOUNDARY_TRANSHIP);
        }

        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ALONG_FUEL_BOUNDARY_TRANSIT_WEB_SELL) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.PUMP_NOZZLE_BOUNDARY_TRANSIT_WEB_SELL) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ROLE_BOUNDARY_TRANSIT_LIST) ||
            SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ROLE_ADMIN)) {
            orderTypes.add(OrderType.BOUNDARY_TRANSIT);
        }


        BooleanExpression where = lv.id.username.eq(username).and(o.orderType.in(orderTypes));


        if (carRfId != null) {
            jpaCountQuery = jpaCountQuery.from(o)
                .innerJoin(c).on(c.id.eq(o.customerId))
                .innerJoin(l).on(o.locationId.eq(l.id))
                .innerJoin(lv).on(l.id.eq(lv.id.id));
            where = where.and(c.carRfId.likeIgnoreCase(carRfId));
        }
        if (plaque != null) {
            jpaCountQuery = jpaCountQuery.from(o)
                .innerJoin(c).on(c.id.eq(o.customerId))
                .innerJoin(l).on(o.locationId.eq(l.id))
                .innerJoin(lv).on(l.id.eq(lv.id.id));
            where = where.and(c.plaque.likeIgnoreCase(plaque).or(c.plaqueTwo.likeIgnoreCase(plaque)));
        }


        Map<String, PathBuilder> map = new HashMap<>();
        PathBuilder<Order> order = new PathBuilder<>(Order.class, "j_order");
        PathBuilder<Customer> customer = new PathBuilder<>(Customer.class, "customer");
        PathBuilder<Location> location = new PathBuilder<>(Location.class, "location");
        map.put("location", location);
        map.put("customer", customer);
        BooleanExpression search = new PredicatesBuilder().build(query, order, map);

        if (search != null)
            where = where.and(search);

        jpaCountQuery.where(where);
        jpaQuery.where(where);
        jpaQuery = jpaQuery.distinct();
        if (pageable.getSort() != null) {
            for (Sort.Order o1 : pageable.getSort()) {
                jpaQuery.orderBy(new OrderSpecifier(o1.isAscending() ? Order.ASC
                    : Order.DESC, order.get(o1.getProperty())));
            }
        }

        long fetchCount = jpaCountQuery.fetchCount();
        jpaQuery.offset(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize());
        List<OrderCustomerLocationPerson> orders = jpaQuery.fetch();

        long size = (fetchCount < pageable.getPageSize() && pageable.getPageNumber() == 0) ?
            fetchCount :
            jpaQuery.offset(0).fetchCount();
        return new PageImpl<>(orders, pageable, size);
    }

    public Page<OrderCustomerLocationPerson> findAllOrderWithCustomer(String query, Long productRateId, Pageable pageable, @Param("username") String username, @Param("mode") String mode, String personName) {
        QOrder order = new QOrder("j_order");
        QSellContract sellContract = new QSellContract("sellContract");
        QCustomer customer = new QCustomer("customer");
        QCustomerType customerType = new QCustomerType("customerType");
        QProduct product = new QProduct("product");
        QOrderProduct orderProduct = new QOrderProduct("orderProduct");
        QPerson person = new QPerson("person");
        QDepot depot = new QDepot("depot");
        QUser user = new QUser("user");
        QCustomerAccess customerAccess = new QCustomerAccess("customerAccess");
        QPersonAccess personAccess = new QPersonAccess("personAccess");
        QLocation location = new QLocation("location");
        QOrderDepot orderDepot = new QOrderDepot("orderDepot");

        JPAQuery<OrderCustomerLocationPerson> jpaQuery = new JPAQuery<>(em);
        jpaQuery.from(order)
            .leftJoin(customer).on(customer.id.eq(order.customerId))
            .leftJoin(customerType).on(customerType.id.eq(customer.type.id))
            .leftJoin(customerAccess).on(customer.id.eq(customerAccess.id.id))
            .innerJoin(person).on(order.personId.eq(person.id))
            .innerJoin(personAccess).on(order.personId.eq(personAccess.id.id))
            .innerJoin(depot).on(order.depotId.eq(depot.id))
            .innerJoin(location).on(order.locationId.eq(location.id))
            .leftJoin(orderDepot).on(orderDepot.order.id.eq(order.id))
            .innerJoin(sellContract).on(sellContract.id.eq(order.sellContractId))
            .innerJoin(user).on(user.login.eq(order.lastModifiedBy));

        OrderType orderType = mode.equalsIgnoreCase(  "airplane") ? OrderType.AIRPLANE :
            mode.equalsIgnoreCase("refuel-center") ? OrderType.REFUEL_CENTER : null;

        QOrderCustomerLocationPerson qOrderCustomerLocationPerson = null;
        if (!mode.equals("airplane")) {
            if (productRateId != null) {
                jpaQuery.innerJoin(orderProduct)
                    .on(order.id.eq(orderProduct.order.id).and(orderProduct.productRateId.eq(productRateId)));
            } else {
                jpaQuery.innerJoin(orderProduct)
                    .on(order.id.eq(orderProduct.order.id));
            }
            jpaQuery.innerJoin(product).on(product.id.eq(orderProduct.productId));
            qOrderCustomerLocationPerson = new QOrderCustomerLocationPerson(person.as("person"),
                location.as("location"), customer.as("customer"), customerType.as("customerType"),
                order.as("j_order"), depot.as("depot"), user.as("user"), sellContract.as("sellContract"),
                product.as("product"), orderDepot.as("orderDepot"));
        } else {
            jpaQuery.innerJoin(orderProduct)
                .on(order.id.eq(orderProduct.order.id))
                .innerJoin(product)
                .on(productRateId == null ? product.id.eq(orderProduct.productId) : product.id.eq(orderProduct.productId).and(orderProduct.productRateId.eq(productRateId)));
            qOrderCustomerLocationPerson = new QOrderCustomerLocationPerson(person.as("person"),
                location.as("location"),
                customer.as("j_order"), customerType.as("customerType"),
                order.as("order"), depot.as("depot"), user.as("user"), sellContract.as("sellContract"),
                product.as("product"), null);
        }


        jpaQuery.select(qOrderCustomerLocationPerson);

        List<ContractType> contractTypes = convertContractType(mode);
        BooleanExpression where =
            (customerAccess.id.username.eq(username).or(order.customerId.isNull())).and(personAccess.id.username.eq(username))
                .and(sellContract.contractType.in(contractTypes));

        if (orderType != null) {
            where = where.and(order.orderType.eq(orderType));
        }

        if (personName != null) {
            where = where.and(person.name.like("%" + personName + "%")
                .or(person.firstName.like("%" + personName + "%")
                    .or(person.lastName.like("%" + personName + "%"))));
        }

        if (contractTypes.contains(ContractType.AIRPLANE)) {
            if (
                !SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ROLE_ADMIN) &&
                    !SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.VIEW_ALL_RECEIPT)) {
                where = where.and(order.createdBy.eq(username));
            }
        }

        PathBuilder<Order> orderPathBuilder = new PathBuilder<>(Order.class, "j_order");

        Map<String, PathBuilder> map = new HashMap<>();
        map.put("customer", new PathBuilder<>(Customer.class, "customer"));
        map.put("customerType", new PathBuilder<>(CustomerType.class, "customerType"));
        map.put("user", new PathBuilder<>(User.class, "user"));
        map.put("location", new PathBuilder<>(Location.class, "location"));
        map.put("sellContract", new PathBuilder<>(SellContract.class, "sellContract"));
        map.put("product", new PathBuilder<>(Product.class, "product"));


        BooleanExpression search = new PredicatesBuilder().build(query, orderPathBuilder, map);
        if (search != null)
            where = where.and(search);

        jpaQuery.where(where);

        long size = jpaQuery.fetchCount();

        jpaQuery.offset(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize());
        if (pageable.getSort() != null) {
            for (Sort.Order orders : pageable.getSort()) {
                jpaQuery.orderBy(new OrderSpecifier(orders.isAscending() ? Order.ASC
                    : Order.DESC, orderPathBuilder.get(orders.getProperty())));
            }
        }
        return new PageImpl<>(jpaQuery.fetch(), pageable, size);
    }


    @Override
    public Page<OrderCustomerLocationPerson> findAllByOrderBuy_GroupAndOrderCredit_Settled(String username, List<BuyGroup> buyGroups, Boolean settled, Pageable pageable, String query) {

        QOrder order = new QOrder("j_order");
        QOrderCredit orderCredit = new QOrderCredit("orderCredit");
        QCustomer customer = new QCustomer("customer");
        QPerson person = new QPerson("person");
        QDepot depot = new QDepot("depot");
        QCustomerAccess customerAccess = new QCustomerAccess("customerAccess");
        QLocation location = new QLocation("location");

        JPAQuery<OrderCustomerLocationPerson> jpaQuery = new JPAQuery<>(em);
        jpaQuery.from(order)
            .leftJoin(customer).on(customer.id.eq(order.customerId))
            .leftJoin(customerAccess).on(customer.id.eq(customerAccess.id.id))
            .innerJoin(person).on(order.personId.eq(person.id))
            .innerJoin(depot).on(order.depotId.eq(depot.id))
            .innerJoin(location).on(order.locationId.eq(location.id))
            .innerJoin(orderCredit).on(order.id.eq(orderCredit.order.id))
            .where(order.status.eq(OrderStatus.CREDIT_PAID).and(orderCredit.buyGroup.in(buyGroups)))
            .distinct();


        QOrderCustomerLocationPerson qOrderCustomerLocationPerson = new QOrderCustomerLocationPerson(
            order.as("j_order"),
            customer.as("customer"),
            person.as("person"),
            depot.as("depot"),
            location.as("location"));
        jpaQuery.select(qOrderCustomerLocationPerson);


        BooleanExpression where = customerAccess.id.username.isNull().or(customerAccess.id.username.eq(username));

        PathBuilder<Order> orderPathBuilder = new PathBuilder<>(Order.class, "j_order");

        Map<String, PathBuilder> map = new HashMap<>();
        map.put("customer", new PathBuilder<>(Customer.class, "customer"));
        map.put("person", new PathBuilder<>(Person.class, "person"));
        map.put("location", new PathBuilder<>(Location.class, "location"));
        map.put("depot", new PathBuilder<>(Depot.class, "depot"));

        BooleanExpression search = new PredicatesBuilder().build(query, orderPathBuilder, map);

        if (search != null)
            where = where.and(search);

        jpaQuery.where(where);

        long size = jpaQuery.fetch().size();

        jpaQuery.offset(pageable.getPageNumber() * pageable.getPageSize()).limit(pageable.getPageSize());
        for (Sort.Order orders : pageable.getSort()) {
            jpaQuery.orderBy(new OrderSpecifier(orders.isAscending() ? Order.ASC
                : Order.DESC, orderPathBuilder.get(orders.getProperty())));
        }
        return new PageImpl<>(jpaQuery.fetch(), pageable, size);
    }


    public CreditNotDepositedInTime hasCreditNotDepositedInTime(@Param("username") String username, @Param("buyGroup") BuyGroup buyGroup, @Param("settled") Boolean settled, @Param("customerId") Long customerId, @Param("personId") Long personId) {
        QOrder o = new QOrder("o");
        QOrderCredit oc = new QOrderCredit("oc");
        QCustomer c = new QCustomer("c");
        QPerson p = new QPerson("p");
        QDepot d = new QDepot("d");
        QCustomerAccess cv = new QCustomerAccess("cv");
//        QPersonView pv = new QPersonView("pv");
        QLocation l = new QLocation("l");
//        QLocationView lv = new QLocationView("lv");
        QBuyType bt = new QBuyType("bt");
        JPAQuery<BuyType> jpaQuery = new JPAQuery<>(em);
        jpaQuery.from(o)
            .leftJoin(c).on(c.id.eq(o.customerId))
            .leftJoin(cv).on(c.id.eq(cv.id.id))
            .innerJoin(p).on(o.personId.eq(p.id))
//            .innerJoin(pv).on(p.id.eq(pv.id))
            .innerJoin(d).on(o.depotId.eq(d.id))
            .innerJoin(l).on(o.locationId.eq(l.id))
//            .innerJoin(lv).on(l.id.eq(lv.id))
            .innerJoin(oc).on(o.id.eq(oc.order.id))
            .innerJoin(bt).on(o.buyGroup.eq(bt.buyGroup))
            .where(
                oc.settled.eq(settled)
                    .and(oc.buyGroup.eq(buyGroup))
                    .and(Expressions.booleanTemplate("DATEADD(day,{0},{1}) < getdate()",
                        bt.effectDate, o.registerDate)))
            .distinct();

        jpaQuery.select(bt);

        BooleanExpression where = cv.id.username.isNull().or(cv.id.username.eq(username));
        if (customerId != null)
            where = where.and(p.id.eq(personId).or(c.id.eq(customerId)));
        else
            where = where.and(p.id.eq(personId));

        jpaQuery.where(where);

        List<BuyType> fetch = jpaQuery.fetch();

        if (fetch.size() <= 0) return CreditNotDepositedInTime.OK;
        for (BuyType buyType : fetch) {
            if (buyType.getSellLimit())
                return CreditNotDepositedInTime.SELL_LIMIT;
        }
        return CreditNotDepositedInTime.SELL_ALARM;

    }

    public List<Map<String, Object>> sendtoDepot(ZonedDateTime startDate, ZonedDateTime finishDate) {
        String query = "select " +
            "  jOrder.id                         ID, " +
            "  customer.identifyCode                     CustomerCode, " +
            "  location.code                     LocationCode, " +
            "  depot.code                        AnbarCode, " +
            "  jOrder.order_no                   HavalehNo, " +
            "  product.code                      productCode, " +
            "  orderProduct.amount               Quantity, " +
            "  orderProduct.product_rate_price   unitPrice, " +
            "  orderProduct.total_price          ProductPrice, " +
            "  orderProduct.base_price           TotalPrice, " +
            "  sum(orderPayment.decreased_price) netprice, " +
            "  jOrder.register_date              RegisterDate, " +
            "  jOrder.expires                    EtebarDay, " +
            "  consumption.code                  ConsumeTypeCode, " +
            "  customer.identifyCode                     HamlCode, " +
            "  transportContract.contract_code   ContractNo, " +
            "  1                                 SellDraftType, " +
            "  GETDATE()                         Senddate, " +
            "  case when consumption.code = '15' " +
            "    then 1 " +
            "  else 3 end                        type, " +
            "  case when sellContractCustomer.has_transport = 1 " +
            "    then 4 " +
            "  else 2 end                        ShipType, " +
            "  person.economic_code              EconomicCode, " +
            "  customer.name                     CustomerName, " +
            "  customer.address                  Address, " +
            "  '687173'                          fishno " +
            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jOrder " +
            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on jOrder.customer_id = customer.id " +
            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.customer_type customerType on customer.type_id = customerType.id " +
            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.location location on jOrder.location_id = location.id " +
            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on jOrder.depot_id = depot.id " +
            "  inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product orderProduct on jOrder.id = orderProduct.order_id " +
            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on orderProduct.product_id = product.id " +
            "  inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_payment orderPayment on jOrder.id = orderPayment.order_id " +
            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.sell_contract_product sellContractProduct " +
            "    on orderProduct.sell_contract_product_id = sellContractProduct.id " +
            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.consumption consumption on sellContractProduct.consumption_id = consumption.id " +
            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on jOrder.person_id = person.id " +
            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.sell_contract_customer sellContractCustomer on " +
            "                                                                              sellContractProduct.sell_contract_customer_id " +
            "                                                                              = sellContractCustomer.id " +
            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.transport_contract transportContract on ( " +
            "    customer.id = transportContract.customer_id " +
            "    and transportContract.start_date >= jOrder.register_date and " +
            "    (transportContract.finish_date is null or transportContract.finish_date <= jOrder.register_date) " +
            "    ) " +
            " " +
            "group by jOrder.id, " +
            "  customer.identifyCode, " +
            "  location.code, " +
            "  depot.code, " +
            "  jOrder.order_no, " +
            "  product.code, " +
            "  jOrder.register_date, " +
            "  jOrder.expires, " +
            "  consumption.code, " +
            "  orderProduct.amount, " +
            "  orderProduct.product_rate_price, " +
            "  orderProduct.total_price, " +
            "  orderProduct.base_price, " +
            "  person.economic_code, " +
            "  customer.name, " +
            "  customer.address, " +
            "  transportContract.contract_code, " +
            "  sellContractCustomer.has_transport";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startDate", new Timestamp(startDate.toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
        parameters.addValue("finishDate", new Timestamp(finishDate.toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
        return jdbcTemplate.queryForList(query, parameters);

    }

    @Override
    public Page<AirplaneListDTO> findAllAirplane(String orderNo,
                                                 String personName,
                                                 String customerName,
                                                 Long amount,
                                                 String status,
                                                 String buyGroup,
                                                 String name,
                                                 String unitTitle,
                                                 String productTitle,
                                                 Pageable pageable) {
        Map<String, String> maps = new HashMap<String, String>() {{
            put("productId", "product_id");
            put("personId", "person_id");
            put("customerId", "customer_id");
            put("orderNo", "order_no");
            put("buyGroup", "buy_group");
        }};

        String sort = null;
        if (ObjectUtils.nonNull(pageable.getSort())) {
            sort = StringUtils.collectionToCommaDelimitedString(
                StreamSupport.stream(pageable.getSort().spliterator(), false)
                    .map(o -> maps.getOrDefault(o.getProperty(), o.getProperty()) + " " + o.getDirection())
                    .collect(Collectors.toList()));
        }

        String where = " where location_access.username = :username " +
            "and " +
            "  (j_order.order_type='AIRPLANE') " +
            "  and " +
            "  (:orderNo is null or j_order.order_no like :orderNo) " +
            "  and " +
            "  (:personName is null or person.name like :personName) " +
            "  and " +
            "  (:customerName is null or customer.name like :customerName) " +
            "  and " +
            "  (:amount is null or j_order.amount = :amount) " +
            "  and " +
            "  (:status is null or j_order.status like :status) " +
            "  and " +
            "  (:buyGroup is null or j_order.buy_group like :buyGroup) " +
            "  and " +
            "  (:name is null or (jhi_user.first_name like :name or jhi_user.last_name like :name)) " +
            "  and " +
            "  (:unitTitle is null or orderUnitConcat.unitTitle like :unitTitle) " +
            "  and " +
            "  (:productTitle is null or orderProductConcat.productTitle like :productTitle)";


        String query = "with orderUnitConcat as (select distinct log_book.order_id                                as orderId, " +
            "                                         niopdcorder_" + Profiles.activeProfile + ".dbo.group_concat(oil_tank.title) as unitTitle " +
            "                         from niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book " +
            "                                inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log_book.day_depot_id = day_depot.id " +
            "                                inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
            "                                inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
            "                                           on day_depot.main_day_operation_id = main_day_operation.id " +
            "                         group by log_book.order_id), " +
            "     orderProductConcat as ( " +
            "       select distinct j_order.id as orderId,niopdcorder_" + Profiles.activeProfile + ".dbo.group_concat(product.title) as productTitle " +
            "       from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
            "              inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
            "       group by j_order.id " +
            "     ) " +
            " " +
            "select j_order.id as id, " +
            "       j_order.order_no                as orderNo, " +
            "       person.name                     as personName, " +
            "       customer.name                   as customerName, " +
            "       j_order.register_date           as registerDate, " +
            "       j_order.amount                  as amount, " +
            "       j_order.status                  as status, " +
            "       j_order.buy_group               as buyGroup, " +
            "       jhi_user.first_name             as firstName, " +
            "       jhi_user.last_name              as lastName, " +
            "       orderUnitConcat.unitTitle       as unitTitle, " +
            "       orderProductConcat.productTitle as productTitle " +
            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = j_order.person_id " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on customer.id = j_order.customer_id " +
            "       inner join niopdcuaa_" + Profiles.activeProfile + ".dbo.jhi_user jhi_user on jhi_user.login = j_order.created_by " +
            "       left join orderUnitConcat orderUnitConcat on orderUnitConcat.orderId = j_order.id " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.location location on location.id = j_order.location_id " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.location_access location_access on location_access.id = location.id " +
            "       left join orderProductConcat orderProductConcat on orderProductConcat.orderId = j_order.id "
            + where +
            " ORDER BY " + sort + " OFFSET :skipRows ROWS FETCH NEXT :takeRows ROWS ONLY ";

        String countQuery = "with orderUnitConcat as (select distinct log_book.order_id                                as orderId, " +
            "                                         niopdcorder_" + Profiles.activeProfile + ".dbo.group_concat(oil_tank.title) as unitTitle " +
            "                         from niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book " +
            "                                inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log_book.day_depot_id = day_depot.id " +
            "                                inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
            "                                inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
            "                                           on day_depot.main_day_operation_id = main_day_operation.id " +
            "                         group by log_book.order_id), " +
            "     orderProductConcat as ( " +
            "       select distinct j_order.id as orderId,niopdcorder_" + Profiles.activeProfile + ".dbo.group_concat(product.title) as productTitle " +
            "       from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
            "              inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
            "       group by j_order.id " +
            "     ) " +
            " " +
            "select count(j_order.order_no)  " +
            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = j_order.person_id " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on customer.id = j_order.customer_id " +
            "       inner join niopdcuaa_" + Profiles.activeProfile + ".dbo.jhi_user jhi_user on jhi_user.login = j_order.created_by " +
            "       left join orderUnitConcat orderUnitConcat on orderUnitConcat.orderId = j_order.id " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.location location on location.id = j_order.location_id " +
            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.location_access location_access on location_access.id = location.id " +
            "       left join orderProductConcat orderProductConcat on orderProductConcat.orderId = j_order.id "
            + where;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("username", SecurityUtils.getCurrentUserLogin().get());
        parameters.addValue("orderNo", ObjectUtils.getLikeForWhere(orderNo));
        parameters.addValue("personName", ObjectUtils.getLikeForWhere(personName));
        parameters.addValue("customerName", ObjectUtils.getLikeForWhere(customerName));
        parameters.addValue("status", ObjectUtils.getLikeForWhere(status));
        parameters.addValue("buyGroup", ObjectUtils.getLikeForWhere(buyGroup));
        parameters.addValue("name", ObjectUtils.getLikeForWhere(name));
        parameters.addValue("unitTitle", ObjectUtils.getLikeForWhere(unitTitle));
        parameters.addValue("productTitle", ObjectUtils.getLikeForWhere(productTitle));
        parameters.addValue("amount", amount);
        parameters.addValue("skipRows", pageable.getOffset());
        parameters.addValue("takeRows", pageable.getPageSize());
        List<AirplaneListDTO> findAll = jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<>(AirplaneListDTO.class));

        int size = findAll.size() < pageable.getPageSize() && pageable.getOffset() == 0 ? findAll.size()
            : jdbcTemplate.queryForObject(countQuery, parameters, Integer.class);

        return new PageImpl<>(findAll, pageable, size);
    }

}
