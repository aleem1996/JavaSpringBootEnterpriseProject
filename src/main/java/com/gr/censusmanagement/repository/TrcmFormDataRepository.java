package com.gr.censusmanagement.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gr.censusmanagement.entity.TrcmFormData;
import com.gr.censusmanagement.external.model.DashboardDto;

public interface TrcmFormDataRepository extends JpaRepository<TrcmFormData, String> {

	@Query(value = "SELECT tfd.*,"
			+ " case"
			+ " when (:columnName) = 'firstName' then tfd.firstName"
			+ " when (:columnName) = 'lastName' then tfd.lastName"
			+ " when (:columnName) = 'fullName' then tfd.fullName"
			+ " when (:columnName) = 'coverageStartDate' then tfd.coverageStartDate"
			+ " when (:columnName) = 'coverageEndDate' then tfd.coverageEndDate"
			+ " when (:columnName) = 'dob' then STR_TO_DATE(tfd.dob, '%c/%e/%Y %H:%i')"
			+ " when (:columnName) = 'email' then tfd.email"
			+ " when (:columnName) = 'membershipType' then tfd.membershipType"
			+ " when (:columnName) = 'city' then ad.city"
			+ " when (:columnName) = 'lineOne' then ad.lineOne"
			+ " when (:columnName) = 'createdOn' then tfd.createdOn"
			+ " when (:columnName) = 'modifiedBy' then ifnull(tfd.modifiedBy, tfd.createdBy)"
			+ " else cfd.value"
			+ " end result"
			+ " FROM TrcmFormData tfd"
			+ " INNER JOIN TrcmForm tf ON tfd.trcmFormId = tf.id"
//			+ " LEFT JOIN DefaultFieldsConfig df ON df.trcmFormId = tf.id AND df.attribute = 'coverageStartDate' AND df.isHiddenInForm = 1"
			+ " LEFT JOIN CustomField cf ON cf.trcmFormId = tf.id AND cf.attribute = (:attributeName) AND cf.sortable = 1"
			+ " LEFT JOIN CustomFieldData cfd ON cfd.customFieldId = cf.id And cfd.trcmFormDataId = tfd.id"
			+ " LEFT JOIN Address ad ON ad.customFieldDataId = cfd.id"
			+ " LEFT JOIN LATERAL ("
			+ " SELECT cfd.id, cfd.trcmFormDataId"
			+ " FROM CustomField cf"
			+ " INNER JOIN CustomFieldData cfd ON cfd.customFieldId = cf.id AND cf.trcmFormId = tf.id AND cfd.trcmFormDataId = tfd.id AND cf.searchable = 1"
			+ " WHERE cfd.value IS NOT NULL AND LOWER(cfd.value) LIKE LOWER(CONCAT('%', :filter, '%'))"
			+ " UNION"
			+ " SELECT ad.id, cfd.trcmFormDataId"
			+ " FROM Address ad"
			+ " INNER JOIN CustomFieldData cfd ON cfd.id = ad.customFieldDataId AND cfd.trcmFormDataId = tfd.id"
			+ " INNER JOIN CustomField cf ON cf.id = cfd.customFieldId AND cf.searchable = 1"
			+ " WHERE LOWER(ad.city) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(ad.state) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(ad.country) LIKE LOWER(CONCAT('%', :filter, '%'))"
			+ " LIMIT 1"
			+ " ) cfs ON cfs.trcmFormDataId = tfd.id"
			+ " WHERE tf.accountId = (:accountId)"
			+ " AND (tfd.deleted IS NULL OR tfd.deleted <> 1)"
			+ " AND ((:membershipType) IS NULL OR (:membershipType) = '' OR tfd.membershipType = (:membershipType))"
			+ " AND ((:filter) IS NULL OR (:filter) = '' OR LOWER(tfd.firstName) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(tfd.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(tfd.fullName) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(tfd.email) LIKE LOWER(CONCAT('%', :filter, '%')) OR cfs.id IS NOT NULL)"
			+ " AND ("
			+ " ((:filterType) = 'creationDates' AND (((:coverageStartDate) IS NULL AND (:coverageEndDate) IS NULL) OR"
//			+ " (df.id IS NOT NULL AND (((:coverageStartDate) IS NULL AND (:coverageEndDate) IS NULL) OR"
			+ " ((:coverageStartDate) IS NULL AND tfd.createdOn <= DATE_FORMAT((:coverageEndDate), '%Y-%m-%d 23:59:59'))"
			+ " OR ((:coverageEndDate) IS NULL AND tfd.createdOn >= DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00'))"
			+ " OR (tfd.createdOn BETWEEN DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND DATE_FORMAT((:coverageEndDate),"
			+ " '%Y-%m-%d 23:59:59'))))"
			+ " OR"
			+ " ((:filterType) = 'coverageDates' AND (((:coverageStartDate) IS NULL OR tfd.coverageStartDate BETWEEN DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND DATE_FORMAT((:coverageEndDate), '%Y-%m-%d 23:59:59') OR ((:coverageEndDate) IS NULL OR tfd.coverageEndDate BETWEEN DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND DATE_FORMAT((:coverageEndDate), '%Y-%m-%d 23:59:59')))"
			+ " OR (tfd.coverageStartDate < DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND (tfd.coverageEndDate IS NULL OR tfd.coverageEndDate > DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00')))))"
			+ " )"
			+ " ORDER BY"
			+ " CASE WHEN (:sortOrder) = 'asc' THEN result END asc,"
			+ " CASE WHEN (:sortOrder) = 'desc' THEN result END desc",
			countQuery = "SELECT COUNT(DISTINCT tfd.id) from TrcmFormData tfd INNER JOIN TrcmForm tf ON tfd.trcmFormId = tf.id "
//					+ "LEFT JOIN DefaultFieldsConfig df ON df.trcmFormId = tf.id AND df.attribute = 'coverageStartDate' AND df.isHiddenInForm = 1 "
			+ "LEFT JOIN LATERAL (SELECT cfd.id, cfd.trcmFormDataId FROM CustomField cf INNER JOIN CustomFieldData cfd ON cfd.customFieldId = cf.id AND cf.trcmFormId = tf.id AND cfd.trcmFormDataId = tfd.id AND cf.searchable = 1 WHERE cfd.value IS NOT NULL AND LOWER(cfd.value) LIKE LOWER(CONCAT('%', :filter, '%')) UNION SELECT ad.id, cfd.trcmFormDataId FROM Address ad INNER JOIN CustomFieldData cfd ON cfd.id = ad.customFieldDataId AND cfd.trcmFormDataId = tfd.id INNER JOIN CustomField cf ON cf.id = cfd.customFieldId AND cf.searchable = 1 WHERE LOWER(ad.city) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(ad.state) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(ad.country) LIKE LOWER(CONCAT('%', :filter, '%')) LIMIT 1) cfs ON cfs.trcmFormDataId = tfd.id where tf.accountId = (:accountId) AND (tfd.deleted IS NULL OR tfd.deleted <> 1) AND ((:membershipType) IS NULL OR (:membershipType) = '' OR tfd.membershipType = (:membershipType)) AND ((:filter) IS NULL OR (:filter) = '' OR LOWER(tfd.firstName) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(tfd.lastName) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(tfd.fullName) LIKE LOWER(CONCAT('%', :filter, '%')) OR LOWER(tfd.email) LIKE LOWER(CONCAT('%', :filter, '%')) OR cfs.id IS NOT NULL)"
			+ " AND ("
			+ " ((:filterType) = 'creationDates' AND (((:coverageStartDate) IS NULL AND (:coverageEndDate) IS NULL) OR ((:coverageStartDate) IS NULL AND tfd.createdOn <= DATE_FORMAT((:coverageEndDate), '%Y-%m-%d 23:59:59')) OR ((:coverageEndDate) IS NULL AND tfd.createdOn >= DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00')) OR (tfd.createdOn BETWEEN DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND DATE_FORMAT((:coverageEndDate), '%Y-%m-%d 23:59:59'))))"
			+ " OR"
			+ " ((:filterType) = 'coverageDates' AND (((:coverageStartDate) IS NULL OR tfd.coverageStartDate BETWEEN DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND DATE_FORMAT((:coverageEndDate), '%Y-%m-%d 23:59:59') OR ((:coverageEndDate) IS NULL OR tfd.coverageEndDate BETWEEN DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND DATE_FORMAT((:coverageEndDate), '%Y-%m-%d 23:59:59')))"
			+ " OR (tfd.coverageStartDate < DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00') AND (tfd.coverageEndDate IS NULL OR tfd.coverageEndDate > DATE_FORMAT((:coverageStartDate), '%Y-%m-%d 00:00:00')))))"
			+ " )",
			nativeQuery = true)
	public Page<TrcmFormData> findByAccountId(@Param("accountId") String accountId, @Param("columnName") String columnName, @Param("attributeName") String attributeName, @Param("sortOrder") String sortOrder, @Param("filter") String filter, @Param("coverageStartDate") Date coverageStartDate, @Param("coverageEndDate") Date coverageEndDate, @Param("membershipType") String membershipType, @Param("filterType") String filterType, Pageable pageable);

