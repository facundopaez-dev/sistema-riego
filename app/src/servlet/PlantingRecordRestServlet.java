package servlet;

import climate.ClimateClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import et.Eto;
import irrigation.WaterMath;
import java.io.IOException;
import java.lang.Math;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import model.ClimateRecord;
import model.Crop;
import model.IrrigationRecord;
import model.Parcel;
import model.PlantingRecord;
import model.PlantingRecordStatus;
import stateless.ClimateRecordServiceBean;
import stateless.CropServiceBean;
import stateless.IrrigationRecordServiceBean;
import stateless.MaximumInsolationServiceBean;
import stateless.ParcelServiceBean;
import stateless.PlantingRecordServiceBean;
import stateless.PlantingRecordStatusServiceBean;
import stateless.SecretKeyServiceBean;
import stateless.SolarRadiationServiceBean;
import util.ErrorResponse;
import util.ReasonError;
import util.RequestManager;
import util.UtilDate;
import utilJwt.AuthHeaderManager;
import utilJwt.JwtManager;

@Path("/plantingRecords")
public class PlantingRecordRestServlet {

  // inject a reference to the PlantingRecordServiceBean
  @EJB PlantingRecordServiceBean plantingRecordService;

  // inject a reference to the ClimateRecordServiceBean
  @EJB ClimateRecordServiceBean climateRecordServiceBean;

  // inject a reference to the CropServiceBean
  @EJB CropServiceBean cropService;

  // inject a reference to the IrrigationRecordServiceBean
  @EJB IrrigationRecordServiceBean irrigationRecordService;

  // inject a reference to the PlantingRecordStatusServiceBean
  @EJB PlantingRecordStatusServiceBean statusService;

  // inject a reference to the ParcelServiceBean slsb
  @EJB ParcelServiceBean serviceParcel;

  // inject a reference to the SolarRadiationServiceBean
  @EJB SolarRadiationServiceBean solarService;

  // inject a reference to the MaximumInsolationServiceBean
  @EJB MaximumInsolationServiceBean insolationService;

  // inject a reference to the SecretKeyServiceBean
  @EJB SecretKeyServiceBean secretKeyService;

