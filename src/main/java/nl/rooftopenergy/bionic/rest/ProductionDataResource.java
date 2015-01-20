package nl.rooftopenergy.bionic.rest;

import nl.rooftopenergy.bionic.dao.company.CompanyDao;
import nl.rooftopenergy.bionic.dao.rtfbox.RtfBoxDao;
import nl.rooftopenergy.bionic.dao.rtfboxdata.RtfBoxDataDao;
import nl.rooftopenergy.bionic.entity.Company;
import nl.rooftopenergy.bionic.entity.RtfBox;
import nl.rooftopenergy.bionic.entity.RtfBoxData;
import nl.rooftopenergy.bionic.rest.util.PrincipalInformation;
import nl.rooftopenergy.bionic.transfer.GraphDataTransfer;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.*;

/**
 * Provides REST API to get information about production of energy by
 * solar panels. The company-owner of panels should have identifier number <code>id</code>.
 * It is possible to get information for given time interval. Also it is possible to get
 * information about consumption during whole solar panel's working period.
 *
 *
 * Created by Alexander Iakovenko.
 * 11/27/14.
 */
@RestController
@Path("production")
public class ProductionDataResource {
    private static final int TWENTY_FOUR_HOURS = 24;
    private static final int HOUR = 3600000;
    private static final int MONTHS = 12;

    @Inject
    private RtfBoxDataDao rtfBoxDataDao;

    @Inject
    private RtfBoxDao rtfBoxDao;

    @Inject
    private CompanyDao companyDao;

    @Inject
    private PrincipalInformation principalInformation;

    @Inject
    private ComparingDataResource comparingDataResource;

    /**
     * Gets total number of production whole working period.
     * @return total number of producing energy.
     */
    @POST
    @Path("total_production")
    @Produces(MediaType.APPLICATION_JSON)
    public Integer showTotalProduction(){
        Integer paramId = principalInformation.getCompany().getCompanyId();

        RtfBox rtfBox = companyDao.find(paramId).getRtfBox();

        Integer result = rtfBoxDataDao.findTotalProduction(rtfBox);
        return result;
    }

    /**
     * Gets list notes that describe production during current day.
     * @param dateStart the date like {@value 'yyyy-mm-dd hh:MM:ss'} when period is started.
     * @param dateEnd the date like {@value 'yyyy-mm-dd hh:MM:ss'} when period should be finished.
     * @return list notes describing production.
     */
    @POST
    @Path("production_period")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GraphDataTransfer> showProduction(@FormParam("dateStart") String dateStart,
                                                  @FormParam("dateEnd") String dateEnd){
        Integer paramId = principalInformation.getCompany().getCompanyId();
        Date paramDateStart = new Timestamp(Long.parseLong(dateStart));
        Date paramDateEnd = new Timestamp(Long.parseLong(dateEnd));

        RtfBox rtfBox = companyDao.find(paramId).getRtfBox();

        List<RtfBoxData> dataList = rtfBoxDataDao.findByPeriod(rtfBox, paramDateStart, paramDateEnd);
        List<GraphDataTransfer> resultList = new ArrayList<GraphDataTransfer>();
        GraphDataTransfer graphData;
        for (int i = 1; i < dataList.size(); i++){
            int firstProduction = dataList.get(i-1).getProduction();
            int secondProduction = dataList.get(i).getProduction();
            Integer value = secondProduction - firstProduction;
            Date date = dataList.get(i).getDate();
            graphData = new GraphDataTransfer(date, value);
            resultList.add(graphData);
        }
        return resultList;
    }

    /**
     * Gets list notes that describe production during current day.
     * There are values of production per hour.
     *
     * @param thoseDate the date (expressed in milliseconds) points on day.
     * @return list notes describing production.
     */
    @POST
    @Path("daily")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GraphDataTransfer> dailyProduction(@FormParam("date") String thoseDate) {

        String companyName = principalInformation.getCompany().getCompanyName();

        return  comparingDataResource.dailyProduction(companyName,thoseDate);
    }