	@Query(value = "SELECT q.startDate as startDate, q.endDate as endDate, QUARTER(q.startDate) as quarter, YEAR(q.startDate) as year, "
			+ "IFNULL(ABS(SUM(DATEDIFF(GREATEST(tfd.coverageStartDate, q.startDate), LEAST(IFNULL(tfd.coverageEndDate, CASE WHEN tfd.coverageStartDate > CURDATE() THEN q.endDate ELSE CURDATE() END), q.endDate) + INTERVAL 1 DAY))), 0) as days, "
			+ "IFNULL(COUNT(distinct tfd.id), 0) as count, "
			+ "CASE WHEN ((:accountType) = 'TRCM_TRAVEL_DAYS' OR (:accountType) = 'TRCM') THEN 1 ELSE 0 END isCoverageStartDateHidden "
			+ "FROM ( "
			+ "SELECT q.startDate, q.endDate, q.endDateTime "
			+ "FROM ( "
			+ "SELECT "
			+ "(DATE_SUB(MAKEDATE(YEAR(CURDATE()), 1) + INTERVAL QUARTER(CURDATE()) QUARTER, INTERVAL 1 YEAR) + INTERVAL Num Quarter) startDate, "
			+ "((DATE_SUB(MAKEDATE(YEAR(CURDATE()), 1) + INTERVAL QUARTER(CURDATE()) QUARTER, INTERVAL 1 YEAR) + INTERVAL Num + 1 Quarter) - INTERVAL 1 DAY) endDate, "
			+ "((DATE_SUB(MAKEDATE(YEAR(CURDATE()), 1) + INTERVAL QUARTER(CURDATE()) QUARTER, INTERVAL 1 YEAR) + INTERVAL Num + 1 Quarter) - INTERVAL 1 SECOND) endDateTime "
			+ "FROM ( "
			+ "SELECT 0 as Num UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 "
			+ ") q "
			+ ") q "
			+ ") q "
//			+ "CROSS JOIN ( "
//			+ "SELECT tf.id, df.id defaultFieldsConfigId "
//			+ "FROM censusmanagement.TrcmForm tf  "
////			+ "LEFT JOIN censusmanagement.DefaultFieldsConfig df ON df.trcmFormId = tf.id AND df.attribute = 'coverageStartDate' AND df.isHiddenInForm = 1 "
//			+ "WHERE tf.isActive = 1 AND tf.accountId = (:accountId) "
//			+ ") tfdf "
			+ "LEFT JOIN ( "
			+ "SELECT tfd.id, tfd.coverageStartDate, tfd.coverageEndDate, tfd.createdOn "
			+ "FROM censusmanagement.TrcmFormData as tfd "
			+ "INNER JOIN censusmanagement.TrcmForm tf ON tf.id = tfd.trcmFormId "
			+ "WHERE tf.accountId = (:accountId) "
			+ "AND (tfd.deleted IS NULL OR tfd.deleted <> 1)"
			+ ") tfd "
			+ "ON (((:accountType) = 'TRCM_SUBSCRIPTION' OR (:accountType) = 'Named') AND ((tfd.coverageStartDate between q.startDate and q.endDateTime) or (tfd.coverageStartDate < q.startDate and (tfd.coverageEndDate is null or tfd.coverageEndDate >= q.startDate)))) "
			+ "OR (((:accountType) = 'TRCM_TRAVEL_DAYS' OR (:accountType) = 'TRCM') AND (tfd.createdOn between q.startDate and q.endDateTime)) "
			+ "GROUP BY q.startDate, q.endDate, QUARTER(q.startDate), YEAR(q.startDate) "
			+ "ORDER BY year DESC, quarter DESC;", nativeQuery = true)
	public List<DashboardDto> getDashboardDto(@Param("accountId") String accountId, @Param("accountType") String accountType);