  // Mapea lista de pojo a JSON
  ObjectMapper mapper = new ObjectMapper();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAll(@Context HttpHeaders request) throws IOException {
    Response givenResponse = RequestManager.validateAuthHeader(request, secretKeyService.find());

    /*
     * Si el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP NO
     * es ACCEPTED, se devuelve el estado de error de la misma.
     * 
     * Que el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP sea
     * ACCEPTED, significa que la peticion es valida,
     * debido a que el encabezado de autorizacion de la misma
     * cumple las siguientes condiciones:
     * - Esta presente.
     * - No esta vacio.
     * - Cumple con la convencion de JWT.
     * - Contiene un JWT valido.
     */
    if (!RequestManager.isAccepted(givenResponse)) {
      return givenResponse;
    }

    /*
     * Obtiene el JWT del valor del encabezado de autorizacion
     * de una peticion HTTP
     */
    String jwt = AuthHeaderManager.getJwt(AuthHeaderManager.getAuthHeaderValue(request));

    /*
     * Obtiene el ID de usuario contenido en la carga util del
     * JWT del encabezado de autorizacion de una peticion HTTP
     */
    int userId = JwtManager.getUserId(jwt, secretKeyService.find().getValue());

    /*
     * Si el valor del encabezado de autorizacion de la peticion HTTP
     * dada, tiene un JWT valido, la aplicacion del lado servidor
     * devuelve el mensaje HTTP 200 (Ok) junto con los datos solicitados
     * por el cliente
     */
    return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(plantingRecordService.findAll(userId))).build();
  }

  @GET
  @Path("/findAllByParcelName/{parcelName}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response findAllByParcelName(@Context HttpHeaders request, @PathParam("parcelName") String givenParcelName) throws IOException {
    Response givenResponse = RequestManager.validateAuthHeader(request, secretKeyService.find());

    /*
     * Si el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP NO
     * es ACCEPTED, se devuelve el estado de error de la misma.
     * 
     * Que el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP sea
     * ACCEPTED, significa que la peticion es valida,
     * debido a que el encabezado de autorizacion de la misma
     * cumple las siguientes condiciones:
     * - Esta presente.
     * - No esta vacio.
     * - Cumple con la convencion de JWT.
     * - Contiene un JWT valido.
     */
    if (!RequestManager.isAccepted(givenResponse)) {
      return givenResponse;
    }

    /*
     * Obtiene el JWT del valor del encabezado de autorizacion
     * de una peticion HTTP
     */
    String jwt = AuthHeaderManager.getJwt(AuthHeaderManager.getAuthHeaderValue(request));

    /*
     * Obtiene el ID de usuario contenido en la carga util del
     * JWT del encabezado de autorizacion de una peticion HTTP
     */
    int userId = JwtManager.getUserId(jwt, secretKeyService.find().getValue());

    /*
     * Si el valor del encabezado de autorizacion de la peticion HTTP
     * dada, tiene un JWT valido, la aplicacion del lado servidor
     * devuelve el mensaje HTTP 200 (Ok) junto con los datos solicitados
     * por el cliente
     */
    return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(plantingRecordService.findAllByParcelName(userId, givenParcelName))).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response find(@Context HttpHeaders request, @PathParam("id") int plantingRecordId) throws IOException {
    Response givenResponse = RequestManager.validateAuthHeader(request, secretKeyService.find());

    /*
     * Si el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP NO
     * es ACCEPTED, se devuelve el estado de error de la misma.
     * 
     * Que el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP sea
     * ACCEPTED, significa que la peticion es valida,
     * debido a que el encabezado de autorizacion de la misma
     * cumple las siguientes condiciones:
     * - Esta presente.
     * - No esta vacio.
     * - Cumple con la convencion de JWT.
     * - Contiene un JWT valido.
     */
    if (!RequestManager.isAccepted(givenResponse)) {
      return givenResponse;
    }

    /*
     * Si el dato solicitado no existe en la base de datos
     * subyacente, la aplicacion del lado servidor devuelve
     * el mensaje HTTP 404 (Not found) junto con el mensaje
     * "Recurso no encontrado" y no se realiza la operacion
     * solicitada
     */
    if (!plantingRecordService.checkExistence(plantingRecordId)) {
      return Response.status(Response.Status.NOT_FOUND).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.RESOURCE_NOT_FOUND))).build();
    }

    /*
     * Obtiene el JWT del valor del encabezado de autorizacion
     * de una peticion HTTP
     */
    String jwt = AuthHeaderManager.getJwt(AuthHeaderManager.getAuthHeaderValue(request));

    /*
     * Obtiene el ID de usuario contenido en la carga util del
     * JWT del encabezado de autorizacion de una peticion HTTP
     */
    int userId = JwtManager.getUserId(jwt, secretKeyService.find().getValue());

    /*
     * Si al usuario que hizo esta peticion HTTP, no le pertenece
     * el dato solicitado, la aplicacion del lado servidor
     * devuelve el mensaje HTTP 403 (Forbidden) junto con el
     * mensaje "Acceso no autorizado" (contenido en el enum
     * ReasonError) y no se realiza la operacion solicitada
     */
    if (!plantingRecordService.checkUserOwnership(userId, plantingRecordId)) {
      return Response.status(Response.Status.FORBIDDEN).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.UNAUTHORIZED_ACCESS))).build();
    }

    /*
     * Si el valor del encabezado de autorizacion de la peticion HTTP
     * dada, tiene un JWT valido, la aplicacion del lado servidor
     * devuelve el mensaje HTTP 200 (Ok) junto con los datos solicitados
     * por el cliente
     */
    return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(plantingRecordService.findByUserId(userId, plantingRecordId))).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(@Context HttpHeaders request, String json) throws IOException {
    Response givenResponse = RequestManager.validateAuthHeader(request, secretKeyService.find());

    /*
     * Si el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP NO
     * es ACCEPTED, se devuelve el estado de error de la misma.
     * 
     * Que el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP sea
     * ACCEPTED, significa que la peticion es valida,
     * debido a que el encabezado de autorizacion de la misma
     * cumple las siguientes condiciones:
     * - Esta presente.
     * - No esta vacio.
     * - Cumple con la convencion de JWT.
     * - Contiene un JWT valido.
     */
    if (!RequestManager.isAccepted(givenResponse)) {
      return givenResponse;
    }

    PlantingRecord newPlantingRecord = mapper.readValue(json, PlantingRecord.class);

    /*
     * Si la fecha de siembra del registro de plantacion a
     * crear NO esta definida, la aplicacion del lado
     * servidor retorna el mensaje HTTP 400 (Bad request)
     * junto con el mensaje "La fecha de siembra debe estar
     * definida" y no se realiza la operacion solicitada
     */
    if (newPlantingRecord.getSeedDate() == null) {
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.INDEFINITE_SEED_DATE))).build();
    }

    /*
     * Se calcula la fecha de cosecha del cultivo del nuevo
     * registro de plantacion en funcion de la fecha de siembra
     * y el ciclo de vida del cultivo
     */
    Calendar harvestDate = cropService.calculateHarvestDate(newPlantingRecord.getSeedDate(), newPlantingRecord.getCrop());
    newPlantingRecord.setHarvestDate(harvestDate);

    /*
     * Si la fecha de siembra del registro de plantacion que se
     * quiere crear es mayor estricta (posterior) a la fecha actual,
     * la aplicacion del lado servidor retorna el mensaje HTTP 400
     * (Bad request) junto con el mensaje "No esta permitido crear
     * un registro de plantacion con una fecha de siembra estrictamente
     * mayor (posterior) que la fecha actual"
     */
    if (plantingRecordService.isFromFuture(newPlantingRecord)) {
      return Response.status(Response.Status.BAD_REQUEST)
        .entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.CREATION_FUTURE_PLANTING_RECORD_NOT_ALLOWED))).build();
    }

    /*
     * Se establece el estado del nuevo registro de plantacion
     * en base a la fecha de cosecha de su cultivo
     */
    newPlantingRecord.setStatus(statusService.calculateStatus(newPlantingRecord.getHarvestDate()));

    /*
     * Si la parcela del registro de plantacion a crear NO
     * esta definida, la aplicacion del lado servidor retorna
     * el mensaje HTTP 400 (Bad request) junto con el mensaje
     * "La parcela debe estar definida" y no se realiza la
     * operacion solicitada
     */
    if (newPlantingRecord.getParcel() == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.INDEFINITE_PARCEL))).build();
    }

    /*
     * Si el cultivo del registro de plantacion a crear NO
     * esta definido, la aplicacion del lado servidor retorna
     * el mensaje HTTP 400 (Bad request) junto con el mensaje
     * "El cultivo debe estar definido" y no se realiza la
     * operacion solicitada
     */
    if (newPlantingRecord.getCrop() == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.INDEFINITE_CROP))).build();
    }

    /*
     * Si la parcela para la que se quiere crear un registro
     * de plantacion, tiene registros de plantacion, se comprueba
     * si la misma tiene un registro de plantacion en desarrollo
     * y si la fecha de cosecha de su ultimo registro de plantacion
     * finalizado es mayor o igual a la fecha de siembra del
     * registro de plantacion que se quiere crear
     */
    if (plantingRecordService.thereIsPlantingRecords(newPlantingRecord.getParcel())) {
      /*
       * Si hay un registro de plantacion en desarrollo para
       * la parcela del registro de plantacion que se quiere
       * crear, la aplicacion del lado servidor retorna el
       * mensaje HTTP 400 (Bad request) junto con el mensaje
       * "No esta permitido crear un registro de plantacion
       * para una parcela que tiene un registro de plantacion
       * en desarrollo" y no se realiza la operacion solicitada
       */
      if (plantingRecordService.checkOneInDevelopment(newPlantingRecord.getParcel())) {
        return Response.status(Response.Status.BAD_REQUEST)
          .entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.CREATION_NOT_ALLOWED_IN_DEVELOPMENT))).build();
      }

      PlantingRecord lastFinishedPlantingRecord = plantingRecordService.findLastFinished(newPlantingRecord.getParcel());

      /*
       * Si la fecha de cosecha del ultimo registro de plantacion
       * finalizado es mayor o igual a la fecha de siembra del
       * registro de plantacion que se quiere crear, la aplicacion
       * del lado servidor retorna el mensaje HTTP 400 (Bad request)
       * junto con el mensaje "No esta permitido crear un registro
       * de plantacion con una fecha de siembra anterior o igual a
       * la fecha de cosecha del ultimo registro de plantacion
       * finalizado de una parcela".
       */
      if (plantingRecordService.checkDateOverlap(lastFinishedPlantingRecord.getHarvestDate(), newPlantingRecord.getSeedDate())) {
        return Response.status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.DATE_OVERLAP_ON_CREATION))).build();
      }

    } // End if

    /*
     * Si el valor del encabezado de autorizacion de la peticion HTTP
     * dada, tiene un JWT valido y no se cumplen las condiciones de
     * los controles para la creacion de un registro de plantacion, la
     * aplicacion del lado servidor devuelve el mensaje HTTP 200 (Ok)
     * junto con los datos que el cliente solicito persistir
     */
    return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(plantingRecordService.create(newPlantingRecord))).build();
  }

  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response modify(@Context HttpHeaders request, @PathParam("id") int plantingRecordId, String json) throws IOException {
    Response givenResponse = RequestManager.validateAuthHeader(request, secretKeyService.find());

    /*
     * Si el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP NO
     * es ACCEPTED, se devuelve el estado de error de la misma.
     * 
     * Que el estado de la respuesta obtenida de validar el
     * encabezado de autorizacion de una peticion HTTP sea
     * ACCEPTED, significa que la peticion es valida,
     * debido a que el encabezado de autorizacion de la misma
     * cumple las siguientes condiciones:
     * - Esta presente.
     * - No esta vacio.
     * - Cumple con la convencion de JWT.
     * - Contiene un JWT valido.
     */
    if (!RequestManager.isAccepted(givenResponse)) {
      return givenResponse;
    }

    /*
     * Si el dato solicitado no existe en la base de datos
     * subyacente, la aplicacion del lado servidor devuelve
     * el mensaje HTTP 404 (Not found) junto con el mensaje
     * "Recurso no encontrado" y no se realiza la operacion
     * solicitada
     */
    if (!plantingRecordService.checkExistence(plantingRecordId)) {
      return Response.status(Response.Status.NOT_FOUND).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.RESOURCE_NOT_FOUND))).build();
    }

    /*
     * Obtiene el JWT del valor del encabezado de autorizacion
     * de una peticion HTTP
     */
    String jwt = AuthHeaderManager.getJwt(AuthHeaderManager.getAuthHeaderValue(request));

    /*
     * Obtiene el ID de usuario contenido en la carga util del
     * JWT del encabezado de autorizacion de una peticion HTTP
     */
    int userId = JwtManager.getUserId(jwt, secretKeyService.find().getValue());

    /*
     * Si al usuario que hizo esta peticion HTTP, no le pertenece
     * el dato solicitado, la aplicacion del lado servidor
     * devuelve el mensaje HTTP 403 (Forbidden) junto con el
     * mensaje "Acceso no autorizado" (contenido en el enum
     * ReasonError) y no se realiza la operacion solicitada
     */
    if (!plantingRecordService.checkUserOwnership(userId, plantingRecordId)) {
      return Response.status(Response.Status.FORBIDDEN).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.UNAUTHORIZED_ACCESS))).build();
    }

    PlantingRecord modifiedPlantingRecord = mapper.readValue(json, PlantingRecord.class);

    /*
     * Si la fecha de siembra o la fecha de cosecha NO esta
     * definida, o ambas NO esta definidas, la aplicacion del
     * lado servidor retorna el mensaje HTTP 400 (Bad request)
     * junto con el mensaje "Las fechas deben estar definidas"
     * y no se realiza la operacion solicitada
     */
    if (modifiedPlantingRecord.getSeedDate() == null || modifiedPlantingRecord.getHarvestDate() == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.INDEFINITE_DATES))).build();
    }

    /*
     * Si la fecha de siembra es mayor o igual a la fecha de
     * cosecha del registro de plantacion a modificar, la
     * aplicacion del lado servidor retorna el mensaje
     * HTTP 400 (Bad request) junto con el mensaje "La fecha
     * de siembra no debe ser mayor o igual a la fecha de
     * cosecha" y no se realiza la operacion solicitada
     */
    if (plantingRecordService.checkDateOverlap(modifiedPlantingRecord.getSeedDate(), modifiedPlantingRecord.getHarvestDate())) {
      return Response
        .status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.OVERLAP_BETWEEN_SEED_DATE_AND_HARVEST_DATE))).build();
    }

    /*
     * Si la parcela del registro de plantacion a modificar NO
     * esta definida, la aplicacion del lado servidor retorna
     * el mensaje HTTP 400 (Bad request) junto con el mensaje
     * "La parcela debe estar definida" y no se realiza la
     * operacion solicitada
     */
    if (modifiedPlantingRecord.getParcel() == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.INDEFINITE_PARCEL))).build();
    }

    /*
     * Si el cultivo del registro de plantacion a modificar NO
     * esta definido, la aplicacion del lado servidor retorna
     * el mensaje HTTP 400 (Bad request) junto con el mensaje
     * "El cultivo debe estar definido" y no se realiza la
     * operacion solicitada
     */
    if (modifiedPlantingRecord.getCrop() == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.INDEFINITE_CROP))).build();
    }

    /*
     * Si existe el registro de plantacion anterior al
     * registro de plantacion a modificar, y si la fecha de
     * cosecha del registro de plantacion anterior es mayor
     * o igual a la fecha de siembra del registro de
     * plantacion a modificar, la aplicacion del lado
     * servidor retorna el mensaje HTTP 400 (Bad request)
     * junto con el mensaje "La fecha de siembra no debe ser
     * anterior o igual a la fecha de cosecha del registro de
     * plantacion inmediatamente anterior" y no se realiza la
     * operacion solicitada
     */
    if (plantingRecordService.checkPrevious(modifiedPlantingRecord.getId(), modifiedPlantingRecord.getParcel())) {
      PlantingRecord previousPlantingRecord = plantingRecordService.find(modifiedPlantingRecord.getId() - 1, modifiedPlantingRecord.getParcel());

      if (plantingRecordService.checkDateOverlap(previousPlantingRecord.getHarvestDate(), modifiedPlantingRecord.getSeedDate())) {
        return Response
          .status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.DATE_OVERLAP_WITH_PREVIOUS_PLANTING_RECORD))).build();
      }

    }

    /*
     * Si existe el registro de plantacion siguiente al registro
     * de plantacion a modificar, y si la fecha de cosecha de
     * este es mayor o igual a la fecha de siembra del registro
     * de plantacion siguiente, la aplicacion del lado servidor
     * retorna el mensaje HTTP 400 (Bad request) junto con el
     * mensaje "La fecha de cosecha no debe ser posterior o
     * igual a la fecha de siembra del registro de plantacion
     * inmediatamente siguiente" y no se realiza la operacion
     * solicitada
     */
    if (plantingRecordService.checkNext(modifiedPlantingRecord.getId(), modifiedPlantingRecord.getParcel())) {
      PlantingRecord nextPlantingRecord = plantingRecordService.find(modifiedPlantingRecord.getId() + 1, modifiedPlantingRecord.getParcel());

      if (plantingRecordService.checkDateOverlap(modifiedPlantingRecord.getHarvestDate(), nextPlantingRecord.getSeedDate())) {
        return Response
          .status(Response.Status.BAD_REQUEST).entity(mapper.writeValueAsString(new ErrorResponse(ReasonError.DATE_OVERLAP_WITH_NEXT_PLANTING_RECORD))).build();
      }

    }

    /*
     * Se establece el estado del registro de plantacion a
     * modificar en base a la fecha de cosecha de su cultivo
     */
    modifiedPlantingRecord.setStatus(statusService.calculateStatus(modifiedPlantingRecord.getHarvestDate()));

    /*
     * Si el valor del encabezado de autorizacion de la peticion HTTP
     * dada, tiene un JWT valido, la aplicacion del lado servidor
     * devuelve el mensaje HTTP 200 (Ok) junto con los datos que el
     * cliente solicito actualizar
     */
    return Response.status(Response.Status.OK).entity(mapper.writeValueAsString(plantingRecordService.modify(userId, plantingRecordId, modifiedPlantingRecord))).build();
  }

  @GET
  @Path("/suggestedIrrigation/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getSuggestedIrrigation(@PathParam("id") int id) throws IOException {
    PlantingRecord givenPlantingRecord = plantingRecordService.find(id);
    Parcel parcel = givenPlantingRecord.getParcel();
    double suggestedIrrigationToday = 0.0;
    double tomorrowPrecipitation = 0.0;

    ClimateRecord yesterdayClimateLog = null;
    double yesterdayEto = 0.0;
    double yesterdayEtc = 0.0;
    double extraterrestrialSolarRadiation = 0.0;
    double maximumInsolation = 0.0;

    /*
     * Fecha actual del sistema
     */
    Calendar currentDate = Calendar.getInstance();

    /*
     * Fecha del dia de ma??ana para solicitar la precipitacion
     * del dia de ma??ana
     */
    Calendar tomorrowDate = UtilDate.getTomorrowDate();

    /*
     * Solicita el registro del clima del dia de ma??ana
     */
    ClimateRecord tomorrowClimateLog = ClimateClient.getForecast(parcel, (tomorrowDate.getTimeInMillis() / 1000));

    /*
     * El atributo precip del modelo de datos ClimateRecord representa
     * la precipitacion del dia en milimetros. La unidad en la que se
     * mide este dato corresponde a la API Visual Crossing Weather y
     * al grupo de unidades en el que se le solicita datos metereologicos.
     */
    tomorrowPrecipitation = tomorrowClimateLog.getPrecip();

    /*
     * Fecha del dia inmediatamente anterior a la fecha
     * actual del sistema
     */
    Calendar yesterdayDate = UtilDate.getYesterdayDate();

    /*
     * Cantidad total de agua utilizada en los riegos
     * realizados en el dia de hoy
     */
    double totalIrrigationWaterToday = irrigationRecordService.getTotalWaterIrrigationToday(parcel);

    /*
     * Si el registro climatico del dia de ayer no existe en
     * la base de datos, se lo tiene que pedir y se lo tiene
     * que persistir en la base de datos subyacente
     */
    if (!(climateRecordServiceBean.exist(yesterdayDate, parcel))) {
      yesterdayClimateLog = ClimateClient.getForecast(parcel, (yesterdayDate.getTimeInMillis() / 1000));

      extraterrestrialSolarRadiation = solarService.getRadiation(yesterdayDate.get(Calendar.MONTH), parcel.getLatitude());
      maximumInsolation = insolationService.getInsolation(yesterdayDate.get(Calendar.MONTH), parcel.getLatitude());

      /*
       * Evapotranspiracion del cultivo de referencia (ETo) con las
       * condiciones climaticas del registro climatico del dia de ayer
       */
      yesterdayEto = Eto.getEto(yesterdayClimateLog.getMinimumTemperature(), yesterdayClimateLog.getMaximumTemperature(), yesterdayClimateLog.getAtmosphericPressure(), yesterdayClimateLog.getWindSpeed(),
        yesterdayClimateLog.getDewPoint(), extraterrestrialSolarRadiation, maximumInsolation, yesterdayClimateLog.getCloudCover());

      /*
       * Evapotranspiracion del cultivo bajo condiciones esntandar (ETc)
       * del cultivo dado con la ETo del dia de ayer
       */
      yesterdayEtc = cropService.getKc(givenPlantingRecord.getCrop(), givenPlantingRecord.getSeedDate()) * yesterdayEto;

      yesterdayClimateLog.setEto(yesterdayEto);
      yesterdayClimateLog.setEtc(yesterdayEtc);
      yesterdayClimateLog.setParcel(parcel);
      climateRecordServiceBean.create(yesterdayClimateLog);
    }

    /*
     * Recupera el registro climatico de la parcela
     * de la fecha anterior a la fecha actual
     */
    ClimateRecord climateLog = climateRecordServiceBean.find(yesterdayDate, parcel);
    suggestedIrrigationToday = WaterMath.getSuggestedIrrigation(parcel.getHectares(), climateLog.getEtc(), climateLog.getEto(), climateLog.getPrecip(), climateLog.getWaterAccumulated(), totalIrrigationWaterToday);

    IrrigationRecord newIrrigationRecord = new IrrigationRecord();
    newIrrigationRecord.setDate(currentDate);
    newIrrigationRecord.setSuggestedIrrigation(suggestedIrrigationToday);
    newIrrigationRecord.setTomorrowPrecipitation(WaterMath.truncateToThreeDecimals(tomorrowPrecipitation));
    newIrrigationRecord.setParcel(parcel);

    return mapper.writeValueAsString(newIrrigationRecord);
  }

  // TODO: Comprobar para que sirve esto
  @GET
  @Path("/findCurrentPlantingRecord/{parcelId}")
  @Produces(MediaType.APPLICATION_JSON)
  public String findCurrentPlantingRecord(@PathParam("parcelId") int parcelId) throws IOException {
    Parcel givenParcel = serviceParcel.find(parcelId);
    PlantingRecord plantingRecord = plantingRecordService.findInDevelopment(givenParcel);
    return mapper.writeValueAsString(plantingRecord);
  }

  // TODO: Comprobar para que sirve esto
  @GET
  @Path("/findNewestPlantingRecord/{parcelId}")
  @Produces(MediaType.APPLICATION_JSON)
  public String findNewestPlantingRecord(@PathParam("parcelId") int parcelId) throws IOException {
    Parcel givenParcel = serviceParcel.find(parcelId);
    PlantingRecord newestPlantingRecord = plantingRecordService.findLastFinished(givenParcel);
    return mapper.writeValueAsString(newestPlantingRecord);
  }

  // TODO: Comprobar para que sirve esto
  // @GET
  // @Path("/checkStageCropLife/{idCrop}")
  // @Produces(MediaType.APPLICATION_JSON)
  // public String checkStageCropLife(@PathParam("idCrop") int idCrop, @QueryParam("fechaSiembra") String fechaSiembra,
  // @QueryParam("fechaCosecha") String fechaCosecha) throws IOException, ParseException {
  //   Crop choosenCrop = cropService.find(idCrop);
  //
  //   SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
  //   Calendar seedDate = Calendar.getInstance();
  //   Calendar harvestDate = Calendar.getInstance();
  //
  //   /*
  //    * Fechas convertidas de String a Date
  //    */
  //   Date dateSeedDate = new Date(dateFormatter.parse(fechaSiembra).getTime());
  //   Date dateHarvestDate = new Date(dateFormatter.parse(fechaCosecha).getTime());
  //
  //   seedDate.set(dateSeedDate.getYear(), dateSeedDate.getMonth(), dateSeedDate.getDate());
  //   harvestDate.set(dateHarvestDate.getYear(), dateHarvestDate.getMonth(), dateHarvestDate.getDate());
  //
  //   /*
  //    * Si la diferencia en dias entre la fecha de siembra
  //    * y la fecha de cosecha ingresadas es mayor a la cantidad
  //    * total de dias de vida que vive el cultivo dado, retorna
  //    * el cultivo dado como "error"
  //    */
  //   if (excessStageLife(choosenCrop, seedDate, harvestDate)) {
  //     return mapper.writeValueAsString(choosenCrop);
  //   }
  //
  //   return mapper.writeValueAsString(null);
  // }

  // TODO: Comprobar para que sirve esto
  /**
   * @param  givenCrop
   * @param  seedDate    [fecha de siembra]
   * @param  harvestDate [fecha de cosecha]
   * @return verdadero si la diferencia en dias entre la fecha de
   * siembra y la fecha de cosecha es mayor que la cantidad de dias
   * que dura la etapa de vida del cultivo dado, en caso contrario
   * retorna falso
   */
  private boolean excessStageLife(Crop givenCrop, Calendar seedDate, Calendar harvestDate) {
    int differenceBetweenDates = 0;
    int totalDaysLife = givenCrop.getInitialStage() + givenCrop.getDevelopmentStage() + givenCrop.getMiddleStage() + givenCrop.getFinalStage();

    /*
     * Si los a??os de la fecha de siembra y de la fecha de cosecha
     * son iguales, entonces la diferencia en dias entre ambas fechas
     * se calcula de forma directa
     */
    if ((seedDate.get(Calendar.YEAR)) == (harvestDate.get(Calendar.YEAR))) {
      differenceBetweenDates = harvestDate.get(Calendar.DAY_OF_YEAR) - seedDate.get(Calendar.DAY_OF_YEAR);
    }

    /*
     * Si la diferencia entre los a??os de la fecha de siembra y la fecha
     * de cosecha ingresadas es igual a uno, la diferencia en dias entre
     * ambas fechas se calcula de la siguiente forma:
     *
     * Diferencia en dias entre ambas fechas = (365 - numero del dia en el
     * a??o de la fecha de siembra + 1) - numero del dia en el a??o de la
     * fecha de cosecha
     */
    if ((harvestDate.get(Calendar.YEAR) - seedDate.get(Calendar.YEAR)) == 1) {
      differenceBetweenDates = (365 - seedDate.get(Calendar.DAY_OF_YEAR) + 1) + harvestDate.get(Calendar.DAY_OF_YEAR);
    }

    /*
     * Si la diferencia entre los a??os de la fecha de siembra y la fecha
     * de cosecha ingresadas es mayor a uno, la diferencia en dias entre
     * ambas fechas se calcula de la siguiente forma:
     *
     * Diferencia en dias entre ambas fechas = (a??o de la fecha de cosecha -
     * a??o de la fecha de siembra) * 365 - (365 - numero del dia en el a??o
     * de la fecha de siembra + 1) - numero del dia en el a??o de la fecha
     * de cosecha
     */
    if ((harvestDate.get(Calendar.YEAR) - seedDate.get(Calendar.YEAR)) > 1) {
      differenceBetweenDates = ((harvestDate.get(Calendar.YEAR) - seedDate.get(Calendar.YEAR)) * 365) - (365 - seedDate.get(Calendar.DAY_OF_YEAR) + 1) - harvestDate.get(Calendar.DAY_OF_YEAR);
    }

    /*
     * Si la diferencia en dias entre la fecha de siembra
     * y la fecha de cosecha ingresadas es mayor a la cantidad
     * total de dias de vida que vive el cultivo dado, retorna
     * verdadero
     */
    if (differenceBetweenDates > totalDaysLife) {
      return true;
    }

    return false;
  }

}
