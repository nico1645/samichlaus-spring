package com.samichlaus.api.services;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.*;

import com.samichlaus.api.config.YAMLConfig;
import com.samichlaus.api.domain.constants.Constants;
import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.constants.Version;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.route.Route;
import com.samichlaus.api.domain.route.RouteRepository;
import com.samichlaus.api.domain.tour.Tour;
import com.samichlaus.api.domain.tour.TourRepository;
import com.samichlaus.api.exception.ResourceNotFoundException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.samichlaus.api.domain.constants.Group;

@Service
public class XLSService {


    private final GraphhopperService graphhopperService;

    private final TourRepository tourRepository;
    private final YAMLConfig yamlConfig;

    public XLSService(GraphhopperService graphhopperService, @Qualifier("config") YAMLConfig yamlConfig, TourRepository tourRepository) {
        this.tourRepository = tourRepository;
        this.graphhopperService = graphhopperService;
        this.yamlConfig = yamlConfig;
    }

     public ByteArrayOutputStream getExcel(int year) throws Exception {
        FileInputStream inputStream = new FileInputStream(yamlConfig.getPathToExcelTemplate());
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet eingabeSheet = workbook.getSheetAt(0);
        eingabeSheet.getRow(1).getCell(2).setCellValue(year);
        
        for (int j = 0; j < 3; j++) {
            int rayon = j+1;
            Optional<Tour> tourOpt = tourRepository.findTourByYearAndRayonAndVersion(year, Rayon.fromValue(rayon), Version.TST);
            if (tourOpt.isEmpty())
                throw new ResourceNotFoundException("No tour exists for Rayon " + rayon + " and Year " + year);
            Tour tour = tourOpt.get();

            System.out.println(tour.getRoutes().size());
            int groupCount = 0;
            for (Route route: tour.getRoutes()) {
                if (route.getGroup() == Group.Z) {
                    break;
                }
                Collections.sort(route.getCustomers());
                // Clone the "Vorlage" sheet
                Sheet vorlageSheet = workbook.getSheet("Vorlage");

                Sheet currentGroupSheet = workbook.cloneSheet(workbook.getSheetIndex(vorlageSheet));

                workbook.setSheetName(workbook.getSheetIndex(currentGroupSheet), route.getGroup().toString() + " Rayon " + "I".repeat(rayon));
                eingabeSheet.getRow(3 + rayon).getCell(2).setCellValue(tour.getDate());
                if (!route.getSamichlaus().isBlank())
                    currentGroupSheet.getRow(1).getCell(1).setCellValue(route.getSamichlaus());
                if (!route.getRuprecht().isBlank())
                    currentGroupSheet.getRow(2).getCell(1).setCellValue(route.getRuprecht());
                if (!route.getRuprecht().isBlank())
                    currentGroupSheet.getRow(3).getCell(1).setCellValue(route.getSchmutzli());
                if (!route.getEngel1().isBlank())
                    currentGroupSheet.getRow(4).getCell(1).setCellValue(route.getEngel1());
                if (!route.getEngel2().isBlank())
                    currentGroupSheet.getRow(5).getCell(1).setCellValue(route.getEngel2());

                currentGroupSheet.getRow(0).getCell(1).setCellValue("I".repeat(rayon));
                currentGroupSheet.getRow(0).getCell(4).setCellValue(route.getGroup().toString());
                currentGroupSheet.getRow(0).getCell(5).setCellValue(LocalDateTime.from(LocalDateTime.of(tour.getDate(), route.getCustomerStart())));
                currentGroupSheet.getRow(7).getCell(1).setCellValue(route.getCustomerStart().toSecondOfDay() / ((double) 3600 * 24));
                int depotToFirstTime = route.getCustomerStart().toSecondOfDay() - route.getCustomers().get(0).getVisitTime().toSecondOfDay();
                currentGroupSheet.getRow(7).getCell(7).setCellValue(((double) Math.abs(depotToFirstTime)) / (3600 * 24));
                List<Customer> customers = route.getCustomers();

                for (int i = 0; i < customers.size(); i++) {
                    Row currentRow = currentGroupSheet.getRow(8+i);
                    currentRow.getCell(2).setCellValue(customers.get(i).getLastName() + " " + customers.get(i).getFirstName());
                    currentRow.getCell(3).setCellValue(customers.get(i).getAddress().getAddress());
                    currentRow.getCell(4).setCellValue(customers.get(i).getChildren());
                    if (i+1 < customers.size()) {
                        int duration = customers.get(i).getVisitTime().toSecondOfDay() - customers.get(i+1).getVisitTime().toSecondOfDay();
                        currentRow.getCell(7).setCellValue(((double) Math.abs(duration)) / (3600 * 24));
                    } else {
                        int index = customers.size() - 1;
                        int lastToDepot = Math.round((float) graphhopperService.calculateTime(customers.get(index).getAddress().getLatitude(), customers.get(index).getAddress().getLongitude(), Constants.DEPOT_LAT_LNG.get(0), Constants.DEPOT_LAT_LNG.get(1), customers.get(index).getTransport().toString()) / 1000 / 60 /5) * 5;
                        int durationLastToDepot = lastToDepot + Constants.getChildrenSeniorCapacity(customers.get(index).getSeniors(), customers.get(index).getSeniors());
                        currentRow.getCell(7).setCellValue(((double) durationLastToDepot) / ( 60 * 24));
                    }

                    switch (customers.get(i).getSeniors()) {
                        case 0 -> {
                            currentRow.getCell(5).setCellValue(0);
                            currentRow.getCell(6).setCellValue(0);
                        }
                        case 1 -> {
                            currentRow.getCell(5).setCellValue(1);
                            currentRow.getCell(6).setCellValue(0);
                        }
                        case 2 -> {
                            currentRow.getCell(5).setCellValue(1);
                            currentRow.getCell(6).setCellValue(1);
                        }
                        default -> {
                            currentRow.getCell(5).setCellValue(customers.get(i).getSeniors() - 1);
                            currentRow.getCell(6).setCellValue(1);
                        }
                    }
                }
                groupCount++;
            }
            eingabeSheet.getRow(3 + rayon).getCell(3).setCellValue(groupCount);
        }

        workbook.removeSheetAt(4);
        workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

        ByteArrayOutputStream outputByteStream = new ByteArrayOutputStream();
        workbook.write(outputByteStream);

        workbook.close();
        inputStream.close();
        return outputByteStream;
    } 

}
