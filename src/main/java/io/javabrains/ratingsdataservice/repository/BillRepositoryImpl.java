//package io.javabrains.ratingsdataservice.repository;
//
//import ir.donyapardaz.niopdc.report.config.Profiles;
//import ir.donyapardaz.niopdc.report.service.dto.ao.BillInfoReportDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.BillItemReportDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.BillReportDTO;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.core.support.JdbcDaoSupport;
//import org.springframework.stereotype.Repository;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.sql.DataSource;
//import java.util.List;
//
//@SuppressWarnings("unused")
//@Repository
//public class BillRepositoryImpl extends JdbcDaoSupport implements BillRepository {
//    private final Logger log = LoggerFactory.getLogger(BillRepositoryImpl.class);
//
//    @PersistenceContext
//    EntityManager entityManager;
//    private NamedParameterJdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public void setDs(DataSource dataSource) {
//        setDataSource(dataSource);
//    }
//
//    @PostConstruct
//    private void postConstruct() {
//        jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
//    }
//
//
//    public BillReportDTO report(Long id) {
//        String query =
//                "select * from (select product.title + isnull(N' - پلکان ' + cast(product_step.step_no as nvarchar), '') title,\n" +
//                    "       bill.register_date                                                               date,\n" +
//                    "       sum(order_product.amount)                                                        amount,\n" +
//                    "       order_product.product_rate_price                                                 rate,\n" +
//                    "       sum(order_product.total_price)                                                   credit,\n" +
//                    "       0                                                                                debit\n" +
//                    "\n" +
//                    "from niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill\n" +
//                    "       inner join niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill_item on bill.id = bill_item.bill_id and type = 'STEP'\n" +
//                    "       inner join niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill_order_ids on bill.id = bill_order_ids.bill_id\n" +
//                    "       inner join niopdcorder_" + Profiles.activeProfile + ".dbo.j_order o on o.id = order_ids\n" +
//                    "       inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product on order_product.order_id = o.id\n" +
//                    "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product on product.id = order_product.product_id\n" +
//                    "       inner join niopdcrate_" + Profiles.activeProfile + ".dbo.product_rate on order_product.product_rate_id = product_rate.id\n" +
//                    "       left join niopdcrate_" + Profiles.activeProfile + ".dbo.product_step on product_rate.product_step_id = product_step.id\n" +
//                    "     where bill.id = :billId\n" +
//                    "group by product.title + isnull(N' - پلکان ' + cast(product_step.step_no as nvarchar), ''),\n" +
//                    "         bill.register_date,\n" +
//                    "         order_product.product_rate_price\n" +
//                    "union\n" +
//                    "\n" +
//                    "select product.title + isnull(N' - پلکان ' + cast(product_step.step_no as nvarchar), '') title,\n" +
//                    "       bill.register_date                                                               date,\n" +
//                    "       bill_item_product.amount                                                         amount,\n" +
//                    "       bill_item_product.product_rate_price                                             rate,\n" +
//                    "       0                                                                                credit,\n" +
//                    "       bill_item_product.total_price                                                    debit\n" +
//                    "\n" +
//                    "from niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill\n" +
//                    "       inner join niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill_item on bill.id = bill_item.bill_id and type = 'STEP'\n" +
//                    "       inner join niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill_item_product on bill_item.id = bill_item_product.bill_item_id\n" +
//                    "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product on product.id = bill_item_product.product_id\n" +
//                    "       inner join niopdcrate_" + Profiles.activeProfile + ".dbo.product_rate on bill_item_product.product_rate_id = product_rate.id\n" +
//                    "       left join niopdcrate_" + Profiles.activeProfile + ".dbo.product_step on product_rate.product_step_id = product_step.id\n" +
//                    "     where bill.id = :billId\n" +
//                    "union\n" +
//                    "\n" +
//                    "select N'هزینه صورت حساب'                        title,\n" +
//                    "       bill.register_date                       date,\n" +
//                    "       null                                     amount,\n" +
//                    "       null                                     rate,\n" +
//                    "       0                                        credit,\n" +
//                    "       (bill_item.price - bill_item.last_price) debit\n" +
//                    "\n" +
//                    "from niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill\n" +
//                    "       inner join niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill_item on bill.id = bill_item.bill_id and type = 'COST'\n" +
//                    "     where bill.id = :billId\n" +
//                    "\n" +
//                    "union\n" +
//                    "\n" +
//                    "select N'هزینه تبخیر'                            title,\n" +
//                    "       bill.register_date                       date,\n" +
//                    "       null                                     amount,\n" +
//                    "       null                                     rate,\n" +
//                    "       0                                        credit,\n" +
//                    "       (bill_item.price - bill_item.last_price) debit\n" +
//                    "\n" +
//                    "from niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill\n" +
//                    "       inner join niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill_item on bill.id = bill_item.bill_id and type = 'EVAPORATION'\n" +
//                    "     where bill.id = :billId\n" +
//                    "\n" +
//                    "union\n" +
//                    "\n" +
//                    "select N'کارمزد متمم'                            title,\n" +
//                    "       bill.register_date                       date,\n" +
//                    "       null                                     amount,\n" +
//                    "       null                                     rate,\n" +
//                    "       (bill_item.last_price - bill_item.price)                                        credit,\n" +
//                    "       0 debit\n" +
//                    "\n" +
//                    "from niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill\n" +
//                    "       inner join niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill_item on bill.id = bill_item.bill_id and type = 'SUPPLEMENT'\n" +
//                    "       where bill.id = :billId) billreport where billreport.credit != 0 or billreport.debit != 0" ;
//
//        String queryInfo =
//                "select bill.id        billNo,\n" +
//                    "       bill.created_date   createdDate,\n" +
//                    "       bill.created_by     createdBy,\n" +
//                    "       customer.name       customerName,\n" +
//                    "       location.name       LocationName,\n" +
//                    "       locationParent.name parentLocationName,\n" +
//                    "       start_date          startDate,\n" +
//                    "       finish_date         finishDate\n" +
//                    "from niopdcaccounting_" + Profiles.activeProfile + ".dbo.bill\n" +
//                    "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.location on location.id = location_id\n" +
//                    "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.location locationParent on locationParent.id = location.location_parent_id\n" +
//                    "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer on customer.id = customer_id" +
//                    " where bill.id = :billId" ;
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("billId", id);
//
//        List billItems = jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(BillItemReportDTO.class));
//
//        List<BillInfoReportDTO> infos = jdbcTemplate.query(queryInfo, parameters, new BeanPropertyRowMapper(BillInfoReportDTO.class));
//
//        return new BillReportDTO(infos.get(0), billItems);
//
//    }
//
//}