	@Query(value = "SELECT tfd.* "
			+ "FROM TrcmFormData tfd "
			+ "INNER JOIN TrcmForm tf ON tfd.trcmFormId = tf.id " 
			+ "INNER JOIN CustomField cf ON cf.trcmFormId = tf.id AND cf.attribute = 'travelerId' "
			+ "INNER JOIN CustomFieldData cfd ON cfd.customFieldId = cf.id And cfd.trcmFormDataId = tfd.id "
			+ "where cfd.value = (:baseMapId) "
			+ "AND ((:excludedTrcmFormDataId) IS NULL OR tfd.id <> (:excludedTrcmFormDataId))", nativeQuery = true)
	public Optional<List<TrcmFormData>> findByBaseMapId(String baseMapId, String excludedTrcmFormDataId);
	
	@Query(value = "SELECT tfd.* "
			+ "FROM TrcmFormData tfd "
			+ "INNER JOIN TrcmForm tf ON tfd.trcmFormId = tf.id " 
			+ "where accountId = ( select id from Account where name = 'BaseMap' ) " 
			+ "AND email = (:email)", nativeQuery = true)
	public Optional<List<TrcmFormData>> findDataForBaseMapByEmail(String email);
	
	@Query(value = "SELECT tfd.* "
			+ "FROM TrcmFormData tfd "
			+ "INNER JOIN TrcmForm tf ON tfd.trcmFormId = tf.id " 
			+ "INNER JOIN CustomField cf ON cf.trcmFormId = tf.id AND cf.attribute = 'globalRescueId' "
			+ "INNER JOIN CustomFieldData cfd ON cfd.customFieldId = cf.id And cfd.trcmFormDataId = tfd.id "
			+ "where cfd.value = (:globalRescueId) "
			+ "AND ((:excludedTrcmFormDataId) IS NULL OR tfd.id <> (:excludedTrcmFormDataId))", nativeQuery = true)
	public Optional<List<TrcmFormData>> findByGlobalRescueId(String globalRescueId, String excludedTrcmFormDataId);

	@Query(value= "SELECT MAX(cfd.strValue) AS membershipNumber "
			+ "FROM TrcmFormData tfd "
			+ "INNER JOIN TrcmForm tf "
			+ "ON tfd.trcmFormId = tf.id "
			+ "INNER JOIN CustomField cf "
			+ "ON cf.trcmFormId = tf.id AND cf.attribute = 'theWorldMembershipNumber'"
			+ "INNER JOIN CustomFieldData cfd "
			+ "ON cfd.customFieldId = cf.id "
			+ "AND cfd.trcmFormDataId = tfd.id "
			+ "INNER JOIN Account ac "
			+ "ON ac.id = tf.accountId; ", nativeQuery=true)
//			+ "WHERE ac.sourceAccountId = (:accountId);", nativeQuery= true)
	public String getTheWorldMembershipNumberLastGenerated();
	
	
	@Query(value = "SELECT tfd.* "
			+ "FROM TrcmFormData tfd "
			+ "where tfd.id IN :trcmFormDataIds", nativeQuery = true)
	public Optional<List<TrcmFormData>> findTrcmFormDataRecordsById(@Param("trcmFormDataIds") List<String> trcmFormDataIds);
	
}