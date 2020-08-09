//package io.javabrains.ratingsdataservice.repository;
//
//import ir.donyapardaz.niopdc.report.config.Profiles;
//import ir.donyapardaz.niopdc.report.service.dto.ao.*;
//import ir.donyapardaz.niopdc.report.service.dto.ao.depot.AoInvoiceDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.depot.TotalPlatformDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.depot.TotalPlatformRequestDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.depot.depotInventory.DepotInventoryDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.depot.depotInventory.DepotInventoryRequestDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.depot.ninePage.NinePageDetailDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.depot.ninePage.NinePageRequestDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.print.AoPrintInfoDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.print.AoPrintUnitsDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.wayBill.SealUseDTO;
//import ir.donyapardaz.niopdc.report.service.dto.ao.wayBill.WayBillDTO;
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
//import java.sql.Timestamp;
//import java.sql.Types;
//import java.time.ZonedDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@SuppressWarnings("unused")
//@Repository
//public class AoRepositoryImpl extends JdbcDaoSupport implements AoRepository {
//    private final Logger log = LoggerFactory.getLogger(AoRepositoryImpl.class);
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
//    public List<AircraftRefuelingRecordDTO> findAllAircraftRefuelingRecords(AircraftRefuelingRecordRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            " case" +
//            " when jo.buy_group='CASH' then " +
//            " 'CA'+jo.order_no " +
//            " when jo.buy_group='CREDIT' then " +
//            " 'CR'+jo.order_no " +
//            " else jo.order_no" +
//            " end " +
//            "                             as receiptNo," +
//            "  jo.created_date                         as dateTime," +
//            "  refuel_center.persian_title              as airportName," +
//            "  sum(op.amount)                                as amount," +
//            "  product.code                             as productCode," +
//            "  product.title                            as productTitle," +
//            "  jo.fuel_type                             as type," +
//            "  case when person.personality = 'LEGAL'" +
//            "    then person.name" +
//            "  else person.first_name + ' ' + person.last_name" +
//            "  end                                      as personName," +
//            "  customer.name                            as customerName," +
//            "  currency.title                           as currencyTitle," +
//            "  jo.cost_price                            as cost_price," +
//            "  jo.product_price                         as product_price," +
//            "  jo.price                                 as price," +
//            "  juser.first_name + ' ' + juser.last_name as userName" +
//            " from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on log_book.order_id = jo.id " +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log_book.day_depot_id = day_depot.id " +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation on day_depot.main_day_operation_id = main_day_operation.id " +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = jo.person_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on customer.id = jo.customer_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = jo.depot_id" +
//            "  inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product op on op.order_id = jo.id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = op.product_id" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center refuel_center on refuel_center.id = depot.refuel_center_id" +
//            "  inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on currency.id = jo.currency_id" +
//            "  inner join niopdcuaa_" + Profiles.activeProfile + ".dbo.jhi_user juser on juser.login = op.created_by" +
//            " where " +
//            "  jo.status = 'CONFIRM' " +
//            "  and jo.type_of_fuel_receipt='UNIT_TO_AIRPLANE'" +
//            "  and jo.order_type = 'AIRPLANE'" +
//            "  and main_day_operation.day >= :startDate" +
//            "  and main_day_operation.day <= :finishDate" +
//            "  and (:refuelCenterId is null or refuel_center.id=:refuelCenterId) " +
//            "  and (:receiptNo is null or jo.order_no like :receiptNo) " +
//            "  and (:customerId is null or customer.id=:customerId) " +
//            "  and (:personId is null or person.id=:personId) " +
//            "group by jo.buy_group,jo.order_no,jo.created_date, " +
//            "refuel_center.persian_title, product.code, product.title, " +
//            "jo.fuel_type, customer.name, currency.title, jo.cost_price, " +
//            "jo.product_price, jo.price, person.last_name, person.first_name, " +
//            "person.name, person.personality, " +
//            "juser.first_name, juser.last_name " +
//            "order by product.title,jo.created_date";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//        parameters.addValue("customerId", req.getCustomerId());
//        parameters.addValue("personId", req.getPersonId());
//        parameters.addValue("receiptNo", req.getReceiptNo() == null ? null : "%" + req.getReceiptNo() + "%");
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AircraftRefuelingRecordDTO.class));
//    }
//
//    @Override
//    public List<BillWithoutContainerDTO> findAllBillWithoutContainers(BillWithoutContainerRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            "  order_person.countDomesticFlightAirplane      as countDomesticFlightAirplane," +
//            "  order_person.sumDomesticFlightAmount          as sumDomesticFlightAmount," +
//            "  order_person.sumInternationalFlightAmount     as sumInternationalFlightAmount," +
//            "  order_person.countInternationalFlightAirplane as countInternationalFlightAirplane," +
//            "  case when person.personality = 'LEGAL'" +
//            "    then person.name" +
//            "  else person.first_name + ' ' + person.last_name" +
//            "  end                                           as personName," +
//            "  product.title," +
//            "  refuel_center.persian_title                   as airportName," +
//            "  order_person.sellType                         as sellType," +
//            "  order_person.currency                         as currency," +
//            "  order_person.buy_group                         as buyGroup" +
//            " from (" +
//            "       select" +
//            "         op.product_id                  product_id," +
//            "         jo.person_id                   person_id," +
//            "         depot.refuel_center_id         refuel_center_id," +
//            "         sum(op.amount)                 sumDomesticFlightAmount," +
//            "         count(DISTINCT jo.customer_id) countDomesticFlightAirplane," +
//            "         0                              sumInternationalFlightAmount," +
//            "         0                              countInternationalFlightAirplane," +
//            "         case" +
//            "         when" +
//            "           jo.type_of_fuel_receipt = 'UNIT_TO_AIRPLANE' or jo.type_of_fuel_receipt = 'UNIT_TO_CUSTOMERS'" +
//            "           then 'میدان'" +
//            "         when" +
//            "           jo.type_of_fuel_receipt = 'TANKER_SALES' or jo.type_of_fuel_receipt = 'PIPE_LINE_SALES'" +
//            "           then 'انبار'" +
//            "         end            as              sellType," +
//            "         currency.title as              currency," +
//            "         jo.buy_group as buy_group" +
//            "       from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo" +
//            "         inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product op on op.order_id = jo.id" +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = jo.depot_id" +
//            "         left join niopdcao_" + Profiles.activeProfile + ".dbo.airport airport on airport.id = jo.target_airport" +
//            "         left join niopdcbase_" + Profiles.activeProfile + ".dbo.country country on country.id = airport.country_id" +
//            "         inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on currency.id = jo.currency_id" +
//            "       where" +
//            "         jo.status <> 'DERAFT'" +
//            "         and jo.order_type = 'AIRPLANE'" +
//            "         and (jo.target_airport is null or country.check_national_code = 1)" +
//            "                and jo.register_date >= :startDate" +
//            "                and jo.register_date <= :finishDate" +
//            "                and depot.refuel_center_id=:refuelCenterId" +
//            "       group by op.product_id, jo.person_id, depot.refuel_center_id, jo.type_of_fuel_receipt,currency.title,jo.buy_group" +
//            "       union" +
//            "       select" +
//            "         op.product_id                  product_id," +
//            "         jo.person_id                   person_id," +
//            "         depot.refuel_center_id         refuel_center_id," +
//            "         0                              sumDomesticFlightAmount," +
//            "         0                              countDomesticFlightAirplane," +
//            "         sum(op.amount)                 sumInternationalFlightAmount," +
//            "         count(DISTINCT jo.customer_id) countInternationalFlightAirplane," +
//            "         case" +
//            "         when" +
//            "           jo.type_of_fuel_receipt = 'UNIT_TO_AIRPLANE' or jo.type_of_fuel_receipt = 'UNIT_TO_CUSTOMERS'" +
//            "           then 'میدان'" +
//            "         when" +
//            "           jo.type_of_fuel_receipt = 'TANKER_SALES' or jo.type_of_fuel_receipt = 'PIPE_LINE_SALES'" +
//            "           then 'انبار'" +
//            "         end as                         sellType," +
//            "         currency.title as              currency," +
//            "         jo.buy_group as buy_group" +
//            "       from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo" +
//            "         inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product op on op.order_id = jo.id" +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = jo.depot_id" +
//            "         left join niopdcao_" + Profiles.activeProfile + ".dbo.airport airport on airport.id = jo.target_airport" +
//            "         left join niopdcbase_" + Profiles.activeProfile + ".dbo.country country on country.id = airport.country_id" +
//            "         inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on currency.id = jo.currency_id" +
//            "       where" +
//            "         jo.status <> 'DERAFT'" +
//            "         and jo.order_type = 'AIRPLANE'" +
//            "         and (jo.target_airport is not null and country.check_national_code is null or country.check_national_code = 0)" +
//            "                and jo.register_date >= :startDate" +
//            "                and jo.register_date <= :finishDate" +
//            "                and depot.refuel_center_id=:refuelCenterId" +
//            "       group by op.product_id, jo.person_id, depot.refuel_center_id, jo.type_of_fuel_receipt,currency.title,jo.buy_group" +
//            "     ) as order_person" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = order_person.person_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_person.product_id" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center refuel_center on refuel_center.id = order_person.refuel_center_id " +
//            "order by person.name,person.last_name";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(BillWithoutContainerDTO.class));
//    }
//
//    @Override
//    public List<AirplaneDTO> findAllAirplanes(AirplaneRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            "  customer.name           as name," +
//            "  customer.identify_code           as code," +
//            "  vehicle_capacity.capacity as capacity," +
//            "  vehicle_model.title    as model," +
//            "  p.title                 as productTitle" +
//            " from niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer_type customer_type on customer.type_id = customer_type.id" +
//            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.vehicle_model vehicle_model on customer.vehicle_model_id = vehicle_model.id" +
//            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.vehicle_capacity vehicle_capacity on vehicle_capacity.vehicle_model_id= vehicle_model.id" +
//            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.product p on vehicle_capacity.product_id = p.id" +
//            " where customer_type.customer_group = 'AIRPLANE'";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AirplaneDTO.class));
//    }
//
//    @Override
//    public List<AirlineDTO> findAllAirlines(AirlineRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select distinct" +
//            "  case when person.personality = 'LEGAL'" +
//            "        then person.name" +
//            "      else person.first_name + ' ' + person.last_name" +
//            "      end                                      as name," +
//            "  person.code," +
//            "  sell_contract_person.credit_account as creditAccount" +
//            " from niopdcbase_" + Profiles.activeProfile + ".dbo.sell_contract sell_contract" +
//            " inner join niopdcbase_" + Profiles.activeProfile + ".dbo.sell_contract_customer sell_contract_customer on sell_contract.id = sell_contract_customer.sell_contract_id" +
//            " inner join niopdcbase_" + Profiles.activeProfile + ".dbo.sell_contract_person sell_contract_person on sell_contract.id = sell_contract_person.sell_contract_id" +
//            " inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on sell_contract_customer.customer_id = customer.id" +
//            " inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer_type customer_type on customer.type_id = customer_type.id" +
//            " inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on sell_contract_person.person_id = person.id" +
//            " where customer_type.customer_group='AIRPLANE'";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AirlineDTO.class));
//    }
//
//    @Override
//    public List<TotalSellDTO> findAllTotalSells(TotalSellRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            "  product.title productName," +
//            "  product.code productCode," +
//            "  jo.fuel_type," +
//            "  count(jo.id) count," +
//            "  sum(op.amount) amount," +
//            "  sum(op.total_price) price," +
//            "  refuel_center.persian_title              as airportName ," +
//            "  CONVERT(date,jo.register_date) day" +
//            " from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo" +
//            " inner join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log on log.order_id = jo.id " +
//            " inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log.day_depot_id = day_depot.id " +
//            " inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation on day_depot.main_day_operation_id = main_day_operation.id " +
//            "  inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product op on op.order_id = jo.id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = op.product_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = jo.depot_id" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center refuel_center on refuel_center.id = depot.refuel_center_id" +
//            "  where " +
//            "         jo.status ='CONFIRM' " +
//            "         and jo.order_type = 'AIRPLANE'" +
//            "  and main_day_operation.day >= :startDate" +
//            "  and main_day_operation.day <= :finishDate" +
//            "  and refuel_center.id=:refuelCenterId " +
//            "group by product.id, product.title,product.code , jo.fuel_type,refuel_center.id,refuel_center.persian_title , CONVERT(date,jo.register_date)";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(TotalSellDTO.class));
//    }
//
//    @Override
//    public List<UnitDTO> findAllUnits(UnitRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            "  oil_tank.title unitName," +
//            "  center.persian_title refuelCenter," +
//            "  product.title productName," +
//            "  product.code productCode," +
//            "  sum(metre_log.amount) amount" +
//            " from niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre metre on metre_log.metre_id = metre.id" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on metre.oil_tank_id = oil_tank.id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on metre_log.log_book_id = log_book.id" +
//            "  left join niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order on j_order.id = log_book.order_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot log_book_day_depot on log_book_day_depot.id = log_book.day_depot_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.transfer transfer on transfer.id = metre_log.transfer_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot from_day_depot on from_day_depot.id = transfer.from_day_depot_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot to_day_depot on to_day_depot.id = transfer.to_day_depot_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank to_oil_tank on to_oil_tank.id = to_day_depot.oil_tank_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.transfer_platform_to_unit transfer_platform_to_unit" +
//            "    on transfer_platform_to_unit.id = metre_log.transfer_platform_to_unit_id" +
//            "  left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot transfer_platform_to_unit_day_depot" +
//            "    on transfer_platform_to_unit_day_depot.id = transfer_platform_to_unit.platform_id" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center center on oil_tank.refuel_center_id = center.id" +
//            " left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot" +
//            "  on" +
//            "   ( log_book_day_depot.main_day_depot_id = main_day_depot.id" +
//            "    or transfer_platform_to_unit_day_depot.main_day_depot_id = main_day_depot.id" +
//            "    or from_day_depot.main_day_depot_id = main_day_depot.id " +
//            "   )" +
//            " left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation" +
//            "  on " +
//            "( log_book_day_depot.main_day_operation_id = main_day_operation.id" +
//            "  or from_day_depot.main_day_operation_id = main_day_operation.id" +
//            ")" +
//            " where " +
//            "(" +
//            "       main_day_depot.id is null " +
//            "       or (" +
//            "               main_day_depot.day >= :startDate " +
//            "               and main_day_depot.day <= :finishDate " +
//            "           )" +
//            "       )" +
//            "      and " +
//            "      (" +
//            "       main_day_operation.id is null " +
//            "       or (" +
//            "               main_day_operation.day >= :startDate " +
//            "               and main_day_operation.day <= :finishDate " +
//            "           )" +
//            "       )" +
//            " and (main_day_depot.id is not null or main_day_operation.id is not null)" +
//            "  and center.id=:refuelCenterId " +
//            " group by oil_tank.title,center.persian_title,product.title,product.code";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(UnitDTO.class));
//    }
//
//    @Override
//    public List<MetreDTO> findAllMetres(MetreRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query =
//            "select max(metre_log.register_date)                   dayDate, " +
//                "       oil_tank.title                                 unitName, " +
//                "       product.title                                  productName, " +
//                "       metre_log.fuel_type                            fuelType, " +
//                "       isnull(case when metre_log.fuel_type='RE_FUEL' then sum(metre_log.amount) else sum(metre_log.amount) * -1 end, 0)              amount, " +
//                "       isnull(count(metre_log.id), 0)                 count, " +
//                "       isnull(min(metre_log.start_meter), 0)          startMetre, " +
//                "       isnull(max(metre_log.end_meter), metre.amount) endMetre " +
//                "from niopdcao_" + Profiles.activeProfile + ".dbo.metre metre " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on metre_log.metre_id = metre.id " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on metre_log.log_book_id = log_book.id " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log_book.day_depot_id = day_depot.id " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//                "                 on day_depot.main_day_operation_id = main_day_operation.id " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//                "       left join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center refuel_center on main_day_operation.refuel_center_id = refuel_center.id " +
//                "where oil_tank.oil_tank_type = 'UNIT' " +
//                "  and main_day_operation.day between :startDate " +
//                "  and :finishDate " +
//                "  and metre.active = 1 " +
//                "  and refuel_center.id = :refuelCenterId " +
//                "group by oil_tank.title, " +
//                "         product.title, " +
//                "         metre.amount, " +
//                "         metre_log.fuel_type " +
//                "union " +
//                " " +
//                "select null                 dayDate, " +
//                "       oil_tank.title                                 unitName, " +
//                "       product.title                                  productName, " +
//                "       null                            fuelType, " +
//                "       0               amount, " +
//                "       0                 count, " +
//                "       null          startMetre, " +
//                "       metre.amount endMetre " +
//                "from niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank " +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre metre on oil_tank.id = metre.oil_tank_id " +
//                "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on oil_tank.product_id = product.id " +
//                "where metre.active = 1 " +
//                "  and oil_tank.refuel_center_id = 2 " +
//                "  and oil_tank.oil_tank_type = 'UNIT' " +
//                "  and oil_tank.oil_tank_status = 'ACTIVE' " +
//                "  and metre.id not in ( " +
//                "  select distinct metre.id " +
//                "  from niopdcao_" + Profiles.activeProfile + ".dbo.metre metre " +
//                "         left join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on metre_log.metre_id = metre.id " +
//                "         left join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on metre_log.log_book_id = log_book.id " +
//                "         left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log_book.day_depot_id = day_depot.id " +
//                "         left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//                "                   on day_depot.main_day_operation_id = main_day_operation.id " +
//                "         left join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//                "         left join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//                "         left join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center refuel_center " +
//                "                   on main_day_operation.refuel_center_id = refuel_center.id " +
//                "  where oil_tank.oil_tank_type = 'UNIT' " +
//                "    and main_day_operation.day between :startDate " +
//                "    and :finishDate " +
//                "    and metre.active = 1 " +
//                "    and refuel_center.id = :refuelCenterId) " +
//                "order by unitName";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(MetreDTO.class));
//    }
//
//    public List<PlatformDTO> findAllPlatforms(PlatformRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query =
//            "select " +
//                "m2.start_meter startMetre, " +
//                "m2.end_meter endMetre, " +
//                "m2.amount , " +
//                "d2.day dayDate, " +
//                "o.title platformName ," +
//                "u.title unitName" +
//                " from niopdcao_" + Profiles.activeProfile + ".dbo.transfer_platform_to_unit tptu" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log m2 on tptu.metre_log_id = m2.id" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot depot on tptu.platform_id = depot.id" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot unitDepot on tptu.unit_id = unitDepot.id" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot d2 on depot.main_day_depot_id = d2.id" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank o on depot.oil_tank_id = o.id" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank u on unitDepot.oil_tank_id = u.id" +
//                " where " +
//                " (:oilTankId is null or unitDepot.oil_tank_id =:oilTankId )" +
//                " and o.refuel_center_id= :refuelCenterId" +
//                " and d2.day>= :startDate " +
//                " and d2.day <= :finishDate";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("oilTankId", req.getOilTankId());
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(PlatformDTO.class));
//    }
//
//    @Override
//    public List<AmountReportDTO> findAllAmountReports(AmountReportRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query =
//            "select case " +
//                "         when person.personality = 'LEGAL' then person.name " +
//                "         else person.first_name + ' ' + person.last_name " +
//                "         end                       as personName, " +
//                "       product.title                  product, " +
//                "       jo.person_id                as personId, " +
//                "       sum(order_product.amount)              as amount, " +
//                "       count(distinct jo.id)       as countFuelReceipt, " +
//                "       refuel_center.persian_title as refuelCenter " +
//                "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo " +
//                "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = jo.person_id " +
//                "       inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on order_product.order_id = jo.id " +
//                "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
//                "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = jo.depot_id " +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center refuel_center on refuel_center.id = depot.refuel_center_id " +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main on main.id = jo.main_day_operation_id " +
//                "  and main.day >= :startDate " +
//                "  and main.day <= :finishDate " +
//                "  and jo.status not in ('DRAFT', 'REVOCATION') " +
//                "  and refuel_center.id = :refuelCenterId " +
//                " " +
//                "group by case " +
//                "         when person.personality = 'LEGAL' then person.name " +
//                "         else person.first_name + ' ' + person.last_name " +
//                "         end, " +
//                "         product.title, " +
//                "         refuel_center.persian_title, " +
//                "         jo.person_id;";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AmountReportDTO.class));
//    }
//
//    @Override
//    public List<Map<String, Object>> findAllTwentyFourAos(TwentyFourAoRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//     /*   String query = " " +
//            "SELECT * " +
//            "FROM ( " +
//            "         select right(niopdcaccounting_" + Profiles.activeProfile + ".dbo.CalculatePersianDate(metre_log.register_date), 2) day, " +
//            "                CONVERT(VARCHAR(2), metre_log.register_date, 108)                                hour, " +
//            "                count(0)                                                                 countFuelReceipt " +
//            "         from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo " +
//            "                  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = jo.depot_id " +
//            "                  inner join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on log_book.order_id = jo.id " +
//            "                  inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on metre_log.log_book_id = log_book.id " +
//            "             and metre_log.register_date >= :startDate " +
//            "             and jo.status = 'CONFIRM' " +
//            "             and metre_log.register_date <= :finishDate " +
//            "             and depot.refuel_center_id = :refuelCenterId " +
//            "         group by right(niopdcaccounting_" + Profiles.activeProfile + ".dbo.CalculatePersianDate(metre_log.register_date), 2), " +
//            "                  CONVERT(VARCHAR(2), metre_log.register_date, 108) " +
//            "     ) as s " +
//            "         PIVOT " +
//            "         ( " +
//            "         SUM(countFuelReceipt) " +
//            "         FOR [hour] IN ([00],[01],[02],[03],[04],[05],[06],[07],[08],[09],[10],[11],[12],[13],[14],[15],[16],[17],[18],[19],[20],[21],[22],[23]) " +
//            "         ) AS pvt ";*/
//
//        String query = "select j_order.id as orderId , metre_log.register_date as registerDate ,metre_log.amount as amount, product.title as product from " +
//            "niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            "inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
//            "inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = j_order.depot_id " +
//            "inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
//            "inner join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on log_book.order_id = j_order.id " +
//            "inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on log_book.id = metre_log.log_book_id " +
//            "where j_order.status not in ('DRAFT','REVOCATION') and " +
//            "metre_log.register_date between :startDate and :finishDate and " +
//            "depot.refuel_center_id = :refuelCenterId";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.queryForList(query, parameters);
//    }
//
//    @Override
//    public List<TwentyFourDepotDTO> findAllTwentyFourDepots(TwentyFourDepotRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(TwentyFourDepotDTO.class));
//    }
//
//    @Override
//    public List<SellReportByProductDTO> findAllSellReportByProducts(SellReportByProductRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            "  product.title as product," +
//            "  sum(order_product.amount) as sumSell," +
//            "  max(order_product.amount) as maxSell," +
//            "  count(distinct jo.id) as countFuelReceipt," +
//            "  jo.fuel_type as fuelType," +
//            "  case" +
//            "  when" +
//            "    jo.type_of_fuel_receipt = 'UNIT_TO_AIRPLANE' or jo.type_of_fuel_receipt = 'UNIT_TO_CUSTOMERS'" +
//            "    then N'میدان'" +
//            "  when" +
//            "    jo.type_of_fuel_receipt = 'TANKER_SALES' or jo.type_of_fuel_receipt = 'PIPE_LINE_SALES'" +
//            "    then N'انبار'" +
//            "  end sellType" +
//            " from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo" +
//            "  inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on order_product.order_id = jo.id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id=jo.depot_id" +
//            "  and jo.register_date >= :startDate" +
//            "  and jo.register_date <= :finishDate" +
//            "  and depot.refuel_center_id=:refuelCenterId " +
//            " group by product.title, product.id, jo.fuel_type, jo.type_of_fuel_receipt";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(SellReportByProductDTO.class));
//    }
//
//
//    @Override
//    public List<ReceiptNoDetailDTO> findAllReceiptNoDetails(ReceiptNoDetailRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            " case" +
//            " when jo.buy_group='CASH' then " +
//            " 'CA'+jo.order_no " +
//            " when jo.buy_group='CREDIT' then " +
//            " 'CR'+jo.order_no " +
//            " else jo.order_no" +
//            " end " +
//            "                            as receiptNo," +
//            "  jo.register_date                         as dateTime," +
//            "  log_book.amount                          as amount," +
//            "  o.title                                  as oilTank," +
//            "  customer.name                            as airplaneName," +
//            "  vehicle_model.title                     as airplaneModel," +
//            "  case when person.personality = 'LEGAL'" +
//            "    then person.name" +
//            "  else person.first_name + ' ' + person.last_name" +
//            "  end                                      as personName," +
//            "  jo.fuel_type                             as fuelType," +
//            "  currency.title                           as currency," +
//            "  product.title                            as productTitle," +
//            "  juser.first_name + ' ' + juser.last_name as username," +
//            "  product.code                             as productCode" +
//            " from niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot depot on log_book.day_depot_id = depot.id" +
//            "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank o on depot.oil_tank_id = o.id" +
//            "  inner join niopdcorder_" + Profiles.activeProfile + ".dbo.j_order jo on jo.id = log_book.order_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on customer.id = jo.customer_id" +
//            "  left join niopdcbase_" + Profiles.activeProfile + ".dbo.vehicle_model vehicle_model on vehicle_model.id = customer.vehicle_model_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = jo.person_id" +
//            "  inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on order_product.id = log_book.order_product_id" +
//            "  inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on currency.id = jo.currency_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id" +
//            "  inner join niopdcuaa_" + Profiles.activeProfile + ".dbo.jhi_user juser on juser.login = jo.created_by" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot1 on depot1.id=jo.depot_id" +
//            " where" +
//            "  jo.register_date >= :startDate" +
//            "  and jo.register_date <= :finishDate" +
//            "  and depot1.refuel_center_id=:refuelCenterId ";
//
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(ReceiptNoDetailDTO.class));
//    }
//
//    @Override
//    public List<AirportDTO> findAllAirports(AirportRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query = "select" +
//            "  airport.persian_title       as persianTitle," +
//            "  airport.english_title       as englishTitle," +
//            "  airport.code                as code," +
//            "  airport.global_code         as globalCode," +
//            "  region.name                 as regionName," +
//            "  country.name                as countryName," +
//            "  country.check_national_code as checkNational" +
//            " from niopdcao_" + Profiles.activeProfile + ".dbo.airport airport" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.region region on region.id = airport.region_id" +
//            "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.country country on country.id = airport.country_id";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AirportDTO.class));
//    }
//
//
//    @Override
//    public List<TotalSellGroundDTO> findAllTotalSellGrounds(TotalSellGroundRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query =
//            "select" +
//                "  o.title," +
//                "  sell_ground_fuel.amount            as amount," +
//                "  sell_ground_fuel.rate              as rate," +
//                "  sell_ground_fuel.total_price       as totalPrice," +
//                "  sell_ground_fuel.sell_to_different as sellToDifferent," +
//                "  product.title                      as productTitle," +
//                "  product.code                       as productCode" +
//                " from niopdcao_" + Profiles.activeProfile + ".dbo.sell_ground_fuel sell_ground_fuel" +
//                "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot depot on sell_ground_fuel.day_depot_id = depot.id" +
//                "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank o on depot.oil_tank_id = o.id" +
//                "  inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot mdd on mdd.id = depot.main_day_depot_id" +
//                "  inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = o.product_id" +
//                "  where mdd.day >= :startDate" +
//                "  and mdd.day <= :finishDate" +
//                "  and o.refuel_center_id=:refuelCenterId " +
//                "order by product.title ";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(TotalSellGroundDTO.class));
//    }
//
//
//    @Override
//    public List<AoMountReportDTO> findAllAoMountReports(AoMountReportRequestDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//        log.debug(req.getState().getSorts());
//
//        String query =
//            "select" +
//                "  tempOrder.maxStepAmount," +
//                "  tempOrder.totalAirport," +
//                "  tempOrder.maxStepAmount," +
//                "  tempOrder.minStepAmount," +
//                "  (select top 1 case when person.personality = 'LEGAL'" +
//                "    then person.name" +
//                "                else person.first_name + ' ' + person.last_name" +
//                "                end" +
//                "   from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order" +
//                "     inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product o on j_order.id = o.order_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person as person on person.id = j_order.person_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = j_order.depot_id" +
//                "   where o.amount = tempOrder.maxStepAmount" +
//                "         and j_order.register_date >= :startDate" +
//                "         and j_order.register_date <= :finishDate" +
//                "         and depot.refuel_center_id = :refuelCenterId" +
//                "  ) as maxStepPerson," +
//                "  (select top 1 product.title" +
//                "   from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order" +
//                "     inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product o on j_order.id = o.order_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product as product on product.id = o.product_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = j_order.depot_id" +
//                "   where o.amount = tempOrder.maxStepAmount" +
//                "  ) as maxStepProduct," +
//                "  (select top 1 case when person.personality = 'LEGAL'" +
//                "    then person.name" +
//                "                else person.first_name + ' ' + person.last_name" +
//                "                end" +
//                "   from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order" +
//                "     inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product o on j_order.id = o.order_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person as person on person.id = j_order.person_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = j_order.depot_id" +
//                "   where o.amount = tempOrder.minStepAmount" +
//                "         and j_order.register_date >= :startDate" +
//                "         and j_order.register_date <= :finishDate" +
//                "         and depot.refuel_center_id = :refuelCenterId" +
//                "  ) as minStepPerson," +
//                "  (select top 1 product.title" +
//                "   from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order" +
//                "     inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product o on j_order.id = o.order_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product as product on product.id = o.product_id" +
//                "     inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = j_order.depot_id" +
//                "   where o.amount = tempOrder.minStepAmount" +
//                "         and j_order.register_date >= :startDate" +
//                "         and j_order.register_date <= :finishDate" +
//                "         and depot.refuel_center_id = :refuelCenterId" +
//                "  ) as minStepProduct" +
//                " from (" +
//                "       select" +
//                "         sum(o.amount)                       totalAmount," +
//                "         count(distinct j_order.customer_id) totalAirport," +
//                "         max(o.amount)                       maxStepAmount," +
//                "         min(o.amount)                       minStepAmount" +
//                "       from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order" +
//                "         inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product o on j_order.id = o.order_id" +
//                "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id = j_order.depot_id" +
//                "       where j_order.register_date >= :startDate" +
//                "             and j_order.register_date <= :finishDate" +
//                "             and depot.refuel_center_id = :refuelCenterId" +
//                "     ) as tempOrder";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AoMountReportDTO.class));
//    }
//
//    @Override
//    public List<Map<String, Object>> findAlLSellProductToCompany(SellProductToCompanyDTO req) {
//        req.getState().setColumnMapper(new HashMap<String, String>() {{
//        }});
//
//        String query = "declare @product_title_col as nvarchar(max), " +
//            "@query as nvarchar(max), " +
//            "@colsWithNoNulls as nvarchar(max) , " +
//            "@startDate AS datetime2 , " +
//            "@finishDate AS datetime2 , " +
//            "@refuelCenterId AS integer ; " +
//            "select @startDate = :startDate;" +
//            "select @finishDate = :finishDate;" +
//            "select @refuelCenterId = :refuelCenterId;" +
//            " " +
//            " " +
//            "with productCurrencyQuery as ( " +
//            "    SELECT distinct case when rate_group.foreign_exchange=1 then QUOTENAME(product.title+' ' +currency.title+' '+currency_rate_group.title) else QUOTENAME(product.title+' ' +currency.title) end productCurrency " +
//            "    from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            "      inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
//            "      inner join niopdcbase_" + Profiles.activeProfile + ".dbo.sell_contract_product sell_contract_product on sell_contract_product.id = order_product.sell_contract_product_id " +
//            "      inner join niopdcrate_" + Profiles.activeProfile + ".dbo.rate_group rate_group on rate_group.id = sell_contract_product.rate_group_id " +
//            "      inner join niopdcrate_" + Profiles.activeProfile + ".dbo.product_rate product_rate on product_rate.product_id=order_product.product_id " +
//            "      inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on product_rate.currency_id=currency.id " +
//            "      inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency_rate_group currency_rate_group on sell_contract_product.currency_rate_group_id=currency_rate_group.id " +
//            "      inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on order_product.product_id=product.id " +
//            "    where j_order.order_type = 'AIRPLANE') " +
//            " " +
//            "select @product_title_col = STUFF((SELECT distinct ',' +  productCurrency " +
//            "                                   from productCurrencyQuery " +
//            " " +
//            "                                   FOR XML PATH (''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 1, '') , " +
//            "  @colsWithNoNulls = STUFF((SELECT distinct  ',ISNULL(' + productCurrency + ', ''0'') ' + productCurrency " +
//            "                            from productCurrencyQuery " +
//            "                            FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'),1,1,'') " +
//            " " +
//            "set @query = 'with pivotQuery as  ( " +
//            "    select j_order.person_id,  coalesce(sum(order_product.amount),0) as [amount], " +
//            "                case when rate_group.foreign_exchange=1 then product.title+'' '' +currency.title+'' ''+currency_rate_group.title " +
//            "                     else product.title+'' '' +currency.title end productCurrency " +
//            "from " +
//            "     niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            " " +
//            "       inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.sell_contract_product sell_contract_product on sell_contract_product.id = order_product.sell_contract_product_id " +
//            "       inner join niopdcrate_" + Profiles.activeProfile + ".dbo.rate_group rate_group on rate_group.id = sell_contract_product.rate_group_id " +
//            "       inner join niopdcrate_" + Profiles.activeProfile + ".dbo.product_rate product_rate on product_rate.product_id=order_product.product_id " +
//            "       inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on product_rate.currency_id=currency.id " +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot depot on depot.id=j_order.depot_id " +
//            "       inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency_rate_group currency_rate_group on sell_contract_product.currency_rate_group_id=currency_rate_group.id " +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on order_product.product_id=product.id " +
//            " " +
//            "where order_product.amount >=0 and j_order.order_type = ''AIRPLANE'' and j_order.fuel_type=''RE_FUEL'' " +
//            "and j_order.register_date between ''' + convert(varchar(19),@startDate) + ''' and ''' + convert(varchar(19),@finishDate)  + ''' and depot.refuel_center_id=''' + convert(varchar(19),@refuelCenterId)  + ''' " +
//            "group by j_order.person_id,product.title, currency.title, currency_rate_group.title, rate_group.foreign_exchange), " +
//            " pvtResult as( " +
//            "             select person_id,  '+@colsWithNoNulls+' " +
//            "from pivotQuery " +
//            "         pivot ( " +
//            "           sum(pivotQuery.amount) " +
//            "         for pivotQuery.productCurrency in (' + @product_title_col + ') " +
//            "         )as pvt),personOrderCountQuery as ( " +
//            "             select case " +
//            "                    when person.personality = ''LEGAL'' then person.name " +
//            "                    when person.personality = ''NATURAL'' then person.first_name + '' '' + person.last_name " +
//            "                    end as [نام_شرکت],count(j_order.id) [مقدار],j_order.person_id person_id " +
//            "             from  niopdcbase_" + Profiles.activeProfile + ".dbo.person person " +
//            "             inner join niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order on j_order.person_id=person.id " +
//            "             group by person.personality,person.name,person.first_name,person.last_name,j_order.person_id) " +
//            "             select [نام_شرکت],[مقدار],pvtResult.* from pvtResult inner join personOrderCountQuery on pvtResult.person_id=personOrderCountQuery.person_id " +
//            "' " +
//            "exec (@query)";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//
//        return jdbcTemplate.queryForList(query, parameters);
//    }
//
//    @Override
//    public List<WayBillDTO> getWayBillReportForPrint(Long wayBillId) {
//        String query = "select drive_security.national_id                              national_code, " +
//            "       product.id                                                product_id, " +
//            "       product.title                                           product_name, " +
//            "       0                                                       ship_type, " +
//            "       ''                                          srouce_region_title, " +
//            "       ''                                 source_zone_title, " +
//            "       ''                                                  forwarding_des, " +
//            "       way_bill.register_date                                  way_bill_date, " +
//            "       source_depot.code                                       senderOwId, " +
//            "       source_depot.title                                      senderName, " +
//            "       car.title                                               plaque, " +
//            "       way_bill.id                                             wayBillId, " +
//            "       target_depot.title                                      destName, " +
//            "       target_depot.code                                       destOWId, " +
//            "       ''                                                      customer_name, " +
//            "       ''                                                      customer_code, " +
//            "       case " +
//            "           when person.name is not null then person.name " +
//            "           else person.first_name + ' ' + person.last_name end contractorName, " +
//            "       person.code                                             contractCode, " +
//            "       ''                                                      permitId, " +
//            "       ''                                                      routeSeqNo, " +
//            "       route.description                                       routeDescription, " +
//            "       driver.first_name + ' ' + driver.last_name              driverName, " +
//            "       ''                                                      serialBankPermissionNo, " +
//            "       person.address, " +
//            "       ''                                              shipCode, " +
//            "       route.rate                                              rate, " +
//            "       car_info.third_party_insurance_expire_date              thirdPartyInsuranceExprDate, " +
//            "       car_info.third_party_insurance_no                       thirdPartyInsuranceNo, " +
//            "       way_bill.created_by                                     j_user, " +
//            "       ''                                                      customerNationalIdentityNo, " +
//            "       driver.driving_license_number                           drivingLicenseNo, " +
//            "       way_bill.register_date                                  exitTime, " +
//            "       way_bill.product_temperature                            sendDegree, " +
//            "       way_bill.nature_amount                                  sentQuantity, " +
//            "       way_bill.sixty_amount                                   sendQuantity60, " +
//            "       way_bill.special_weight                                 sentSpecialWeight, " +
//            "       way_bill.weight                                         weight, " +
//            "       ''                                                      qCUser " +
//            " " +
//            " " +
//            "from niopdcao_" + Profiles.activeProfile + ".dbo.way_bill " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.driver on driver.id = way_bill.driver_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.drive_security on driver.id = drive_security.driver_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot source_depot on source_depot.id = way_bill.source_depot_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.depot target_depot on target_depot.id = way_bill.target_depot_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.car car on car.id = way_bill.car_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.car_info car_info on car.id = car_info.car_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on day_depot.id = way_bill.day_depot_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = way_bill.person_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.route route on route.id = way_bill.route_id " +
//            " " +
//            " " +
//            "where way_bill.way_bill_type = 'SEND' and way_bill.id=:wayBillId ";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("wayBillId", wayBillId);
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(WayBillDTO.class));
//    }
//
//    @Override
//    public List<SealUseDTO> getSealUseByWayBillId(Long wayBillId) {
//        String query = "select " +
//            "       seal_prefix + cast(seal_number as nvarchar) sealNumber, " +
//            "       amount " +
//            "from niopdcao_" + Profiles.activeProfile + ".dbo.seal_use " +
//            "where way_bill_id=:wayBillId ";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("wayBillId", wayBillId);
//
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(SealUseDTO.class));
//    }
//
//    @Override
//    public List<TotalPlatformDTO> getTotalPlatformReport(TotalPlatformRequestDTO req) {
//        String query = "with platformSell as (select day_depot.id        as dayDepotId, " +
//            "                             sum(j_order.amount) as amount " +
//            "                      from niopdcao_" + Profiles.activeProfile + ".dbo.way_bill way_bill " +
//            "                             inner join niopdcorder_" + Profiles.activeProfile + ".dbo.j_order on j_order.id = way_bill.order_id " +
//            "                             inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on way_bill.day_depot_id = day_depot.id " +
//            "                             inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                                        on day_depot.main_day_depot_id = main_day_depot.id " +
//            "                             inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "                      where oil_tank.refuel_center_id = :refuelCenterId " +
//            "                        and main_day_depot.day between :startDate " +
//            "                        and :finishDate " +
//            "                        and ( " +
//            "                          :oilTankId is null " +
//            "                          or (oil_tank.id = :oilTankId) " +
//            "                        ) " +
//            "                      group by day_depot.id), " +
//            "     platformToUnit as ( " +
//            "       select day_depot.id                                 as dayDepotId, " +
//            "              sum(transfer_platform_to_unit.nature_amount) as amount " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.transfer_platform_to_unit transfer_platform_to_unit " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on transfer_platform_to_unit.platform_id = day_depot.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                         on day_depot.main_day_depot_id = main_day_depot.id " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and main_day_depot.day between :startDate " +
//            "         and :finishDate " +
//            "         and ( " +
//            "           :oilTankId is null " +
//            "           or (oil_tank.id = :oilTankId) " +
//            "         ) " +
//            "       group by day_depot.id " +
//            "     ) " +
//            "select oil_tank.title                                                              as oilTankTitle, " +
//            "       product.title                                                               as product, " +
//            "       isnull(sum(platformSell.amount), 0)                                         as platformSell, " +
//            "       isnull(sum(platformToUnit.amount), 0)                                       as unitToPlatform, " +
//            "       isnull(sum(platformSell.amount), 0) + isnull(sum(platformToUnit.amount), 0) as totalAmount " +
//            "from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on oil_tank.product_id = product.id " +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot on day_depot.main_day_depot_id = main_day_depot.id " +
//            "       left join platformSell on platformSell.dayDepotId = day_depot.id " +
//            "       left join platformToUnit on platformToUnit.dayDepotId = day_depot.id " +
//            " " +
//            "where oil_tank.oil_tank_type = 'PLATFORM' " +
//            "  and oil_tank.refuel_center_id = :refuelCenterId " +
//            "  and main_day_depot.day between :startDate " +
//            "  and :finishDate " +
//            "  and ( " +
//            "    :oilTankId is null " +
//            "    or (oil_tank.id = :oilTankId) " +
//            "  ) " +
//            "group by oil_tank.title, product.title";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("oilTankId", req.getOilTankId());
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(TotalPlatformDTO.class));
//
//    }
//
//    @Override
//    public List<AoInvoiceDTO> getInvoice(ZonedDateTime startTime, ZonedDateTime endTime) {
//        String query = "select person.name                                                                                         as personCode, " +
//            "       100                                                                                                    airport_code, " +
//            "       count(j_order.order_no)                                                                             as receiptCount, " +
//            "       isnull(sum(case when currency.is_national_currency = 1 then j_order.price end), " +
//            "              0)                                                                                              totalPriceRial, " +
//            "       isnull(sum(case when currency.is_national_currency <> 1 then j_order.price end), " +
//            "              0)                                                                                              totalPriceDollar, " +
//            "       0                                                                                                      totalPriceEuro, " +
//            "       isnull(sum(case when cost.cost_related = 'COMPLICATION' then order_cost.rate end), " +
//            "              0)                                                                                           as complication, " +
//            "       isnull(sum(case when cost.cost_related = 'TAX' then order_cost.price end), 0)                       as tax, " +
//            "       isnull(sum(case when cost.cost_related not in ('COMPLICATION', 'TAX') then order_cost.rate end), 0) as transport, " +
//            "       isnull(sum(j_order.amount), 0)                                                                      as amount, " +
//            "       target_airport_country.check_national_code                                                             nationalAirport " +
//            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = j_order.person_id " +
//            "         inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on currency.id = j_order.currency_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.airport airport on j_order.target_airport = airport.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.country target_airport_country on airport.country_id = target_airport_country.id " +
//            "         left join niopdcorder_" + Profiles.activeProfile + ".dbo.order_cost order_cost on j_order.id = order_cost.order_id " +
//            "         left join niopdcrate_" + Profiles.activeProfile + ".dbo.cost cost on cost.id = order_cost.cost_id " +
//            "where j_order.order_type = 'AIRPLANE' " +
//            "  and j_order.status != 'DRAFT' and j_order.register_date between :startTime and :endTime " +
//            "group by person.name, target_airport_country.check_national_code, cost.cost_related ";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startTime", new Timestamp(startTime.toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("endTime", new Timestamp(endTime.toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AoInvoiceDTO.class));
//    }
//
//    @Override
//    public List<MetreSheetAoDTO> getMetreSheetForAirport(MetreSheetAoRequestDTO req) {
//        String query = "select oil_tank.title                                 as unitTitle, " +
//            "       case " +
//            "           when j_order.buy_group = 'CREDIT' then 'CR' + j_order.order_no " +
//            "           else 'CA' + j_order.order_no end           as orderNumber, " +
//            "       left(cast(metre_log.register_date as time), 5) as createTime, " +
//            "       customer.name                                  as customerName, " +
//            "       product.code                                   as productCode, " +
//            "       product.title                                  as productTitle, " +
//            "       j_order.fuel_type                              as fuel_type, " +
//            "       case when j_order.fuel_type='RE_FUEL' then metre_log.amount" +
//            "       else (metre_log.amount * -1)    end                           as amount, " +
//            "       metre_log.end_meter                            as endMetre, " +
//            "       metre_log.start_meter                          as startMetre, " +
//            "       isnull(metre_log.difference_end_metre, 0)      as differenceEndMetre, " +
//            "       jhi_user.first_name + ' ' + jhi_user.last_name                             as username, " +
//            "       cast(metre_log.register_date as date)          as createDay " +
//            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            "         inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                    on main_day_operation.id = j_order.main_day_operation_id " +
//            "          inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on currency.id = j_order.currency_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on log_book.order_product_id = order_product.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on metre_log.log_book_id = log_book.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on customer.id = j_order.customer_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log_book.day_depot_id = day_depot.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "         inner join niopdcuaa_" + Profiles.activeProfile + ".dbo.jhi_user jhi_user on jhi_user.login= j_order.created_by " +
//            "where j_order.order_type = 'AIRPLANE' " +
//            "  and ( " +
//            "    main_day_operation.day between :startDate " +
//            "        and :finishDate " +
//            "    ) " +
//            "  and main_day_operation.refuel_center_id = :refuelCenterId " +
//            "  and (j_order.status not in ('DRAFT', 'REVOCATION')) " +
//            "  and ( " +
//            "        :username is null " +
//            "        or j_order.created_by = :username " +
//            "    ) " +
//            "  and ( " +
//            "        :oilTankId is null " +
//            "        or oil_tank.id = :oilTankId " +
//            "    ) " +
//            "  and ( " +
//            "        :receiptNo is null " +
//            "        or j_order.order_no = :receiptNo " +
//            "    ) " +
//            "  and ( " +
//            "        :productId is null " +
//            "        or product.id = :productId " +
//            "    ) " +
//            "  and ( " +
//            "        :amountGreaterThan is null " +
//            "        or metre_log.amount >= :amountGreaterThan " +
//            "    ) " +
//            "  and ( " +
//            "        :amountSmallerThan is null " +
//            "        or metre_log.amount <= :amountSmallerThan " +
//            "    ) " +
//            "  and ( " +
//            "        :fuelType is null " +
//            "        or j_order.fuel_type = :fuelType " +
//            "    ) " +
//            "  and ( " +
//            "        :customerTitle is null " +
//            "        or customer.name = :customerTitle " +
//            "    ) " +
//            "   and (" +
//            "       :fromReceiptNumber is null " +
//            "       or j_order.order_no >= :fromReceiptNumber " +
//            "   ) " +
//            "   and (" +
//            "       :toReceiptNumber is null " +
//            "       or j_order.order_no <= :toReceiptNumber " +
//            "   ) " +
//            "   and (" +
//            "       :currencyType is null " +
//            "       or ((:currencyType = 'NATIONAL' and currency.is_national_currency = 1) or (:currencyType='FOREIGN' and currency.is_national_currency = 0)) " +
//            "   ) " +
//            "   and (" +
//            "       :buyGroup is null " +
//            "       or j_order.buy_group = :buyGroup " +
//            "   ) " +
//            "union " +
//            "select from_oil_tank.title                            as unitTitle, " +
//            "       '-------'                                      as orderNumber, " +
//            "       left(cast(metre_log.register_date as time), 5) as createTime, " +
//            "       to_oil_tank.title                              as customerName, " +
//            "       product.code                                   as productCode, " +
//            "       product.title                                  as productTitle, " +
//            "       transfer_type.title                            as fuel_type, " +
//            "       metre_log.amount                               as amount, " +
//            "       metre_log.end_meter                            as endMetre, " +
//            "       metre_log.start_meter                          as startMetre, " +
//            "       isnull(metre_log.difference_end_metre, 0)      as differenceEndMetre, " +
//            "       metre_log.created_by                           as username, " +
//            "       cast(metre_log.register_date as date)          as createDay " +
//            "from niopdcao_" + Profiles.activeProfile + ".dbo.transfer " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot from_day_depot on transfer.from_day_depot_id = from_day_depot.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank from_oil_tank on from_day_depot.oil_tank_id = from_oil_tank.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                    on from_day_depot.main_day_operation_id = main_day_operation.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on transfer.metre_log_id = metre_log.id " +
//            "         left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot to_day_depot on to_day_depot.id = transfer.to_day_depot_id " +
//            "         left join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank to_oil_tank on to_day_depot.oil_tank_id = to_oil_tank.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = from_oil_tank.product_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.transfer_type on transfer.transfer_type_id = transfer_type.id " +
//            "where from_oil_tank.oil_tank_type = 'UNIT' " +
//            "  and ( " +
//            "    main_day_operation.day between :startDate " +
//            "        and :finishDate " +
//            "    ) " +
//            "  and main_day_operation.refuel_center_id = :refuelCenterId " +
//            "  and ( " +
//            "        :username is null " +
//            "        or metre_log.created_by = :username " +
//            "    ) " +
//            "  and ( " +
//            "        :oilTankId is null " +
//            "        or from_oil_tank.id = :oilTankId " +
//            "    ) " +
//            "  and ( " +
//            "        :productId is null " +
//            "        or product.id = :productId " +
//            "    ) " +
//            "  and ( " +
//            "        :amountGreaterThan is null " +
//            "        or metre_log.amount >= :amountGreaterThan " +
//            "    ) " +
//            "  and ( " +
//            "        :amountSmallerThan is null " +
//            "        or metre_log.amount <= :amountSmallerThan " +
//            "    ) " +
//            "  and ( " +
//            "        :customerTitle is null " +
//            "        or to_oil_tank.title = :customerTitle " +
//            "    ) " +
//            "  and :fromReceiptNumber is null " +
//            "  and :toReceiptNumber is null " +
//            "  and :currencyType is null " +
//            "  and :buyGroup is null " +
//            "order by unitTitle, createTime ";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("username", req.getUsername());
//        parameters.addValue("oilTankId", req.getOilTankId());
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//        parameters.addValue("receiptNo", req.getReceiptNo());
//        parameters.addValue("productId", req.getProductId());
//        parameters.addValue("amountGreaterThan", req.getAmountGreaterThan());
//        parameters.addValue("amountSmallerThan", req.getAmountSmallerThan());
//        parameters.addValue("fuelType", req.getFuelType());
//        parameters.addValue("customerTitle", req.getCustomerTitle());
//        parameters.addValue("fromReceiptNumber", req.getFromReceiptNumber());
//        parameters.addValue("toReceiptNumber", req.getToReceiptNumber());
//        parameters.addValue("buyGroup", req.getBuyGroup());
//        parameters.addValue("currencyType", req.getCurrencyType());
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(MetreSheetAoDTO.class));
//
//    }
//
//    @Override
//    public List<LastOilTankStatusDTO> getLastOilTankStatus(LastOilTankStatusRequestDTO req) {
//        String query = "select oil_tank.title as oilTankTitle, " +
//            "       case " +
//            "         when oil_tank.oil_tank_status = 'ACTIVE' then N'فعال' " +
//            "         else " +
//            "           case " +
//            "             when oil_tank.oil_tank_status = 'STAGNANT' then N'راکد' " +
//            "             else N'تحت تعمیر' " +
//            "             end " +
//            "         end as oilTankStatus, " +
//            "       oil_tank.last_modified_date lastDate " +
//            "from niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank " +
//            "where oil_tank.oil_tank_type = 'UNIT' and oil_tank.refuel_center_id = :refuelCenterId " +
//            "and oil_tank.last_modified_date between :startDate and :finishDate ";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(LastOilTankStatusDTO.class));
//    }
//
//    @Override
//    public List<DepotInventoryDTO> getAllDepotInventory(DepotInventoryRequestDTO req) {
//        String query;
//        if (req.getReportType().equals("by-product")) {
//            query = "select case when product.id = 27800 then N'بنزین جت JP4' else N'نفت جت ATK' end as oilTankTitle,\n" +
//                "       product.title                                                          as productTitle,\n" +
//                "       cast(start_measurement_oil_tank.register_date as date)                 as registerDate,\n" +
//                "       right(start_measurement_oil_tank.register_date, 6)                     as registerTime,\n" +
//                "       sum(start_measurement_oil_tank.amount_deep)                            as amountDeep,\n" +
//                "       max(start_measurement_oil_tank.product_temperature)                    as productTemperature,\n" +
//                "       sum(start_measurement_oil_tank.amount)                                 as natureAmount,\n" +
//                "       sum(start_measurement_oil_tank.sixty_amount)                           as sixtyAmount,\n" +
//                "       max(isnull(way_bill.received_product_temperature, 0))                  as receivedProductTemperature,\n" +
//                "       sum(isnull(way_bill.received_nature_amount, 0))                        as receivedNatureAmount,\n" +
//                "       sum(isnull(way_bill.received_sixty_amount, 0))                         as receivedSixtyAmount,\n" +
//                "       sum(isnull(transfer.nature_amount, 0))                                 as sendNatureAmount,\n" +
//                "       sum(isnull(transfer.sixty_amount, 0))                                  as sendsixtyAmount,\n" +
//                "       max(isnull(transfer.product_temperature, 0))                           as sendProductTemperature,\n" +
//                "       sum(day_depot.received_sixty_system_amount)                            as receivedSixtySystemAmount,\n" +
//                "       sum(day_depot.nature_addition)                                         as natureAddition,\n" +
//                "       sum(day_depot.sixty_addition)                                          as sixtyAddition,\n" +
//                "       sum(day_depot.nature_deductible)                                       as natureDeductible,\n" +
//                "       sum(day_depot.sixty_sixty_deductible)                                  as sixtyDeductible\n" +
//                "from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot\n" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot on day_depot.main_day_depot_id = main_day_depot.id\n" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.measurement_oil_tank start_measurement_oil_tank\n" +
//                "                  on day_depot.start_measurement_oil_tank_id = start_measurement_oil_tank.id\n" +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id\n" +
//                "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id\n" +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.way_bill way_bill on day_depot.id = way_bill.day_depot_id\n" +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.transfer transfer on transfer.from_day_depot_id = day_depot.id\n" +
//                "where oil_tank.refuel_center_id = :refuelCenterId\n" +
//                "  and main_day_depot.day between :startDate\n" +
//                "  and :finishDate\n" +
//                "  and product.id = :productId\n" +
//                "group by cast(start_measurement_oil_tank.register_date as date),\n" +
//                "         right(start_measurement_oil_tank.register_date, 6),\n" +
//                "         product.title,\n" +
//                "         product.id\n" +
//                "order by cast(start_measurement_oil_tank.register_date as date)";
//        } else {
//            query = "select oil_tank.title                                         as oilTankTitle, " +
//                "       product.title                                          as productTitle, " +
//                "       cast(start_measurement_oil_tank.register_date as date) as registerDate, " +
//                "       right(start_measurement_oil_tank.register_date, 6)     as registerTime, " +
//                "       sum(start_measurement_oil_tank.amount_deep)            as amountDeep, " +
//                "       max(start_measurement_oil_tank.product_temperature)    as productTemperature, " +
//                "       sum(start_measurement_oil_tank.amount)                 as natureAmount, " +
//                "       sum(start_measurement_oil_tank.sixty_amount)           as sixtyAmount, " +
//                "       max(isnull(way_bill.received_product_temperature, 0))  as receivedProductTemperature, " +
//                "       sum(isnull(way_bill.received_nature_amount, 0))        as receivedNatureAmount, " +
//                "       sum(isnull(way_bill.received_sixty_amount, 0))         as receivedSixtyAmount, " +
//                "       sum(isnull(transfer.nature_amount, 0))                 as sendNatureAmount, " +
//                "       sum(isnull(transfer.sixty_amount, 0))                  as sendsixtyAmount, " +
//                "       max(isnull(transfer.product_temperature, 0))           as sendProductTemperature, " +
//                "       sum(day_depot.received_sixty_system_amount)            as receivedSixtySystemAmount, " +
//                "       sum(day_depot.nature_addition)                         as natureAddition, " +
//                "       sum(day_depot.sixty_addition)                          as sixtyAddition, " +
//                "       sum(day_depot.nature_deductible)                       as natureDeductible, " +
//                "       sum(day_depot.sixty_sixty_deductible)                  as sixtyDeductible " +
//                "from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot " +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot on day_depot.main_day_depot_id = main_day_depot.id " +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.measurement_oil_tank start_measurement_oil_tank " +
//                "                  on day_depot.start_measurement_oil_tank_id = start_measurement_oil_tank.id " +
//                "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//                "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.way_bill way_bill on day_depot.id = way_bill.day_depot_id " +
//                "       left join niopdcao_" + Profiles.activeProfile + ".dbo.transfer transfer on transfer.from_day_depot_id = day_depot.id " +
//                "where oil_tank.id = :oilTankId " +
//                "  and " +
//                "  oil_tank.refuel_center_id = :refuelCenterId " +
//                "  and main_day_depot.day between :startDate " +
//                "  and :finishDate " +
//                "group by oil_tank.title, " +
//                "         cast(start_measurement_oil_tank.register_date as date), " +
//                "         right(start_measurement_oil_tank.register_date, 6), product.title " +
//                "order by cast(start_measurement_oil_tank.register_date as date) ";
//        }
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//        if (req.getReportType().equals("by-product")) {
//            parameters.addValue("productId", req.getProductId());
//        } else {
//            parameters.addValue("oilTankId", req.getOilTankId());
//        }
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(DepotInventoryDTO.class));
//    }
//
//    @Override
//    public List<MetreSheetAoDTO> getMetreSheetForDepot(MetreSheetAoRequestDTO req) {
//        String query = "select oil_tank.title                                 as unitTitle, " +
//            "       case " +
//            "           when j_order.buy_group = 'CREDIT' then 'CR' + j_order.order_no " +
//            "           else 'CA' + j_order.order_no end           as orderNumber, " +
//            "       left(cast(metre_log.register_date as time), 5) as createTime, " +
//            "       customer.name                                  as customerName, " +
//            "       product.code                                   as productCode, " +
//            "       product.title                                  as productTitle, " +
//            "       j_order.fuel_type                              as fuel_type, " +
//            "       case when j_order.fuel_type='RE_FUEL' then metre_log.amount" +
//            "       else (metre_log.amount * -1)    end                           as amount, " +
//            "       metre_log.end_meter                            as endMetre, " +
//            "       metre_log.start_meter                          as startMetre, " +
//            "       isnull(metre_log.difference_end_metre, 0)      as differenceEndMetre, " +
//            "       j_order.created_by                             as username, " +
//            "       cast(metre_log.register_date as date)          as createDay " +
//            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            "         inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                    on main_day_depot.id = j_order.main_day_depot_id " +
//            "          inner join niopdcrate_" + Profiles.activeProfile + ".dbo.currency currency on currency.id = j_order.currency_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.way_bill way_bill on way_bill.order_id = j_order.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on metre_log.way_bill_id = way_bill.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on customer.id = j_order.customer_id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on way_bill.day_depot_id = day_depot.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "where j_order.order_type = 'REFUEL_CENTER' " +
//            "  and ( " +
//            "    main_day_depot.day between :startDate " +
//            "        and :finishDate " +
//            "    ) " +
//            "  and main_day_depot.refuel_center_id = :refuelCenterId " +
//            "  and (j_order.status not in ('DRAFT', 'REVOCATION')) " +
//            "  and ( " +
//            "        :username is null " +
//            "        or j_order.created_by = :username " +
//            "    ) " +
//            "  and ( " +
//            "        :oilTankId is null " +
//            "        or oil_tank.id = :oilTankId " +
//            "    ) " +
//            "  and ( " +
//            "        :receiptNo is null " +
//            "        or j_order.order_no = :receiptNo " +
//            "    ) " +
//            "  and ( " +
//            "        :productId is null " +
//            "        or product.id = :productId " +
//            "    ) " +
//            "  and ( " +
//            "        :amountGreaterThan is null " +
//            "        or metre_log.amount >= :amountGreaterThan " +
//            "    ) " +
//            "  and ( " +
//            "        :amountSmallerThan is null " +
//            "        or metre_log.amount <= :amountSmallerThan " +
//            "    ) " +
//            "  and ( " +
//            "        :fuelType is null " +
//            "        or j_order.fuel_type = :fuelType " +
//            "    ) " +
//            "  and ( " +
//            "        :customerTitle is null " +
//            "        or customer.name = :customerTitle " +
//            "    ) " +
//            "   and (" +
//            "       :fromReceiptNumber is null " +
//            "       or j_order.order_no >= :fromReceiptNumber " +
//            "   ) " +
//            "   and (" +
//            "       :toReceiptNumber is null " +
//            "       or j_order.order_no <= :toReceiptNumber " +
//            "   ) " +
//            "   and (" +
//            "       :currencyType is null " +
//            "       or ((:currencyType = 'NATIONAL' and currency.is_national_currency = 1) or (:currencyType='FOREIGN' and currency.is_national_currency = 0)) " +
//            "   ) " +
//            "   and (" +
//            "       :buyGroup is null " +
//            "       or j_order.buy_group = :buyGroup " +
//            "   ) " +
//            "union " +
//            "select from_oil_tank.title                            as unitTitle, " +
//            "       '-------'                                      as orderNumber, " +
//            "       left(cast(metre_log.register_date as time), 5) as createTime, " +
//            "       to_oil_tank.title                              as customerName, " +
//            "       product.code                                   as productCode, " +
//            "       product.title                                  as productTitle, " +
//            "       N'انتقال سکو به واحد' as fuel_type, " +
//            "       metre_log.amount                               as amount, " +
//            "       metre_log.end_meter                            as endMetre, " +
//            "       metre_log.start_meter                          as startMetre, " +
//            "       isnull(metre_log.difference_end_metre, 0)      as differenceEndMetre, " +
//            "       metre_log.created_by                           as username, " +
//            "       cast(metre_log.register_date as date)          as createDay " +
//            "from niopdcao_" + Profiles.activeProfile + ".dbo.transfer_platform_to_unit " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot from_day_depot on transfer_platform_to_unit.platform_id = from_day_depot.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank from_oil_tank on from_day_depot.oil_tank_id = from_oil_tank.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                    on from_day_depot.main_day_depot_id = main_day_depot.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on transfer_platform_to_unit.metre_log_id = metre_log.id " +
//            "         left join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot to_day_depot on to_day_depot.id = transfer_platform_to_unit.unit_id " +
//            "         left join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank to_oil_tank on to_day_depot.oil_tank_id = to_oil_tank.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = from_oil_tank.product_id " +
//            "where from_oil_tank.oil_tank_type = 'PLATFORM' " +
//            "  and ( " +
//            "    main_day_depot.day between :startDate " +
//            "        and :finishDate " +
//            "    ) " +
//            "  and main_day_depot.refuel_center_id = :refuelCenterId " +
//            "  and ( " +
//            "        :username is null " +
//            "        or metre_log.created_by = :username " +
//            "    ) " +
//            "  and ( " +
//            "        :oilTankId is null " +
//            "        or from_oil_tank.id = :oilTankId " +
//            "    ) " +
//            "  and ( " +
//            "        :productId is null " +
//            "        or product.id = :productId " +
//            "    ) " +
//            "  and ( " +
//            "        :amountGreaterThan is null " +
//            "        or metre_log.amount >= :amountGreaterThan " +
//            "    ) " +
//            "  and ( " +
//            "        :amountSmallerThan is null " +
//            "        or metre_log.amount <= :amountSmallerThan " +
//            "    ) " +
//            "  and ( " +
//            "        :customerTitle is null " +
//            "        or to_oil_tank.title = :customerTitle " +
//            "    ) " +
//            "  and :fromReceiptNumber is null " +
//            "  and :toReceiptNumber is null " +
//            "  and :currencyType is null " +
//            "  and :buyGroup is null " +
//            "order by unitTitle, createTime ";
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//        parameters.addValue("username", req.getUsername());
//        parameters.addValue("oilTankId", req.getOilTankId());
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//        parameters.addValue("receiptNo", req.getReceiptNo());
//        parameters.addValue("productId", req.getProductId());
//        parameters.addValue("amountGreaterThan", req.getAmountGreaterThan());
//        parameters.addValue("amountSmallerThan", req.getAmountSmallerThan());
//        parameters.addValue("fuelType", req.getFuelType());
//        parameters.addValue("customerTitle", req.getCustomerTitle());
//        parameters.addValue("fromReceiptNumber", req.getFromReceiptNumber());
//        parameters.addValue("toReceiptNumber", req.getToReceiptNumber());
//        parameters.addValue("buyGroup", req.getBuyGroup());
//        parameters.addValue("currencyType", req.getCurrencyType());
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(MetreSheetAoDTO.class));
//
//    }
//
//    public List<NinePageDetailDTO> getNinePage(NinePageRequestDTO req, String code) {
//        String query = null;
//        if (code.equals("085"))
//            query = get85();
//        else if (code.equals("341"))
//            query = get341();
//        else if (code.equals("411"))
//            query = get411();
//        else if (code.equals("421"))
//            query = get421();
//        else if (code.equals("451"))
//            query = get451();
//        else if (code.equals("511"))
//            query = get511();
//        else if (code.equals("551"))
//            query = get551();
//        else if (code.equals("631"))
//            query = get631();
//        else if (code.equals("641"))
//            query = get641();
//        else if (code.equals("714"))
//            query = get714();
//        else if (code.equals("724"))
//            query = get724();
//        else if (code.equals("811"))
//            query = get811();
//        else if (code.equals("821"))
//            query = get821();
//        else if (code.equals("985"))
//            query = get985();
//
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        if (!code.equals("985"))
//            parameters.addValue("startDate", new Timestamp(req.getStartDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//
//        if (!code.equals("085")) {
//            if (code.equals("985")) {
//                ZonedDateTime finishDate = req.getFinishDate();
//                finishDate = finishDate.minusHours(finishDate.getHour())
//                    .minusMinutes(finishDate.getMinute())
//                    .minusSeconds(finishDate.getSecond())
//                    .minusNanos(finishDate.getNano());
//                parameters.addValue("finishDate", new Timestamp(finishDate.toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//            } else {
//                parameters.addValue("finishDate", new Timestamp(req.getFinishDate().toInstant().getEpochSecond() * 1000L), Types.TIMESTAMP);
//            }
//        }
//
//        parameters.addValue("refuelCenterId", req.getRefuelCenterId());
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(NinePageDetailDTO.class));
//    }
//
//    @Override
//    public List<AoPrintInfoDTO> getInfoForAirplanePrint(Long orderId) {
//        String query = "select j_order.order_no                               as orderNo,\n" +
//            "       j_order.register_date                          as date,\n" +
//            "       refuel_center.persian_title                    as refuelCenter,\n" +
//            "       customer.name                                  as customer,\n" +
//            "       person.name                                    as person,\n" +
//            "       j_order.description                            as description,\n" +
//            "       j_order.fuel_type                              as fuelType,\n" +
//            "       j_order.flight_conditions                      as flightType,\n" +
//            "       j_order.buy_group                              as buyType,\n" +
//            "       jhi_user.first_name + ' ' + jhi_user.last_name as username,\n" +
//            "       vehicle_model.title                            as airplaneType\n" +
//            "\n" +
//            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order\n" +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation\n" +
//            "                  on j_order.main_day_operation_id = main_day_operation.id\n" +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.refuel_center refuel_center on main_day_operation.refuel_center_id = refuel_center.id\n" +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.customer customer on j_order.customer_id = customer.id\n" +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.person person on person.id = j_order.person_id\n" +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.vehicle_model vehicle_model on customer.vehicle_model_id = vehicle_model.id\n" +
//            "       inner join niopdcuaa_" + Profiles.activeProfile + ".dbo.jhi_user jhi_user on jhi_user.login = j_order.created_by\n" +
//            "\n" +
//            "\n" +
//            "where j_order.id = :orderId";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        parameters.addValue("orderId", orderId);
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AoPrintInfoDTO.class));
//    }
//
//    @Override
//    public List<AoPrintUnitsDTO> getUnitsForAirplanePrint(Long orderId) {
//        String query = "select oil_tank.title                        as unitTitle,\n" +
//            "       product.title                         as productTitle,\n" +
//            "       metre_log.amount                      as amount,\n" +
//            "       left(cast(metre_log.register_date as time),5) as startHour,\n" +
//            "       left(cast(metre_log.finish_date as time),5)   as finishHour\n" +
//            "from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order\n" +
//            "       inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id\n" +
//            "       inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on order_product.product_id = product.id\n" +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on log_book.order_product_id = order_product.id\n" +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.metre_log metre_log on log_book.id = metre_log.log_book_id\n" +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on log_book.day_depot_id = day_depot.id\n" +
//            "       inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id\n" +
//            "where j_order.id = :orderId";
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//
//        parameters.addValue("orderId", orderId);
//        return jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper(AoPrintUnitsDTO.class));
//    }
//
//    private String get85() {
//        return "with dayDepotContainerWith as ( " +
//            "  select max(day_depot_container.start_count) as count, " +
//            "         product.title                        as productTitle, " +
//            "         product.code                         as productCode " +
//            "  from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot_container day_depot_container " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank_container oil_tank_container " +
//            "                    on day_depot_container.oil_tank_container_id = oil_tank_container.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on oil_tank_container.product_id = product.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                    on day_depot_container.main_day_depot_id = main_day_depot.id " +
//            " " +
//            "  where oil_tank_container.refuel_center_id = :refuelCenterId " +
//            "    and main_day_depot.day = :startDate " +
//            "  group by product.title, product.code " +
//            "), " +
//            "     dayDepotWith as ( " +
//            "       select product.title                          as productTitle, " +
//            "              product.code                           as productCode, " +
//            "              sum(start_measurement_oil_tank.amount) as amount " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.measurement_oil_tank start_measurement_oil_tank " +
//            "                         on day_depot.start_measurement_oil_tank_id = start_measurement_oil_tank.id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                        on day_depot.main_day_depot_id = main_day_depot.id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                        on day_depot.main_day_operation_id = main_day_operation.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and main_day_depot.day = :startDate " +
//            "       group by product.title,product.code " +
//            "     ) " +
//            " " +
//            "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            " " +
//            "from ( " +
//            "       select case " +
//            "                when dayDepotWith.productTitle is not null then " +
//            "                  dayDepotWith.productCode " +
//            "                else dayDepotContainerWith.productCode end as productCode, " +
//            "              case " +
//            "                when dayDepotWith.productTitle is not null then " +
//            "                  dayDepotWith.amount " +
//            "                else dayDepotContainerWith.count end       as amount " +
//            "       from niopdcbase_" + Profiles.activeProfile + ".dbo.product " +
//            "              left join dayDepotWith dayDepotWith on dayDepotWith.productCode = product.code " +
//            "              left join dayDepotContainerWith dayDepotContainerWith on dayDepotContainerWith.productCode = product.code " +
//            " " +
//            "       where (dayDepotWith.productTitle is null and dayDepotContainerWith.productTitle is not null) " +
//            "          or (dayDepotContainerWith.productTitle is null and dayDepotWith.productTitle is not null) " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get341() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(way_bill.received_nature_amount) as amount, " +
//            "              product.code                         as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.way_bill way_bill " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on way_bill.day_depot_id = day_depot.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                         on day_depot.main_day_depot_id = main_day_depot.id " +
//            "       where way_bill.way_bill_type = 'RECEIVE' " +
//            "         and oil_tank.refuel_center_id = :refuelCenterId and  " +
//            "             main_day_depot.day between :startDate and :finishDate " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get411() {
//        return "select " +
//            " " +
//            "piv.[662001] as tm, " +
//            "            piv.[034001] as gasoil, " +
//            "            piv.[005401] as petrol, " +
//            "            piv.[401079] as tanker, " +
//            "            piv.[660501] as fuel100, " +
//            "            piv.[420901] as contaminated, " +
//            "            piv.[410001] as jp4, " +
//            "            piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(order_product.amount) as amount, " +
//            "              product.code              as productCode " +
//            "       from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                        on main_day_operation.id = j_order.main_day_operation_id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot on main_day_depot.id = j_order.main_day_depot_id " +
//            "              inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
//            " " +
//            "       where ((main_day_depot.id is not null and main_day_depot.refuel_center_id = :refuelCenterId and " +
//            "               main_day_depot.day between :startDate and :finishDate) " +
//            "         or (main_day_operation.id is not null and main_day_operation.refuel_center_id = :refuelCenterId and " +
//            "             main_day_operation.day between :startDate and :finishDate)) " +
//            "         and j_order.status = 'CONFIRM' " +
//            "         and " +
//            "         j_order.buy_group = 'CASH' " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get421() {
//        return "select " +
//            " " +
//            "piv.[662001] as tm, " +
//            "            piv.[034001] as gasoil, " +
//            "            piv.[005401] as petrol, " +
//            "            piv.[401079] as tanker, " +
//            "            piv.[660501] as fuel100, " +
//            "            piv.[420901] as contaminated, " +
//            "            piv.[410001] as jp4, " +
//            "            piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(order_product.amount) as amount, " +
//            "              product.code              as productCode " +
//            "       from niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                        on main_day_operation.id = j_order.main_day_operation_id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot on main_day_depot.id = j_order.main_day_depot_id " +
//            "              inner join niopdcorder_" + Profiles.activeProfile + ".dbo.order_product order_product on j_order.id = order_product.order_id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = order_product.product_id " +
//            " " +
//            "       where ((main_day_depot.id is not null and main_day_depot.refuel_center_id = :refuelCenterId and " +
//            "               main_day_depot.day between :startDate and :finishDate) " +
//            "         or (main_day_operation.id is not null and main_day_operation.refuel_center_id = :refuelCenterId and " +
//            "             main_day_operation.day between :startDate and :finishDate)) " +
//            "         and j_order.status = 'CONFIRM' " +
//            "         and " +
//            "         j_order.buy_group = 'CREDIT' " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv; ";
//    }
//
//    private String get451() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(log_book.amount) as amount, " +
//            "              product.code         as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                         on day_depot.main_day_operation_id = main_day_operation.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.log_book log_book on day_depot.id = log_book.day_depot_id " +
//            "              inner join niopdcorder_" + Profiles.activeProfile + ".dbo.j_order j_order on j_order.id = log_book.order_id " +
//            " " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and j_order.fuel_type = 'DE_FUEL' " +
//            "         and j_order.status = 'CONFIRM' " +
//            "       and main_day_operation.day between :startDate and :finishDate " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get511() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(sell_ground_fuel.amount) as amount, " +
//            "              product.code                 as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                         on day_depot.main_day_depot_id = main_day_depot.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.sell_ground_fuel sell_ground_fuel " +
//            "                         on day_depot.id = sell_ground_fuel.day_depot_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            " " +
//            "       where main_day_depot.refuel_center_id = :refuelCenterId and main_day_depot.day between :startDate and :finishDate " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get551() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(way_bill.nature_amount) as amount, " +
//            "              product.code                as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.way_bill " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot on way_bill.day_depot_id = day_depot.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                         on day_depot.main_day_depot_id = main_day_depot.id " +
//            " " +
//            "       where main_day_depot.refuel_center_id = :refuelCenterId and main_day_depot.day between :startDate and :finishDate " +
//            "         and way_bill.way_bill_type = 'SEND' " +
//            "         and way_bill.order_id is null " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get631() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(transfer.nature_amount) as amount, " +
//            "              product.code                as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                         on day_depot.main_day_depot_id = main_day_depot.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.transfer transfer on day_depot.id = transfer.to_day_depot_id " +
//            " " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and main_day_depot.day between :startDate and :finishDate " +
//            "         and oil_tank.oil_tank_type = 'CONTAMINATED' " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get641() {
//        return "select  " +
//            "piv.[662001] as tm, " +
//            "  piv.[034001] as gasoil, " +
//            "  piv.[005401] as petrol, " +
//            "  piv.[401079] as tanker, " +
//            "  piv.[660501] as fuel100, " +
//            "  piv.[420901] as contaminated, " +
//            "  piv.[410001] as jp4, " +
//            "  piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(transfer.nature_amount) as amount, " +
//            "              product.code                as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                         on day_depot.main_day_depot_id = main_day_depot.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.transfer transfer on day_depot.id = transfer.from_day_depot_id " +
//            " " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId and main_day_depot.day between :startDate and :finishDate " +
//            "  group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get714() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(day_depot.nature_addition) as amount, " +
//            "              product.code                   as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                        on day_depot.main_day_depot_id = main_day_depot.id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                        on day_depot.main_day_operation_id = main_day_operation.id " +
//            " " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and ((main_day_depot.id is not null and main_day_depot.day between :startDate and :finishDate) or " +
//            "              (main_day_operation.id is not null and main_day_operation.day between :startDate and :finishDate)) " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get724() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(day_depot.nature_deductible) as amount, " +
//            "              product.code                     as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                        on day_depot.main_day_depot_id = main_day_depot.id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                        on day_depot.main_day_operation_id = main_day_operation.id " +
//            " " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and " +
//            "         ((main_day_operation.id is not null and main_day_operation.day between :startDate and :finishDate) or " +
//            "          (main_day_depot.id is not null and main_day_depot.day between :startDate and :finishDate)) " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//
//    private String get811() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(transfer.nature_amount) as amount, " +
//            "              product.code                as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                         on day_depot.main_day_operation_id = main_day_operation.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.transfer transfer on day_depot.id = transfer.from_day_depot_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.transfer_type transfer_type on transfer.transfer_type_id = transfer_type.id " +
//            " " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and main_day_operation.day between :startDate and :finishDate " +
//            "         and oil_tank.oil_tank_type = 'UNIT' " +
//            "         and ( " +
//            "           transfer_type.transfer_to_himself = 1 or (transfer_type.transfer_to = 'SERVICE_TANK') " +
//            "         ) " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv; ";
//    }
//
//    private String get821() {
//        return "select piv.[662001] as tm, " +
//            "       piv.[034001] as gasoil, " +
//            "       piv.[005401] as petrol, " +
//            "       piv.[401079] as tanker, " +
//            "       piv.[660501] as fuel100, " +
//            "       piv.[420901] as contaminated, " +
//            "       piv.[410001] as jp4, " +
//            "       piv.[420001] as atk " +
//            "from ( " +
//            "       select sum(transfer.nature_amount) as amount, " +
//            "              product.code                as productCode " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                         on day_depot.main_day_operation_id = main_day_operation.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.transfer transfer on day_depot.id = transfer.from_day_depot_id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.transfer_type transfer_type on transfer.transfer_type_id = transfer_type.id " +
//            " " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and main_day_operation.day between :startDate and :finishDate " +
//            "         and oil_tank.oil_tank_type = 'UNIT' " +
//            "         and ( " +
//            "           transfer_type.transfer_to_himself = 1 or (transfer_type.transfer_to = 'SERVICE_TANK') " +
//            "         ) " +
//            "       group by product.code " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv; ";
//    }
//
//    private String get985() {
//        return "with dayDepotContainerWith as ( " +
//            "  select max(day_depot_container.end_count) as count, " +
//            "         product.title                      as productTitle, " +
//            "         product.code                       as productCode " +
//            "  from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot_container day_depot_container " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank_container oil_tank_container " +
//            "                    on day_depot_container.oil_tank_container_id = oil_tank_container.id " +
//            "         inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on oil_tank_container.product_id = product.id " +
//            "         inner join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                    on day_depot_container.main_day_depot_id = main_day_depot.id " +
//            " " +
//            "  where oil_tank_container.refuel_center_id = :refuelCenterId " +
//            "    and main_day_depot.day = :finishDate " +
//            "  group by product.title, product.code " +
//            "), " +
//            "     dayDepotWith as ( " +
//            "       select product.title                        as productTitle, " +
//            "              product.code                         as productCode, " +
//            "              sum(end_measurement_oil_tank.amount) as amount " +
//            "       from niopdcao_" + Profiles.activeProfile + ".dbo.day_depot day_depot " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.oil_tank oil_tank on day_depot.oil_tank_id = oil_tank.id " +
//            "              inner join niopdcao_" + Profiles.activeProfile + ".dbo.measurement_oil_tank end_measurement_oil_tank " +
//            "                         on day_depot.end_measurement_oil_tank_id = end_measurement_oil_tank.id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_depot main_day_depot " +
//            "                        on day_depot.main_day_depot_id = main_day_depot.id " +
//            "              left join niopdcao_" + Profiles.activeProfile + ".dbo.main_day_operation main_day_operation " +
//            "                        on day_depot.main_day_operation_id = main_day_operation.id " +
//            "              inner join niopdcbase_" + Profiles.activeProfile + ".dbo.product product on product.id = oil_tank.product_id " +
//            "       where oil_tank.refuel_center_id = :refuelCenterId " +
//            "         and " +
//            "         (( " +
//            "            main_day_depot.id is not null and main_day_depot.day = :finishDate " +
//            "            ) or ( " +
//            "              main_day_operation.id is not null and main_day_operation.day = :finishDate " +
//            "            )) " +
//            "       group by product.title,product.code " +
//            "     ) " +
//            " " +
//            "select  " +
//            " " +
//            "piv.[662001] as tm, " +
//            "  piv.[034001] as gasoil, " +
//            "  piv.[005401] as petrol, " +
//            "  piv.[401079] as tanker, " +
//            "  piv.[660501] as fuel100, " +
//            "  piv.[420901] as contaminated, " +
//            "  piv.[410001] as jp4, " +
//            "  piv.[420001] as atk " +
//            "from ( " +
//            "       select case " +
//            "                when dayDepotWith.productTitle is not null then " +
//            "                  dayDepotWith.productCode " +
//            "                else dayDepotContainerWith.productCode end as productCode, " +
//            "              case " +
//            "                when dayDepotWith.productTitle is not null then " +
//            "                  dayDepotWith.amount " +
//            "                else dayDepotContainerWith.count end       as amount " +
//            "       from niopdcbase_" + Profiles.activeProfile + ".dbo.product " +
//            "              left join dayDepotWith dayDepotWith on dayDepotWith.productCode = product.code " +
//            "              left join dayDepotContainerWith dayDepotContainerWith on dayDepotContainerWith.productCode = product.code " +
//            " " +
//            "       where (dayDepotWith.productTitle is null and dayDepotContainerWith.productTitle is not null) " +
//            "          or (dayDepotContainerWith.productTitle is null and dayDepotWith.productTitle is not null) " +
//            "     ) sc " +
//            "       pivot ( " +
//            "       SUM(amount) " +
//            "       for productCode in ([005401],[034001],[381101],[401079],[410001],[420001],[420901],[660501],[662001]) " +
//            "       ) piv;";
//    }
//}