    /**
     * Gets list notes that describe production during current month.
     * There are values of production per day.
     *
     * @param thoseDate the date (expressed in milliseconds) points on month.
     * @return list notes describing production.
     */
    @POST
    @Path("monthly")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GraphDataTransfer> monthlyProduction(@FormParam("date") String thoseDate) {



        long longDate = Long.parseLong(thoseDate);
        RtfBox box = principalInformation.getCompany().getRtfBox();
        DateTime thisDate = new DateTime(longDate);

        String month = thisDate.getYear() + "-" + thisDate.getMonthOfYear() + "-";
        int daysInMonth = getDaysInMonth(thisDate);

        List<GraphDataTransfer> resultList = new ArrayList<GraphDataTransfer>();
        for (int i = 1; i <= daysInMonth; i++){
            Timestamp startDay = Timestamp.valueOf(month + i + " 00:00:00");
            Timestamp finishDay = Timestamp.valueOf(month + i + " 23:59:59");
            Integer value = rtfBoxDataDao.findTotalProductionByPeriod(box, startDay, finishDay);
            if (value == null) {
                value = 0;
            }
            resultList.add(new GraphDataTransfer(finishDay, value));
        }

        Date dateBefore = new Date(resultList.get(0).getDate().getTime() - TWENTY_FOUR_HOURS*HOUR);
        resultList = transformToDifferences(box, resultList, dateBefore);

        return resultList;
    }

    /**
     * Gets list notes that describe production during current year.
     * There are values of production per month.
     *
     * @param thoseDate the date (expressed in milliseconds) points on year.
     * @return list notes describing production.
     */
    @POST
    @Path("yearly")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GraphDataTransfer> yearlyProduction(@FormParam("date") String thoseDate) {



        long longDate = Long.parseLong(thoseDate);
        RtfBox box = principalInformation.getCompany().getRtfBox();
        DateTime thisDate = new DateTime(longDate);

        String year = thisDate.getYear() + "-";


        List<GraphDataTransfer> resultList = new ArrayList<GraphDataTransfer>();
        for (int i = 1; i <= MONTHS; i++){

            Timestamp startMonth = Timestamp.valueOf(year + i + "-01 00:00:00");
            int lastDay = getDaysInMonth(new DateTime(startMonth));
            Timestamp finishMonth = Timestamp.valueOf(year + i + "-" + lastDay + " 23:59:59");
            Integer value = rtfBoxDataDao.findTotalProductionByPeriod(box, startMonth, finishMonth);
            if (value == null) {
                value = 0;
            }
            resultList.add(new GraphDataTransfer(finishMonth, value));
        }

        Calendar thisYear = Calendar.getInstance();
        thisYear.setTime(resultList.get(0).getDate());
        thisYear.add(Calendar.YEAR, -1);
        Date dateBefore = thisYear.getTime();
        resultList = transformToDifferences(box, resultList, dateBefore);

        return resultList;
    }

    /**
     * Gets list notes that describe production for all working period.
     * There are values of production per month.
     *
     * @param tillDate the date (expressed in milliseconds) points on date.
     * @return list notes describing production.
     */
    @POST
    @Path("totally")
    @Produces(MediaType.APPLICATION_JSON)
    public List<GraphDataTransfer> totallyProduction(@FormParam("date") String tillDate) {



        long longDate = Long.parseLong(tillDate);
        RtfBox box = principalInformation.getCompany().getRtfBox();
        RtfBoxData boxData = rtfBoxDataDao.findFirstNote(box);

        Calendar firstDate = Calendar.getInstance();
        firstDate.setTime(boxData.getDate());

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTime(new Date(longDate));

        List<GraphDataTransfer> resultList = new ArrayList<GraphDataTransfer>();
        boolean isAvailable = true;
        while (isAvailable) {
            DateTime date = new DateTime(firstDate);
            String year = date.getYear() + "-" + date.getMonthOfYear();
            Timestamp startMonth = Timestamp.valueOf(year + "-01 00:00:00");
            int lastDay = getDaysInMonth(new DateTime(startMonth));
            Timestamp finishMonth;
            if (firstDate.compareTo(lastDate) > 0) {
                finishMonth = new Timestamp(lastDate.getTimeInMillis());
                isAvailable = false;
            } else {
                finishMonth = Timestamp.valueOf(year + "-" + lastDay + " 23:59:59");
            }
            Integer value = rtfBoxDataDao.findTotalProductionByPeriod(box, startMonth, finishMonth);
            if (value == null) {
                value = 0;
            }
            resultList.add(new GraphDataTransfer(finishMonth, value));
            firstDate.add(Calendar.MONTH, 1);
        }

        Calendar thisYear = Calendar.getInstance();
        thisYear.setTime(resultList.get(0).getDate());
        thisYear.add(Calendar.YEAR, -1);
        Date dateBefore = thisYear.getTime();
        resultList = transformToDifferences(box, resultList, dateBefore);

        return resultList;
    }

