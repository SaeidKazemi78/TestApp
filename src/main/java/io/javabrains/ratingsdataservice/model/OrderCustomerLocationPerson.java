//package io.javabrains.ratingsdataservice.model;
//
//import com.querydsl.core.annotations.QueryProjection;
//import ir.donyapardaz.niopdc.order.domain.Order;
//import ir.donyapardaz.niopdc.order.domain.OrderDepot;
//
//public class OrderCustomerLocationPerson {
//    private Person person;
//    private Location location;
//    private Customer customer;
//    private CustomerType customerType;
//    private Order order;
//    private Depot depot;
//    private User user;
//    private SellContract sellContract;
//    private Product product;
//    private OrderDepot orderDepot;
//
//    @QueryProjection
//    public OrderCustomerLocationPerson(Person person, Location location, Customer customer, CustomerType customerType, Order order, Depot depot, User user, SellContract sellContract, Product product, OrderDepot orderDepot) {
//        this.person = person;
//        this.location = location;
//        this.customer = customer;
//        this.customerType = customerType;
//        this.order = order;
//        this.depot = depot;
//        this.user = user;
//        this.sellContract = sellContract;
//        this.product = product;
//        this.orderDepot = orderDepot;
//    }
//
//    @QueryProjection
//    public OrderCustomerLocationPerson(Order order, Customer customer, Person person, Depot depot, Location location) {
//        this.order = order;
//        this.customer = customer;
//        this.depot = depot;
//        this.person = person;
//        this.location = location;
//
//    }
//
//    @QueryProjection
//    public OrderCustomerLocationPerson(Order order, Customer customer, Person person, Depot depot, Location location, User user, SellContract sellContract) {
//        this.order = order;
//        this.customer = customer;
//        this.depot = depot;
//        this.person = person;
//        this.location = location;
//        this.user = user;
//        this.sellContract = sellContract;
//    }
//
//    @QueryProjection
//    public OrderCustomerLocationPerson(Order order, Customer customer, Location location) {
//        this.order = order;
//        this.customer = customer;
//        this.location = location;
//
//    }
//
//    public Person getPerson() {
//        return person;
//    }
//
//    public void setPerson(Person person) {
//        this.person = person;
//    }
//
//    public Location getLocation() {
//        return location;
//    }
//
//    public void setLocation(Location location) {
//        this.location = location;
//    }
//
//    public Customer getCustomer() {
//        return customer;
//    }
//
//    public void setCustomer(Customer customer) {
//        this.customer = customer;
//    }
//
//    public Order getOrder() {
//        return order;
//    }
//
//    public void setOrder(Order order) {
//        this.order = order;
//    }
//
//    public Depot getDepot() {
//        return depot;
//    }
//
//    public void setDepot(Depot depot) {
//        this.depot = depot;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public SellContract getSellContract() {
//        return sellContract;
//    }
//
//    public void setSellContract(SellContract sellContract) {
//        this.sellContract = sellContract;
//    }
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//    public OrderDepot getOrderDepot() {
//        return orderDepot;
//    }
//
//    public OrderCustomerLocationPerson setOrderDepot(OrderDepot orderDepot) {
//        this.orderDepot = orderDepot;
//        return this;
//    }
//
//    public CustomerType getCustomerType() {
//        return customerType;
//    }
//
//    public void setCustomerType(CustomerType customerType) {
//        this.customerType = customerType;
//    }
//}
