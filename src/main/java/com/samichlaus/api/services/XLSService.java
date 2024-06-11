package com.samichlaus.api.services;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.*;

import com.samichlaus.api.config.YAMLConfig;
import com.samichlaus.api.domain.constants.*;
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

            int groupCount = 0;
            List<Route> routes = tour.getRoutes();
            routes.sort(Comparator.comparing(Route::getGroup));
            for (Route route: tour.getRoutes()) {
                if (route.getGroup() == Group.Z) {
                    continue;
                }
                Collections.sort(route.getCustomers());
                // Clone the "Vorlage" sheet
                Sheet vorlageSheet = workbook.getSheet("Vorlage");

                Sheet currentGroupSheet = workbook.cloneSheet(workbook.getSheetIndex(vorlageSheet));

                workbook.setSheetName(workbook.getSheetIndex(currentGroupSheet), route.getGroup().toString() + " Rayon " + "I".repeat(rayon));
                eingabeSheet.getRow(3 + rayon).getCell(2).setCellValue(tour.getDate());
                if (!route.getSamichlaus().isBlank())
                    currentGroupSheet.getRow(1).getCell(2).setCellValue(route.getSamichlaus());
                if (!route.getRuprecht().isBlank())
                    currentGroupSheet.getRow(2).getCell(2).setCellValue(route.getRuprecht());
                if (!route.getSchmutzli().isBlank())
                    currentGroupSheet.getRow(3).getCell(2).setCellValue(route.getSchmutzli());
                if (!route.getEngel1().isBlank())
                    currentGroupSheet.getRow(4).getCell(2).setCellValue(route.getEngel1());
                if (!route.getEngel2().isBlank())
                    currentGroupSheet.getRow(5).getCell(2).setCellValue(route.getEngel2());
                String transport = route.getTransport() == Transportation.foot ? "Laufen" : "Fahren";
                currentGroupSheet.getRow(6).getCell(2).setCellValue(transport);

                currentGroupSheet.getRow(0).getCell(1).setCellValue("I".repeat(rayon));
                currentGroupSheet.getRow(0).getCell(4).setCellValue(route.getGroup().toString());
                currentGroupSheet.getRow(0).getCell(5).setCellValue(LocalDateTime.from(LocalDateTime.of(tour.getDate(), route.getCustomerStart())));
                currentGroupSheet.getRow(7).getCell(1).setCellValue(route.getCustomerStart().toSecondOfDay() / ((double) 3600 * 24));
                int depotToFirstTime = route.getCustomerStart().toSecondOfDay() - route.getCustomers().get(0).getVisitTime().toSecondOfDay();
                currentGroupSheet.getRow(7).getCell(7).setCellValue(((double) Math.abs(depotToFirstTime)) / (3600 * 24));
                List<Customer> customers = route.getCustomers();

                for (int i = 0; i < customers.size(); i++) {
                    Row currentRow = currentGroupSheet.getRow(8+i);
                    currentRow.getCell(2).setCellValue(customers.get(i).getLastName().strip() + " " + customers.get(i).getFirstName().strip());
                    currentRow.getCell(3).setCellValue(customers.get(i).getAddress().getAddress());
                    currentRow.getCell(4).setCellValue(customers.get(i).getChildren());
                    if (customers.get(i).getTransport() == Transportation.car)
                        currentRow.getCell(8).setCellValue("Fahren");
                    if (i+1 < customers.size()) {
                        int duration = customers.get(i).getVisitTime().toSecondOfDay() - customers.get(i+1).getVisitTime().toSecondOfDay();
                        currentRow.getCell(7).setCellValue(((double) Math.abs(duration)) / (3600 * 24));
                    } else {
                        int index = customers.size() - 1;
                        int lastToDepot = route.getCustomers().get(index).getVisitTime().toSecondOfDay() - route.getCustomerEnd().toSecondOfDay();
                        currentRow.getCell(7).setCellValue(((double) Math.abs(lastToDepot)) / (3600 * 24));
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