    /**
     * Gets the value of energy has produced this day.
     *
     * @return total daily production.
     */
    @POST
    @Path("thisDayTotal")
    @Produces(MediaType.APPLICATION_JSON)
    public Integer thisDayTotalProduction() {
        DateTime nowDate = new DateTime(System.currentTimeMillis());
        int year = nowDate.getYear();
        int month = nowDate.getMonthOfYear();
        int day = nowDate.getDayOfMonth();
        Timestamp begin = Timestamp.valueOf(year + "-" + month + "-" + day + " 00:00:00");
        Integer value = getThisProduction(begin);

        return value;
    }

    /**
     * Gets the value of energy has produced this month.
     *
     * @return total monthly production.
     */
    @POST
    @Path("thisMonthTotal")
    @Produces(MediaType.APPLICATION_JSON)
    public Integer thisMonthTotalProduction() {
        DateTime nowDate = new DateTime(System.currentTimeMillis());
        int year = nowDate.getYear();
        int month = nowDate.getMonthOfYear();

        Timestamp begin = Timestamp.valueOf(year + "-" + month + "-" + "01" + " 00:00:00");

        Integer value = getThisProduction(begin);
        return value;
    }

    /**
     * Gets the value of energy has produced this year.
     *
     * @return total yearly production.
     */
    @POST
    @Path("thisYearTotal")
    @Produces(MediaType.APPLICATION_JSON)
    public Integer thisYearTotalProduction() {
        DateTime nowDate = new DateTime(System.currentTimeMillis());
        int year = nowDate.getYear();

        Timestamp begin = Timestamp.valueOf(year + "-" + "01" + "-" + "01" + " 00:00:00");

        Integer value = getThisProduction(begin);
        return value;
    }


    private List<GraphDataTransfer> transformToDifferences(RtfBox box, List<GraphDataTransfer> list, Date dateBefore) {

        List<GraphDataTransfer> resultList = new ArrayList<GraphDataTransfer>();

        Integer previousValue = rtfBoxDataDao.findTotalProductionBefore(box, dateBefore);
        if (previousValue == null){
            previousValue = 0;
        }

        int value;
        int now = list.get(0).getValue();
        int before = previousValue;
        if (now != 0) {
            value = now - before;
        } else  value = 0;
        resultList.add(new GraphDataTransfer(list.get(0).getDate(), value));

        for (int i = 1; i < list.size(); i++) {
            now = list.get(i).getValue();
            before = list.get(i - 1).getValue();
            if (before == 0) {
                before = previousValue;
            } else {
                previousValue = before;
            }
            if (now != 0){
                value = now - before;
            } else {
                value = 0;
            }
            resultList.add(new GraphDataTransfer(list.get(i).getDate(), value));
        }
        return resultList;
    }

    private int getDaysInMonth(DateTime thisDate){

        String month = thisDate.getYear() + "-" + thisDate.getMonthOfYear() + "-";

        String start = month + "01 00:01:01";
        DateTime startDate = new DateTime(Timestamp.valueOf(start));
        DateTime finishDate = startDate;
        int days = 1;
        while (true){
            finishDate = finishDate.plusDays(1);
            if (finishDate.getDayOfMonth() != 1){
                days++;
            } else break;
        }
        return days;
    }

    private Integer getThisProduction(Timestamp begin){
        RtfBox box = principalInformation.getCompany().getRtfBox();
        Integer valueBefore = rtfBoxDataDao.findTotalProductionBefore(box, begin);
        Integer actualValue = rtfBoxDataDao.findTotalProduction(box);
        return actualValue - valueBefore;
    }

}
