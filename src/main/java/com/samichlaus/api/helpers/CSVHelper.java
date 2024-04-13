package com.samichlaus.api.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.samichlaus.api.domain.constants.Rayon;
import com.samichlaus.api.domain.user.UserRepository;
import com.samichlaus.api.domain.vrp.CSVObject;
import com.samichlaus.api.exception.IllegalCSVFileException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import org.geotools.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;


import com.samichlaus.api.domain.address.Address;
import com.samichlaus.api.domain.address.AddressRepository;
import com.samichlaus.api.domain.customer.Customer;
import com.samichlaus.api.domain.user.User;

public class CSVHelper {
  public static String TYPE = "text/csv";
  enum AddressHeaders{
    EGID, EDID, EGAID, DEINR, ESID, STRNAME, STRNAMK, STRINDX, STRSP, STROFFIZIEL, DPLZ4, DPLZZ, DPLZNAME, DKODE, DKODN, DOFFADR, DEXPDAT

  } 
  enum CustomerHeaders{
    DATE, RAYON, NAME, ADDRESS, EMAIL, PHONE, COUNTCHILD, LINK
  } 
  public static boolean hasCSVFormat(MultipartFile file) {
      return TYPE.equals(file.getContentType());
  }

  public static CSVObject csvToCustomers(InputStream is, AddressRepository addressRepository, User user) throws IllegalCSVFileException {
    CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                          .setHeader(CustomerHeaders.class)
                          .setSkipHeaderRecord(true)
                          .setIgnoreEmptyLines(true)
                          .setDelimiter(",")
                          .setAllowMissingColumnNames(false)
                          .setTrim(true)
                          .build();
    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
         CSVParser csvParser = new CSVParser(fileReader, csvFormat)) {

      if (csvParser.getHeaderMap().size() != CustomerHeaders.values().length) {
        throw new IllegalCSVFileException("CSV file is of wrong format. Seperator should be \",\". Headers should be %s and order matters".formatted(CustomerHeaders.values().toString()));
      }

        List<Customer> customers = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        Iterable<CSVRecord> csvRecords = csvParser.getRecords();

        for (CSVRecord csvRecord : csvRecords) {
          List<Address> addresses = addressRepository.findByAddress(csvRecord.get(CustomerHeaders.ADDRESS));
          if (addresses.isEmpty()) {
              System.out.println(csvRecord.get(CustomerHeaders.ADDRESS));
              throw new IllegalCSVFileException("Address is not valid in row %s with value (%s)".formatted(csvRecord.getRecordNumber(), csvRecord.get(CustomerHeaders.ADDRESS)));
          }
          String[] name = csvRecord.get(CustomerHeaders.NAME).split(" ", 2);

          DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN);
          
          char rayon = csvRecord.get(CustomerHeaders.RAYON).charAt(csvRecord.get(CustomerHeaders.RAYON).length() - 1);

          LocalDate localVisitDate = LocalDate.parse(csvRecord.get(CustomerHeaders.DATE), inputFormatter);

          Customer customer = Customer.builder()
                              .address(addresses.get(0))
                              .children(Integer.parseInt(csvRecord.get(CustomerHeaders.COUNTCHILD)))
                              .email(csvRecord.get(CustomerHeaders.EMAIL))
                              .firstName(name[0])
                              .lastName(name[1])
                              .link(csvRecord.get(CustomerHeaders.LINK))
                              .phone(csvRecord.get(CustomerHeaders.PHONE))
                              .seniors(0)
                              .year(localVisitDate.getYear())
                              .visitTime(LocalTime.of(0,0))
                              .visitRayon(Rayon.fromValue(Character.getNumericValue(rayon)))
                              .lastModified(new Date())
                              .user(user)
                              .build();
          customers.add(customer);
          dates.add(localVisitDate);
        }
        return new CSVObject(customers, dates);
      } catch (IOException | MismatchedDimensionException e) {
      throw new IllegalCSVFileException("fail to parse CSV file: " + e.getMessage());
    }
  }

  public static List<Address> csvToAddresses(InputStream is, AddressRepository addressRepository) throws TransformException, IllegalCSVFileException {
    CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                          .setHeader(AddressHeaders.class)
                          .setSkipHeaderRecord(true)
                          .setIgnoreEmptyLines(true)
                          .setDelimiter(";")
                          .setAllowMissingColumnNames(false)
                          .setTrim(true)
                          .build();
    
    GeometryFactory gf = new GeometryFactory();
    List<Coordinate> rayon1Coords = Arrays.asList(new Coordinate(8.3147085, 46.979169), new Coordinate(8.3177125, 46.9896794), new Coordinate(8.3154058, 46.9953362), new Coordinate(8.3123884, 46.9991852), new Coordinate(8.3117741, 46.9991907), new Coordinate(8.311758, 46.9986108), new Coordinate(8.3117124, 46.9981077), new Coordinate(8.311699, 46.9976614), new Coordinate(8.3116561, 46.9973486), new Coordinate(8.31164, 46.9970101), new Coordinate(8.31164, 46.996679), new Coordinate(8.3116776, 46.9963442), new Coordinate(8.3118036, 46.9959473), new Coordinate(8.3120289, 46.9955594), new Coordinate(8.3123669, 46.9951441), new Coordinate(8.3126163, 46.9948039), new Coordinate(8.3127585, 46.9944581), new Coordinate(8.3127558, 46.9941562), new Coordinate(8.3126995, 46.9938397), new Coordinate(8.3126378, 46.9935068), new Coordinate(8.3126137, 46.9931153), new Coordinate(8.3127022, 46.9926835), new Coordinate(8.3129141, 46.9923085), new Coordinate(8.3132172, 46.991948), new Coordinate(8.313531, 46.9916224), new Coordinate(8.3137992, 46.9913077), new Coordinate(8.3139306, 46.990993), new Coordinate(8.3139226, 46.990607), new Coordinate(8.3138126, 46.9903014), new Coordinate(8.3136195, 46.9900618), new Coordinate(8.3133566, 46.9898239), new Coordinate(8.3128256, 46.989522), new Coordinate(8.3122972, 46.9891707), new Coordinate(8.3117661, 46.9887115), new Coordinate(8.3112001, 46.9881882), new Coordinate(8.3108059, 46.9878168), new Coordinate(8.3104411, 46.9874746), new Coordinate(8.3102211, 46.9872075), new Coordinate(8.3100253, 46.9869276), new Coordinate(8.3098724, 46.9866385), new Coordinate(8.3097759, 46.9863933), new Coordinate(8.3096927, 46.9860438), new Coordinate(8.3096579, 46.9856156), new Coordinate(8.3097866, 46.9849185), new Coordinate(8.3099824, 46.984322), new Coordinate(8.3100629, 46.9836157), new Coordinate(8.3099288, 46.9827886), new Coordinate(8.3096659, 46.9817968), new Coordinate(8.3095694, 46.9808215), new Coordinate(8.309623, 46.9801499), new Coordinate(8.3093441, 46.9788177), new Coordinate(8.3112538, 46.9784956), new Coordinate(8.3147085, 46.979169));
    List<Coordinate> rayon2Coords = Arrays.asList(new Coordinate(8.3093548, 46.9752601), new Coordinate(8.3095801, 46.9803091), new Coordinate(8.3095828, 46.9813009), new Coordinate(8.3098134, 46.9823805), new Coordinate(8.3099851, 46.9832186), new Coordinate(8.3100361, 46.9838115), new Coordinate(8.3099422, 46.9843824), new Coordinate(8.3097786, 46.9849917), new Coordinate(8.3096632, 46.9855589), new Coordinate(8.3097008, 46.9860603), new Coordinate(8.3098322, 46.9865305), new Coordinate(8.3100173, 46.9869349), new Coordinate(8.3102775, 46.9873008), new Coordinate(8.3107254, 46.9877473), new Coordinate(8.3112189, 46.9881974), new Coordinate(8.3116695, 46.9886218), new Coordinate(8.3121738, 46.9890847), new Coordinate(8.3127612, 46.9895037), new Coordinate(8.3133888, 46.9898696), new Coordinate(8.3136865, 46.9901203), new Coordinate(8.3138877, 46.9904551), new Coordinate(8.3125547, 46.990768), new Coordinate(8.3120048, 46.9908997), new Coordinate(8.3112001, 46.9909601), new Coordinate(8.3099717, 46.990929), new Coordinate(8.3090115, 46.9907076), new Coordinate(8.3084133, 46.9905832), new Coordinate(8.3075792, 46.9905301), new Coordinate(8.3068845, 46.9904624), new Coordinate(8.3062139, 46.9902868), new Coordinate(8.3059162, 46.9900563), new Coordinate(8.3058277, 46.9896739), new Coordinate(8.3057204, 46.9892933), new Coordinate(8.305251, 46.9889567), new Coordinate(8.3046877, 46.9886346), new Coordinate(8.3044088, 46.9882943), new Coordinate(8.3040601, 46.9878772), new Coordinate(8.3034861, 46.9875789), new Coordinate(8.3029121, 46.9872532), new Coordinate(8.3021611, 46.9868178), new Coordinate(8.301453, 46.986708), new Coordinate(8.3009434, 46.9869568), new Coordinate(8.3006591, 46.9874948), new Coordinate(8.3002996, 46.9875423), new Coordinate(8.2992965, 46.9874728), new Coordinate(8.2984114, 46.987352), new Coordinate(8.2977945, 46.987352), new Coordinate(8.2972312, 46.9874618), new Coordinate(8.2958311, 46.9873667), new Coordinate(8.2946134, 46.9874838), new Coordinate(8.2937014, 46.9876924), new Coordinate(8.2925588, 46.9878461), new Coordinate(8.2911372, 46.9880693), new Coordinate(8.290059, 46.9884059), new Coordinate(8.2889754, 46.9884023), new Coordinate(8.2881171, 46.9882779), new Coordinate(8.2872587, 46.9879632), new Coordinate(8.2865024, 46.9879375), new Coordinate(8.2851505, 46.9882705), new Coordinate(8.2842708, 46.9886804), new Coordinate(8.2829887, 46.9885779), new Coordinate(8.2817817, 46.9887755), new Coordinate(8.2807517, 46.9884938), new Coordinate(8.2794428, 46.9885999), new Coordinate(8.2786059, 46.9890939), new Coordinate(8.2773399, 46.9893647), new Coordinate(8.2766104, 46.9898733), new Coordinate(8.2752424, 46.9897416), new Coordinate(8.2737619, 46.9892805), new Coordinate(8.2723349, 46.9889987), new Coordinate(8.2712674, 46.9885706), new Coordinate(8.2700872, 46.9885157), new Coordinate(8.269009, 46.9879924), new Coordinate(8.2675982, 46.9881169), new Coordinate(8.2661337, 46.9881022), new Coordinate(8.2642508, 46.988126), new Coordinate(8.2609463, 46.9879229), new Coordinate(8.2600021, 46.9828874), new Coordinate(8.2704735, 46.9755089), new Coordinate(8.3093548, 46.9752601));
    List<Coordinate> rayon3Coords = Arrays.asList(new Coordinate(8.2427931, 46.9970559), new Coordinate(8.2476854, 46.9851417), new Coordinate(8.2584143, 46.9796082), new Coordinate(8.2675123, 46.9883328), new Coordinate(8.2714927, 46.9886694), new Coordinate(8.275044, 46.9896355), new Coordinate(8.2762697, 46.9899575), new Coordinate(8.2776135, 46.989222), new Coordinate(8.2788366, 46.9890317), new Coordinate(8.2799256, 46.9884755), new Coordinate(8.2815832, 46.9887243), new Coordinate(8.2844746, 46.9886145), new Coordinate(8.2860678, 46.9879851), new Coordinate(8.2873446, 46.9879375), new Coordinate(8.2883692, 46.9883474), new Coordinate(8.2900268, 46.9884206), new Coordinate(8.291561, 46.9879192), new Coordinate(8.2932132, 46.9878241), new Coordinate(8.2949513, 46.9874435), new Coordinate(8.2970273, 46.9873484), new Coordinate(8.2988915, 46.9874582), new Coordinate(8.3005705, 46.9875771), new Coordinate(8.3010882, 46.9868251), new Coordinate(8.301968, 46.9866604), new Coordinate(8.303352, 46.9873996), new Coordinate(8.3043712, 46.9880949), new Coordinate(8.3049452, 46.9886804), new Coordinate(8.3057982, 46.9893354), new Coordinate(8.3059028, 46.9898404), new Coordinate(8.3059216, 46.9900288), new Coordinate(8.3062059, 46.9902941), new Coordinate(8.3066511, 46.9904149), new Coordinate(8.3073968, 46.990521), new Coordinate(8.3082363, 46.990585), new Coordinate(8.309041, 46.9906875), new Coordinate(8.309902, 46.9909034), new Coordinate(8.3108434, 46.9909583), new Coordinate(8.3117151, 46.9909143), new Coordinate(8.3124313, 46.9907936), new Coordinate(8.3130482, 46.9906472), new Coordinate(8.313885, 46.9904387), new Coordinate(8.313944, 46.990671), new Coordinate(8.313936, 46.9909509), new Coordinate(8.3138287, 46.9912528), new Coordinate(8.3136302, 46.9915254), new Coordinate(8.3133727, 46.9917889), new Coordinate(8.3131287, 46.9920523), new Coordinate(8.3128685, 46.9923579), new Coordinate(8.3127022, 46.9927018), new Coordinate(8.312619, 46.993097), new Coordinate(8.3126459, 46.9934629), new Coordinate(8.3126888, 46.9938233), new Coordinate(8.31278, 46.9941873), new Coordinate(8.3127666, 46.9944984), new Coordinate(8.3125895, 46.9948716), new Coordinate(8.3123106, 46.9952246), new Coordinate(8.3120182, 46.9955649), new Coordinate(8.3118412, 46.9958924), new Coordinate(8.3117151, 46.996229), new Coordinate(8.3116454, 46.996551), new Coordinate(8.31164, 46.9969113), new Coordinate(8.3116615, 46.9972479), new Coordinate(8.3116695, 46.9976156), new Coordinate(8.311758, 46.998512), new Coordinate(8.3118117, 46.9993169), new Coordinate(8.3038402, 46.9985193), new Coordinate(8.2979393, 46.9989876), new Coordinate(8.2807302, 46.9968802), new Coordinate(8.2427931, 46.9970559));
    
    Polygon rayon1 = gf.createPolygon(rayon1Coords.toArray(new Coordinate[0]));
    Polygon rayon2 = gf.createPolygon(rayon2Coords.toArray(new Coordinate[0]));
    Polygon rayon3 = gf.createPolygon(rayon3Coords.toArray(new Coordinate[0]));


    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
         CSVParser csvParser = new CSVParser(fileReader, csvFormat)) {

    if (csvParser.getHeaderMap().size() != AddressHeaders.values().length) {
      throw new IllegalCSVFileException("CSV file is of wrong format. Seperator should be \";\". Headers should be %s and order matters".formatted(AddressHeaders.values().toString()));
    }

      // Define the source CRS (Swiss coordinate system)
    CoordinateReferenceSystem CH1903PLUS_CRS = CRS.decode("EPSG:2056"); // Swiss coordinate system

    // Define the target CRS (WGS84, i.e. latitude and longitude)
    CoordinateReferenceSystem WGS84_CRS  = CRS.decode("EPSG:4326"); // WGS84 (latitude and longitude)

    // Define the transformation from source to target CRS
    MathTransform transform = CRS.findMathTransform(CH1903PLUS_CRS, WGS84_CRS);

      List<Address> addresses = new ArrayList<>();
      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {
        if (!csvRecord.get(AddressHeaders.DKODE).isEmpty() && !csvRecord.get(AddressHeaders.DKODN).isEmpty()) {
          double dkode = Double.parseDouble(csvRecord.get(AddressHeaders.DKODE));
          double dkodn = Double.parseDouble(csvRecord.get(AddressHeaders.DKODN));      
          double[] longLat = swissCoordinatesToLatLong(dkode, dkodn, CH1903PLUS_CRS, transform);
          Point point = gf.createPoint(new Coordinate(longLat[0], longLat[1]));
          Integer rayon = getRayonFromLatLong(point, rayon1, rayon2, rayon3);
          String postalAddress;
          if (csvRecord.get(AddressHeaders.DEINR).isEmpty()) {
            postalAddress = csvRecord.get(AddressHeaders.STRNAME);
          } else if (csvRecord.get(AddressHeaders.DEINR).contains(".")) {
            postalAddress = "";
          } else {
            postalAddress = csvRecord.get(AddressHeaders.STRNAME) + " " + csvRecord.get(AddressHeaders.DEINR);
          }
          if (!postalAddress.isEmpty()) {
            Address address = Address.builder()
            .longitude(((float)longLat[0]))
            .latitude(((float)longLat[1]))
            .dkode(((float)dkode))
            .dkodn(((float)dkodn))
            .zipName(csvRecord.get(AddressHeaders.DPLZNAME))
            .zipCode(Integer.parseInt(csvRecord.get(AddressHeaders.DPLZ4)))
            .address(postalAddress)
            .rayon(Rayon.fromValue(rayon))
            .build();
          if (addressRepository.findByAddress(postalAddress).isEmpty()) {
            addresses.add(address);
          }
          }
        }          
      }

      return addresses;
    } catch (IOException | FactoryException | MismatchedDimensionException e) {
      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
    }
  }


  private static Integer getRayonFromLatLong(Point point, Polygon rayon1, Polygon rayon2, Polygon rayon3) {
    if (rayon1.contains(point)) {
      return 1;
  }
  
  if (rayon2.contains(point)) {
      return 2;
  }
  
  if (rayon3.contains(point)) {
      return 3;
  }

  throw new RuntimeException("Point is not within any polygon");
  }

  private static double[] swissCoordinatesToLatLong(double easting, double northing, CoordinateReferenceSystem sourceCRS, MathTransform transform) throws FactoryException, MismatchedDimensionException, TransformException {

    // Transform the input coordinates from source to target CRS
    DirectPosition inputPos = new DirectPosition2D(sourceCRS, easting, northing);
    DirectPosition wgs84Pos = transform.transform(inputPos, null);

    // Return the transformed coordinates as an array [longitude, latitude]
    return new double[]{wgs84Pos.getOrdinate(1), wgs84Pos.getOrdinate(0)};
}

}